package org.mvasylchuk.pfcc.securitytoken;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties.JobConfiguration.DropOutdatedSecurityTokensConfiguration;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "pfcc.jobs.drop-outdated-security-tokens.enabled", havingValue = "true")
public class SecurityTokenCleanupJobConfiguration {
    public static final String JOB_NAME = "drop-outdated-security-tokens";
    public static final String DROP_OUTDATED_SECURITY_TOKENS_STEP_NAME = "drop-outdated-security-tokens_step";

    private final PfccAppConfigurationProperties conf;
    private final SecurityTokenJpaRepository tokenRepository;
    private final PlatformTransactionManager ptm;
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "${pfcc.jobs.drop-outdated-security-tokens.cron}")
    protected void runDropOutdatedSecurityTokensJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = dropOutdatedSecurityTokensJob(tokenRepository,
                jobRepository,
                ptm,
                conf.jobs.dropOutdatedSecurityTokens);

        JobParameters parameters = new JobParametersBuilder()
                // Adding a parameter, which will have different value on each run
                // Otherwise a job will not start
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(job, parameters);
    }

    protected Step dropOutdatedSecurityTokensStep(SecurityTokenJpaRepository tokenRepository,
                                                  JobRepository jobRepository,
                                                  PlatformTransactionManager ptm,
                                                  DropOutdatedSecurityTokensConfiguration conf) {
        DropOutdatedSecurityTokensTasklet tasklet = new DropOutdatedSecurityTokensTasklet(
                conf,
                tokenRepository);

        return new StepBuilder(DROP_OUTDATED_SECURITY_TOKENS_STEP_NAME, jobRepository)
                .tasklet(tasklet, ptm)
                .build();
    }

    protected Job dropOutdatedSecurityTokensJob(SecurityTokenJpaRepository tokenRepository,
                                                JobRepository jobRepository,
                                                PlatformTransactionManager ptm,
                                                DropOutdatedSecurityTokensConfiguration conf) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(dropOutdatedSecurityTokensStep(tokenRepository, jobRepository, ptm, conf))
                .build();
    }

    @Slf4j
    @RequiredArgsConstructor
    protected static class DropOutdatedSecurityTokensTasklet implements Tasklet, StepExecutionListener {
        private final DropOutdatedSecurityTokensConfiguration conf;
        private final SecurityTokenJpaRepository repository;

        private int deletedCount;

        @Override
        @Transactional
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            LocalDateTime bound = LocalDateTime.now().minus(conf.outdatedSecurityTokenTtl);

            this.deletedCount = repository.deleteOutdated(bound);

            return RepeatStatus.FINISHED;
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("Deleted outdated security tokens: {}", this.deletedCount);
            return ExitStatus.COMPLETED;
        }
    }
}
