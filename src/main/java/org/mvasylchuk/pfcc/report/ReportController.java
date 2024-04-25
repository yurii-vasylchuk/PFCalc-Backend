package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportFacade reportFacade;

    @GetMapping(
            value = "/period",
            produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileSystemResource> generateReport(@RequestParam("from") LocalDate from,
                                                             @RequestParam("to") LocalDate to,
                                                             @AuthenticationPrincipal Principal user) throws IOException {
        if (!(user instanceof PfccAuthToken)) {
            throw new IllegalArgumentException("Unable to handle %s auth".formatted(user));
        }

        Long userId = ((PfccAuthToken) user).id();

        Path reportPath = reportFacade.generatePeriodReport(userId, from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"report_%tF_%tF.pdf\"".formatted(from, to))
                .body(new FileSystemResource(reportPath));
    }
}
