package org.mvasylchuk.pfcc.report;

import org.mvasylchuk.pfcc.report.dto.PeriodReportData;

import java.time.LocalDate;

public interface ReportRepository {
    PeriodReportData getPeriodReport(Long userId, LocalDate from, LocalDate to);
}
