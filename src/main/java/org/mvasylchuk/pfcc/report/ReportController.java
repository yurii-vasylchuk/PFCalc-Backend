package org.mvasylchuk.pfcc.report;

import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.common.dto.BaseResponse;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.mvasylchuk.pfcc.report.dto.ReportDto;
import org.mvasylchuk.pfcc.report.dto.ReportStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final JobLauncher jobLauncher;
    private final Job periodReportJob;
    private final ReportFacade reportFacade;

    public ReportController(JobLauncher jobLauncher,
                            @Qualifier(GeneratePeriodReportJob.JOB_NAME) Job periodReportJob, ReportFacade reportFacade) {
        this.jobLauncher = jobLauncher;
        this.periodReportJob = periodReportJob;
        this.reportFacade = reportFacade;
    }

    @PostMapping("/period")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> generateReport(@RequestParam("from") LocalDate from,
                                             @RequestParam("to") LocalDate to,
                                             @AuthenticationPrincipal PfccAuthToken user) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Long userId = user.id();

        jobLauncher.run(periodReportJob, new JobParametersBuilder()
                .addLocalDateTime("now", LocalDateTime.now())
                .addLocalDate(GeneratePeriodReportJob.FROM_DATE_PARAM, from)
                .addLocalDate(GeneratePeriodReportJob.TO_DATE_PARAM, to)
                .addString(GeneratePeriodReportJob.USERS_IDS_PARAM, String.valueOf(userId))
                .toJobParameters());

        return BaseResponse.success(null);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Page<ReportDto>> getReports(@RequestParam(name = "page",
                                                                  required = false,
                                                                  defaultValue = "0") Integer page,
                                                    @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                    @AuthenticationPrincipal(errorOnInvalidType = true) PfccAuthToken user) {
        Long id = user.id();
        return BaseResponse.success(reportFacade.getUserReports(id, page, pageSize));
    }

    @GetMapping(value = "/{id}/file",
                produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileSystemResource> getReportFile(@PathVariable("id") Long reportId) {
        ReportDto report = reportFacade.getReport(reportId);
        if (report.status() == ReportStatus.CORRUPTED) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(report.name() + ".pdf", UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(report.path()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<Void> deleteReport(@PathVariable("id") Long reportId) {
        reportFacade.deleteReport(reportId);
        return BaseResponse.success(null);
    }
}
