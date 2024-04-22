package org.mvasylchuk.pfcc.report;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

@IntegrationTest
class ReportFacadeTest {
    @Autowired
    ReportFacade underTest;

    @Test
    @Disabled("")
    @FlywayTest(locationsForMigrate = "migration/ReportFacadeTest/testReportGenerated")
    void generatePeriodReport() throws IOException {
        underTest.generatePeriodReport(50001L, LocalDate.of(2024, Month.MARCH, 25),
                LocalDate.of(2024, Month.MARCH, 30));
    }
}
