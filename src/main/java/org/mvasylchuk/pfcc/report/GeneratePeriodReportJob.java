package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.report.dto.ReportStatus;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.mvasylchuk.pfcc.user.UserRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GeneratePeriodReportJob {
    public static final String JOB_NAME = "generate-period-report";
    public static final String USERS_IDS_PARAM = "USERS_IDS";
    public static final String FROM_DATE_PARAM = "FROM";
    public static final String TO_DATE_PARAM = "TO";
    public static final String USERS_IDS_PARAM_SEPARATOR = ",";
    private static final String STEP_NAME = JOB_NAME + "_step";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;

    private static <T> Iterator<T> emptyIterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }

    @Bean(JOB_NAME)
    protected Job generatePeriodReportJob(JobRepository jobRepository,
                                          @Qualifier(STEP_NAME) Step step) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step)
                .build();
    }

    @Bean(STEP_NAME)
    protected Step generatePeriodReportStep(GeneratePeriodReportTaskGenerator reader,
                                            PeriodReportGenerator processor,
                                            PeriodReportWriter writer) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<PeriodReportGenerationTask, PeriodReportGenerationResult>chunk(1, ptm)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .allowStartIfComplete(true)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected GeneratePeriodReportTaskGenerator reader(ReportJooqRepository queryRepository,
                                                       ReportRepository commandRepository,
                                                       UserRepository userRepository) {
        return new GeneratePeriodReportTaskGenerator(queryRepository, commandRepository, userRepository);
    }

    @Bean
    protected PeriodReportGenerator processor(ReportFacade facade) {
        return new PeriodReportGenerator(facade);
    }

    @Bean
    protected PeriodReportWriter writer(ReportRepository repository) {
        return new PeriodReportWriter(repository);
    }

    @RequiredArgsConstructor
    protected static class PeriodReportWriter implements ItemWriter<PeriodReportGenerationResult> {
        private final ReportRepository repository;

        @Override
        public void write(Chunk<? extends PeriodReportGenerationResult> items) {
            for (PeriodReportGenerationResult res : items) {
                repository.findById(res.reportId)
                        .ifPresentOrElse(
                                report -> {
                                    report.setPath(res.filePath);
                                    report.setStatus(ReportStatus.GENERATED);
                                    repository.save(report);
                                },
                                () -> log.error("Unable to save report generation results: report #{} is not found",
                                                res.reportId
                                )
                        );
            }
        }
    }

    @RequiredArgsConstructor
    protected static class PeriodReportGenerator implements ItemProcessor<PeriodReportGenerationTask, PeriodReportGenerationResult> {
        private final ReportFacade reportFacade;

        @Override
        public PeriodReportGenerationResult process(PeriodReportGenerationTask item) throws Exception {
            Path path = reportFacade.generatePeriodReport(item.userId, item.from, item.to);
            return new PeriodReportGenerationResult(item.reportId(), path);
        }
    }

    @RequiredArgsConstructor
    protected static class GeneratePeriodReportTaskGenerator implements ItemReader<PeriodReportGenerationTask>, StepExecutionListener {
        private final ReportJooqRepository queryRepository;
        private final ReportRepository commandRepository;
        private final UserRepository userRepository;
        private Iterator<PeriodReportGenerationTask> tasks;

        @Override
        public void beforeStep(StepExecution stepExecution) {
            JobParameters jobParameters = stepExecution.getJobParameters();
            String idsParamStr = jobParameters.getString(USERS_IDS_PARAM);
            LocalDate fromDate = jobParameters.getLocalDate(FROM_DATE_PARAM);
            LocalDate toDate = jobParameters.getLocalDate(TO_DATE_PARAM);

            if (fromDate == null || toDate == null) {
                log.warn("Required params isn't set: FROM: {}, TO: {}", fromDate, toDate);
                this.tasks = emptyIterator();
                return;
            }

            List<Long> ids;
            if (idsParamStr == null) {
                ids = queryRepository.getAllUsersIdsForPeriodReportGeneration();
            } else {
                ids = Arrays.stream(idsParamStr.split(USERS_IDS_PARAM_SEPARATOR)).map(Long::parseLong).toList();
            }

            this.tasks = ids.stream()
                    .map(id -> new PeriodReportGenerationTask(id, fromDate, toDate, null))
                    .iterator();
        }

        @Override
        public PeriodReportGenerationTask read() {
            if (!tasks.hasNext()) {
                return null;
            }

            PeriodReportGenerationTask task = tasks.next();

            UserEntity user = userRepository.findById(task.userId)
                    .orElseThrow(() -> new PfccException("Can't find user", ApiErrorCode.USER_IS_NOT_FOUND));
            ReportEntity report = commandRepository.save(ReportEntity.periodReport(user, task.from, task.to));

            return task.withReportId(report.getId());
        }
    }

    protected record PeriodReportGenerationTask(Long userId, LocalDate from, LocalDate to, Long reportId) {
        public PeriodReportGenerationTask withReportId(Long reportId) {
            return new PeriodReportGenerationTask(userId, from, to, reportId);
        }
    }

    protected record PeriodReportGenerationResult(Long reportId, Path filePath) {
    }
}
