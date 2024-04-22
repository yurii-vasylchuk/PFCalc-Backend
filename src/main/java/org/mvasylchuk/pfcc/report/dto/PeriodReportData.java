package org.mvasylchuk.pfcc.report.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.user.Language;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Getter
@ToString
@EqualsAndHashCode
public class PeriodReportData {
    private final String userName;
    private final Language userLanguage;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PfccDto dailyAverage;
    private final PfccDto totalPfcc;
    private final PfccDto dailyAim;
    private final PfccDto maxDailyPfcc;
    private final PfccDto minDailyPfcc;
    private final PfccDto percentOfAim;

    private final TreeMap<LocalDate, DailyReportData> days;

    @Builder
    public PeriodReportData(String userName, Language userLanguage, LocalDate startDate, LocalDate endDate, PfccDto dailyAim, Map<LocalDate, DailyReportData> days) {
        this.userName = userName;
        this.userLanguage = userLanguage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyAim = dailyAim;
        this.minDailyPfcc = PfccDto.zero();
        this.maxDailyPfcc = PfccDto.zero();

        this.days = new TreeMap<>(Comparator.naturalOrder());
        this.days.putAll(days);

        this.totalPfcc = days.values()
                .stream()
                .map(DailyReportData::getTotal)
                .peek(this::adjustMinAndMaxPfcc)
                .reduce(PfccDto.zero(), PfccDto::add);
        this.dailyAverage = totalPfcc.divide(days.size());
        this.percentOfAim = dailyAverage.multiply(100).divide(dailyAim);
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class DailyReportData {
        private final List<MealForDailyReport> meals;
        private final List<MealForDailyReport> deconstructedMeals;
        private final PfccDto total;
        private final PfccDto percent;

        @Builder
        public DailyReportData(@Singular List<MealForDailyReport> meals,
                               @Singular List<MealForDailyReport> deconstructedMeals,
                               PfccDto aim) {
            this.meals = meals;
            this.deconstructedMeals = deconstructedMeals
                    .stream()
                    .collect(Collectors.groupingBy(dm -> dm.name))
                    .values()
                    .stream()
                    .map(list -> list.stream()
                            .reduce((m1, m2) -> MealForDailyReport.builder()
                                    .name(m1.name)
                                    .date(m1.date)
                                    .pfcc(m1.pfcc.add(m2.pfcc))
                                    .build()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            this.total = meals.stream().map(MealForDailyReport::pfcc).reduce(PfccDto.zero(), PfccDto::add);
            this.percent = this.total.multiply(100).divide(aim).scale(0);
        }
    }

    @Builder
    public record MealForDailyReport(String name, PfccDto pfcc, LocalDate date, BigDecimal weight) {
    }

    private void adjustMinAndMaxPfcc(PfccDto daily) {
        maxDailyPfcc.setProtein(max(maxDailyPfcc.getProtein(), daily.getProtein()));
        maxDailyPfcc.setFat(max(maxDailyPfcc.getFat(), daily.getFat()));
        maxDailyPfcc.setCarbohydrates(max(maxDailyPfcc.getCarbohydrates(), daily.getCarbohydrates()));
        maxDailyPfcc.setCalories(max(maxDailyPfcc.getCalories(), daily.getCalories()));

        if (minDailyPfcc.getProtein().equals(BigDecimal.ZERO)) {
            minDailyPfcc.setProtein(daily.getProtein());
        } else {
            minDailyPfcc.setProtein(min(minDailyPfcc.getProtein(), daily.getProtein()));
        }

        if (minDailyPfcc.getFat().equals(BigDecimal.ZERO)) {
            minDailyPfcc.setFat(daily.getFat());
        } else {
            minDailyPfcc.setFat(min(minDailyPfcc.getFat(), daily.getFat()));
        }

        if (minDailyPfcc.getCarbohydrates().equals(BigDecimal.ZERO)) {
            minDailyPfcc.setCarbohydrates(daily.getCarbohydrates());
        } else {
            minDailyPfcc.setCarbohydrates(min(minDailyPfcc.getCarbohydrates(), daily.getCarbohydrates()));
        }

        if (minDailyPfcc.getCalories().equals(BigDecimal.ZERO)) {
            minDailyPfcc.setCalories(daily.getCalories());
        } else {
            minDailyPfcc.setCalories(min(minDailyPfcc.getCalories(), daily.getCalories()));
        }
    }

    private BigDecimal max(BigDecimal first, BigDecimal second) {
        return first.compareTo(second) > 0 ? first : second;
    }

    private BigDecimal min(BigDecimal first, BigDecimal second) {
        return first.compareTo(second) > 0 ? second : first;
    }
}
