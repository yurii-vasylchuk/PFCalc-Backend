package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.common.ThymeleafPfcalcUtil;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData;
import org.mvasylchuk.pfcc.report.dto.ReportDto;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportFacade {
    private static final String PERIOD_REPORT_TEMPLATE = "weeklyReport.html";
    private final ReportJooqRepository reportJooqRepository;
    private final TemplateEngine templateEngine;
    private final PfccAppConfigurationProperties conf;
    private final UserService userService;

    public Path generatePeriodReport(Long userId, LocalDate from, LocalDate to) throws IOException {
        PeriodReportData reportData = reportJooqRepository.getPeriodReport(userId, from, to);

        Context context = getContext(reportData);

        String clearUserName = reportData.getUserName()
                .replaceAll("[^\\p{LD}]", "-")
                .replaceAll("-+", "-");

        String filename = "WeeklyReport_%s_%tF_%tF".formatted(clearUserName, from, to);
        Path pdfFilePath = conf.reports.storePath.resolve(filename + ".pdf");
        Path outputHtmlPath = conf.reports.storePath.resolve(filename + ".html");

        if (Files.exists(pdfFilePath)) {
            Files.delete(pdfFilePath);
        }

        if (Files.exists(outputHtmlPath)) {
            Files.delete(outputHtmlPath);
        }

        Writer writer = new FileWriter(outputHtmlPath.toString(), false);

        templateEngine.process(PERIOD_REPORT_TEMPLATE, context, writer);

        Process process = Runtime.getRuntime().exec(
                new String[]{
                        conf.reports.chromeExecutable,
                        "--headless",
                        "--disable-gpu",
                        "--print-to-pdf=%s".formatted(pdfFilePath),
                        outputHtmlPath.toString(),
                        "--no-pdf-header-footer"
                });
        try {
            process.waitFor(conf.reports.renderTimeout.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            String msg = "Unable to convert report to PDF: Timeout exception";
            log.error(msg);
            process.destroy();
            throw new IllegalStateException(msg, e);
        }

        if (process.exitValue() != 0) {
            String msg = "Unable to print to PDF: Exit value = %s".formatted(process.exitValue());
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        Files.delete(outputHtmlPath);

        return pdfFilePath;
    }

    private Context getContext(PeriodReportData reportData) {
        return new Context(
                reportData.getUserLanguage().getLocale(),
                Map.ofEntries(
                        entry("userName", reportData.getUserName()),
                        entry("start", reportData.getStartDate()),
                        entry("end", reportData.getEndDate()),
                        entry("average", reportData.getDailyAverage()),
                        entry("total", reportData.getTotalPfcc()),
                        entry("max", reportData.getMaxDailyPfcc()),
                        entry("min", reportData.getMinDailyPfcc()),
                        entry("aim", reportData.getDailyAim()),
                        entry("days", reportData.getDays()),
                        entry("percent", reportData.getPercentOfAim()),
                        entry("util", new ThymeleafPfcalcUtil())
                ));
    }

    public Page<ReportDto> getUserReports(Long userId, Integer page, Integer pageSize) {
        return reportJooqRepository.getUserReportsPage(userId, page, pageSize);
    }

    public ReportDto getReport(Long reportId) {
        ReportDto report = reportJooqRepository.getReport(reportId);

        Long currentUserId = userService.currentUser().getId();

        if (!Objects.equals(currentUserId, report.userId())) {
            throw new PfccException(
                    "Requested report #%d does not belong to authenticated user #%d".formatted(reportId, currentUserId),
                    ApiErrorCode.SECURITY);
        }

        return report;
    }
}
