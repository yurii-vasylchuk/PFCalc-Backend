package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.jooq.tables.records.ReportsRecord;
import org.mvasylchuk.pfcc.jooq.tables.records.UsersRecord;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData.DailyReportData;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData.MealForDailyReport;
import org.mvasylchuk.pfcc.report.dto.ReportDto;
import org.mvasylchuk.pfcc.report.dto.ReportStatus;
import org.mvasylchuk.pfcc.report.dto.ReportType;
import org.mvasylchuk.pfcc.user.Language;
import org.springframework.context.annotation.Primary;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.*;
import static org.mvasylchuk.pfcc.jooq.Tables.REPORTS;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;
import static org.mvasylchuk.pfcc.jooq.tables.FoodIngredients.FOOD_INGREDIENTS;
import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;
import static org.mvasylchuk.pfcc.jooq.tables.Users.USERS;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class ReportJooqRepository {
    public static final Language DEFAULT_LANGUAGE = Language.EN;
    private static final int DEFAULT_SCALE = 0;
    private final DSLContext ctx;

    private static ReportDto recordToDto(ReportsRecord r) {
        return new ReportDto(r.getId(),
                             r.getName(),
                             ReportStatus.valueOf(r.getStatus()),
                             ReportType.valueOf(r.getType()),
                             r.getUserId(),
                             r.getFilePath()
        );
    }

    public PeriodReportData getPeriodReport(Long userId, LocalDate from, LocalDate to) {
        PeriodReportData.PeriodReportDataBuilder builder = PeriodReportData.builder()
                .startDate(from)
                .endDate(to);

        UsersRecord usersRecord = ctx.selectFrom(USERS)
                .where(USERS.ID.eq(userId))
                .fetchOne();

        if (usersRecord == null) {
            throw new IllegalArgumentException("User with provided id is not found");
        }

        PfccDto userAim = new PfccDto(
                usersRecord.getProteinAim(),
                usersRecord.getFatAim(),
                usersRecord.getCarbohydratesAim(),
                usersRecord.getCaloriesAim()
        );
        Language language = usersRecord.getPreferredLanguage() == null ?
                            DEFAULT_LANGUAGE :
                            Language.valueOf(usersRecord.getPreferredLanguage());
        builder.userName(usersRecord.getName())
                .userLanguage(language)
                .dailyAim(userAim.scale(DEFAULT_SCALE));

        Map<LocalDate, DailyReportData.DailyReportDataBuilder> days = Stream.iterate(from,
                                                                                     date -> !date.isAfter(to),
                                                                                     prevDate -> prevDate.plusDays(1)
                )
                .collect(Collectors.toMap(Function.identity(), ignored -> DailyReportData.builder()));
        List<MealDeconstructTask> tasksForDeconstruction = new ArrayList<>();

        ctx.select(
                FOOD.NAME,
                MEAL.EATEN_ON,
                MEAL.PROTEIN,
                MEAL.FAT,
                MEAL.CARBOHYDRATES,
                MEAL.CALORIES,
                MEAL.WEIGHT_IN_GRAM,
                MEAL.MEASUREMENT_ID,
                MEAL.MEASUREMENT_WEIGHT,
                MEAL.MEASUREMENT_NAME,
                MEAL.FOOD_ID
        ).from(MEAL
                       .leftJoin(FOOD).on(MEAL.FOOD_ID.eq(FOOD.ID))
        ).where(MEAL.EATEN_ON.between(from.atTime(LocalTime.MIN), to.atTime(LocalTime.MAX))
                        .and(MEAL.OWNER_ID.eq(userId))
        ).forEach(rec -> {
            LocalDate date = rec.get(MEAL.EATEN_ON).toLocalDate();

            days.get(date)
                    .meal(MealForDailyReport.builder()
                                  .date(date)
                                  .name(rec.get(FOOD.NAME))
                                  .pfcc(new PfccDto(
                                          rec.get(MEAL.PROTEIN),
                                          rec.get(MEAL.FAT),
                                          rec.get(MEAL.CARBOHYDRATES),
                                          rec.get(MEAL.CALORIES)
                                  ).scale(DEFAULT_SCALE))
                                  .weight(rec.get(MEAL.WEIGHT_IN_GRAM).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                                  .build());

            tasksForDeconstruction.add(new MealDeconstructTask(
                    rec.get(MEAL.WEIGHT_IN_GRAM),
                    rec.get(MEAL.FOOD_ID),
                    rec.get(MEAL.EATEN_ON).toLocalDate()
            ));
        });

        tasksForDeconstruction.stream()
                .map(this::deconstructMeal)
                .flatMap(List::stream)
                .forEach(deconstructed -> days.get(deconstructed.getFirst())
                        .deconstructedMeal(deconstructed.getSecond()));

        builder.days(days.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().aim(userAim).build()
        )));

        return builder.build();
    }

    private List<Pair<LocalDate, MealForDailyReport>> deconstructMeal(MealDeconstructTask task) {
        Field<BigDecimal> weight = field(name("weight"), BigDecimal.class);
        Field<BigDecimal> protein = field(name("protein"), BigDecimal.class);
        Field<BigDecimal> fat = field(name("fat"), BigDecimal.class);
        Field<BigDecimal> carbohydrates = field(name("carbohydrates"), BigDecimal.class);
        Field<BigDecimal> calories = field(name("calories"), BigDecimal.class);

        List<RecipeIngredientDeconstructed> res = ctx.select(
                        FOOD.TYPE,
                        FOOD.NAME,
                        FOOD.PROTEIN.multiply(task.weight).divide(100)
                                .as(protein),
                        FOOD.FAT.multiply(task.weight).divide(100)
                                .as(fat),
                        FOOD.CARBOHYDRATES.multiply(task.weight).divide(100)
                                .as(carbohydrates),
                        FOOD.CALORIES.multiply(task.weight).divide(100)
                                .as(calories),
                        FOOD_INGREDIENTS.INGREDIENT_ID,
                        FOOD_INGREDIENTS.WEIGHT_IN_GRAM.multiply(task.weight)
                                .divide(sum(FOOD_INGREDIENTS.WEIGHT_IN_GRAM).over().partitionBy(FOOD_INGREDIENTS.RECIPE_ID))
                                .as(weight)
                )
                .from(FOOD.leftJoin(FOOD_INGREDIENTS).on(FOOD_INGREDIENTS.RECIPE_ID.eq(FOOD.ID)))
                .where(FOOD.ID.eq(task.foodId))
                .fetch(rec -> new RecipeIngredientDeconstructed(
                        FoodType.valueOf(rec.get(FOOD.TYPE)),
                        rec.get(FOOD.NAME),
                        new PfccDto(
                                rec.get(protein),
                                rec.get(fat),
                                rec.get(carbohydrates),
                                rec.get(calories)
                        ).scale(DEFAULT_SCALE),
                        rec.get(FOOD_INGREDIENTS.INGREDIENT_ID),
                        rec.get(weight)
                ));

        if (res.getFirst().type == FoodType.RECIPE) {
            return res.stream().flatMap(ing -> this.deconstructMeal(
                    new MealDeconstructTask(
                            ing.weight,
                            ing.id,
                            task.eatenOn
                    )).stream()).toList();
        }

        Pair<LocalDate, MealForDailyReport> result = Pair.of(
                task.eatenOn,
                MealForDailyReport.builder()
                        .name(res.getFirst().name)
                        .pfcc(res.getFirst().pfcc)
                        .date(task.eatenOn)
                        .weight(task.weight.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                        .build()
        );
        return List.of(result);
    }

    public List<Long> getAllUsersIdsForPeriodReportGeneration() {
        return ctx.select(USERS.ID)
                .from(USERS)
                .where(USERS.EMAIL_CONFIRMED.isTrue())
                .fetchInto(Long.class);
    }

    public Page<ReportDto> getUserReportsPage(Long userId, Integer page, Integer pageSize) {
        Condition condition = REPORTS.USER_ID.eq(userId)
                .and(REPORTS.STATUS.notIn(ReportStatus.CORRUPTED.name()));

        List<ReportDto> reports = ctx.selectFrom(REPORTS)
                .where(condition)
                .limit(pageSize)
                .offset(page * pageSize)
                .fetch(ReportJooqRepository::recordToDto);

        int totalElements = ctx.fetchCount(REPORTS, condition);


        return new Page<>(page, pageSize, totalElements / pageSize, totalElements, reports);
    }

    public ReportDto getReport(Long id) {
        return ctx.selectFrom(REPORTS)
                .where(REPORTS.ID.eq(id))
                .fetchOne(ReportJooqRepository::recordToDto);
    }

    private record MealDeconstructTask(BigDecimal weight, Long foodId, LocalDate eatenOn) {
    }

    private record RecipeIngredientDeconstructed(FoodType type, String name, PfccDto pfcc, Long id, BigDecimal weight) {
    }
}
