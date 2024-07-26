package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.report.dto.ReportStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeleteOutdatedReportsJob {
    public static final String JOB_NAME = "delete-outdated-reports-job";
    private static final String STEP_NAME = JOB_NAME + "_step";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobLauncher jobLauncher;
    private final ListableJobLocator jobLocator;

    @Scheduled(cron = "${pfcc.jobs.drop-outdated-reports.cron}")
    protected void runByCron() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, NoSuchJobException {
        Job job = jobLocator.getJob(JOB_NAME);
        JobParameters parameters = new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(job, parameters);
    }

    @Bean(JOB_NAME)
    protected Job deleteOutdatedReportsJob(@Qualifier(STEP_NAME) Step step) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step)
                .build();
    }

    @Bean(STEP_NAME)
    protected Step deleteOutdatedReportsStep(DeleteOutdatedReportsTasklet tasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(tasklet, ptm)
                .build();
    }

    @Component
    @RequiredArgsConstructor
    protected static class DeleteOutdatedReportsTasklet implements Tasklet {
        private final ReportRepository reportRepository;
        private final PfccAppConfigurationProperties conf;

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            Collection<ReportEntity> outdatedReports = reportRepository.findOlderThan(LocalDateTime.now().minus(conf.jobs.dropOutdatedReports.ttl));

            List<ReportEntity> toSave = new ArrayList<>();
            List<ReportEntity> toDelete = new ArrayList<>();

            for (ReportEntity report : outdatedReports) {
                if (!Files.exists(report.getPath())) {
                    log.error("No file found for outdated report {}", report);
                    report.setStatus(ReportStatus.CORRUPTED);
                    toSave.add(report);
                    continue;
                }

                try {
                    log.info("Deleting report's #{} file {}", report.getId(), report.getFilePath());
                    Files.delete(report.getPath());
                } catch (IOException e) {
                    log.error("Failed to delete report's file; Report: %s".formatted(report), e);
                    report.setStatus(ReportStatus.CORRUPTED);
                    toSave.add(report);
                    continue;
                }

                log.info("Report will be deleted: {}", report);
                toDelete.add(report);
            }

            reportRepository.saveAll(toSave);
            reportRepository.deleteAll(toDelete);

            log.info("Totally deleted reports: {}", toDelete.size());
            log.info("Totally corrupted reports: {}", toSave.size());

            return RepeatStatus.FINISHED;
        }
    }
}
