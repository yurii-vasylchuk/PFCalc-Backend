package org.mvasylchuk.pfcc.report.dto;

import java.util.List;

public enum ReportStatus {
    INITIALIZED, GENERATED;
    public static final List<ReportStatus> READY_STATUSES = List.of(GENERATED);
}
