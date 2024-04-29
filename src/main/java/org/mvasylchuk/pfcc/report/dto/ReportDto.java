package org.mvasylchuk.pfcc.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ReportDto(Long id,
                        String name,
                        ReportStatus status,
                        ReportType type,
                        @JsonIgnore Long userId,
                        @JsonIgnore String path) {
}
