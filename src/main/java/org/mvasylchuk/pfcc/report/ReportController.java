package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@RestController("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportFacade reportFacade;

    @GetMapping(value = "/{from}/{to}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("isAuthenticated()")
    public InputStream generateReport(@PathVariable("from") LocalDate from,
                                      @PathVariable("to") LocalDate to,
                                      Principal user) throws IOException {
        if (!(user instanceof PfccAuthToken)) {
            throw new IllegalArgumentException("Unable to handle %s auth".formatted(user));
        }

        Long userId = ((PfccAuthToken) user).id();

        Path reportPath = reportFacade.generatePeriodReport(userId, from, to);

        return new FileInputStream(reportPath.toFile());
    }
}
