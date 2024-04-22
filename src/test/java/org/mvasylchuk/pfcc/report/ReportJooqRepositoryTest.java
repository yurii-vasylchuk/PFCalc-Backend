package org.mvasylchuk.pfcc.report;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Percentage;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.pfcc.IntegrationTest;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@IntegrationTest
class ReportJooqRepositoryTest {
    public static final Percentage ACCEPTABLE_DEVIATION = Percentage.withPercentage(0.001);
    @Autowired
    ReportJooqRepository underTest;

    @Test
    @FlywayTest(locationsForMigrate = "migration/ReportJooqRepositoryTest/singleProduct")
    void singleProduct_shouldBeCalculatedCorrectly() {
        PeriodReportData actual = underTest.getPeriodReport(1L,
                LocalDate.of(2024, Month.JANUARY, 5),
                LocalDate.of(2024, Month.JANUARY, 5));

        PfccDto total = actual.getTotalPfcc();
        assertThat(total.getProtein()).isEqualByComparingTo("48");
        assertThat(total.getFat()).isEqualByComparingTo("12");
        assertThat(total.getCarbohydrates()).isEqualByComparingTo("24");
        assertThat(total.getCalories()).isEqualByComparingTo("480");

        PfccDto average = actual.getDailyAverage();
        assertThat(average.getProtein()).isEqualByComparingTo("48");
        assertThat(average.getFat()).isEqualByComparingTo("12");
        assertThat(average.getCarbohydrates()).isEqualByComparingTo("24");
        assertThat(average.getCalories()).isEqualByComparingTo("480");

        assertThat(actual.getDays())
                .containsOnlyKeys(LocalDate.of(2024, Month.JANUARY, 5));

        PeriodReportData.DailyReportData day = actual.getDays().get(LocalDate.of(2024, Month.JANUARY, 5));

        assertThat(day.getMeals())
                .hasSize(1)
                .allSatisfy(meal -> {
                    assertThat(meal.name()).isEqualTo("Product 1 u1");
                    assertThat(meal.date()).isEqualTo("2024-01-05");
                    assertThat(meal.weight()).isEqualByComparingTo("200");
                    assertThat(meal.pfcc().getProtein()).isEqualByComparingTo("48");
                    assertThat(meal.pfcc().getFat()).isEqualByComparingTo("12");
                    assertThat(meal.pfcc().getCarbohydrates()).isEqualByComparingTo("24");
                    assertThat(meal.pfcc().getCalories()).isEqualByComparingTo("480");
                });
    }

    @Test
    @FlywayTest(locationsForMigrate = "migration/ReportJooqRepositoryTest/singleRecipe")
    void singleRecipe_shouldBeCalculatedCorrectly() {
        PeriodReportData actual = underTest.getPeriodReport(1L,
                LocalDate.of(2024, Month.JANUARY, 5),
                LocalDate.of(2024, Month.JANUARY, 5));

        PfccDto total = actual.getTotalPfcc();
        assertThat(total.getProtein()).isEqualByComparingTo("36");
        assertThat(total.getFat()).isEqualByComparingTo("22");
        assertThat(total.getCarbohydrates()).isEqualByComparingTo("30");
        assertThat(total.getCalories()).isEqualByComparingTo("586");

        PfccDto average = actual.getDailyAverage();
        assertThat(average.getProtein()).isEqualByComparingTo("36");
        assertThat(average.getFat()).isEqualByComparingTo("22");
        assertThat(average.getCarbohydrates()).isEqualByComparingTo("30");
        assertThat(average.getCalories()).isEqualByComparingTo("586");

        assertThat(actual.getDays())
                .containsOnlyKeys(LocalDate.of(2024, Month.JANUARY, 5));

        PeriodReportData.DailyReportData day = actual.getDays().get(LocalDate.of(2024, Month.JANUARY, 5));

        assertThat(day.getMeals())
                .hasSize(1)
                .allSatisfy(meal -> {
                    assertThat(meal.name()).isEqualTo("Recipe 1");
                    assertThat(meal.date()).isEqualTo("2024-01-05");
                    assertThat(meal.weight()).isEqualByComparingTo("200");
                    assertThat(meal.pfcc().getProtein()).isEqualByComparingTo("36");
                    assertThat(meal.pfcc().getFat()).isEqualByComparingTo("22");
                    assertThat(meal.pfcc().getCarbohydrates()).isEqualByComparingTo("30");
                    assertThat(meal.pfcc().getCalories()).isEqualByComparingTo("586");
                });

        assertThat(day.getDeconstructedMeals())
                .hasSize(3)
                .satisfiesOnlyOnce(meal -> {
                    assertThat(meal.name()).isEqualTo("Product 2");
                    assertThat(meal.date()).isEqualTo("2024-01-05");
                    assertThat(meal.weight()).isCloseTo(new BigDecimal("33.3333"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getProtein()).isCloseTo(new BigDecimal("8"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getFat()).isCloseTo(new BigDecimal("2"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getCarbohydrates()).isCloseTo(new BigDecimal("4"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getCalories()).isCloseTo(new BigDecimal("80"), ACCEPTABLE_DEVIATION);
                })
                .satisfiesOnlyOnce(meal -> {
                    assertThat(meal.name()).isEqualTo("Product 3");
                    assertThat(meal.date()).isEqualTo("2024-01-05");
                    assertThat(meal.weight()).isCloseTo(new BigDecimal("66.6667"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getProtein()).isCloseTo(new BigDecimal("12"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getFat()).isCloseTo(new BigDecimal("6"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getCarbohydrates()).isCloseTo(new BigDecimal("4"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getCalories()).isCloseTo(new BigDecimal("126"), ACCEPTABLE_DEVIATION);
                })
                .satisfiesOnlyOnce(meal -> {
                    assertThat(meal.name()).isEqualTo("Product 4");
                    assertThat(meal.date()).isEqualTo("2024-01-05");
                    assertThat(meal.weight()).isEqualByComparingTo("100");
                    assertThat(meal.pfcc().getProtein()).isCloseTo(new BigDecimal("10"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getFat()).isCloseTo(new BigDecimal("14"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getCarbohydrates()).isCloseTo(new BigDecimal("22"), ACCEPTABLE_DEVIATION);
                    assertThat(meal.pfcc().getCalories()).isCloseTo(new BigDecimal("300"), ACCEPTABLE_DEVIATION);
                });
    }

    @Test
    void singleDish_shouldBeCalculatedCorrectly() {
        //TODO: implement
    }

    @Test
    void whenSameDeconstructedProductRepeats_thenItShouldBeCombined() {
        //TODO: implement
    }
}
