package org.mvasylchuk.pfcc.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.jooq.tables.records.UsersRecord;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData.DailyReportData;
import org.mvasylchuk.pfcc.report.dto.PeriodReportData.MealForDailyReport;
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

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.if_;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.sum;
import static org.mvasylchuk.pfcc.jooq.tables.Dish.DISH;
import static org.mvasylchuk.pfcc.jooq.tables.DishIngredients.DISH_INGREDIENTS;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;
import static org.mvasylchuk.pfcc.jooq.tables.Ingredients.INGREDIENTS;
import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;
import static org.mvasylchuk.pfcc.jooq.tables.Users.USERS;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class ReportJooqRepository implements ReportRepository {
    private static final int DEFAULT_SCALE = 0;
    public static final Language DEFAULT_LANGUAGE = Language.EN;

    private final DSLContext ctx;

    @Override
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
        Language language = usersRecord.getPreferredLanguage() == null ? DEFAULT_LANGUAGE : Language.valueOf(usersRecord.getPreferredLanguage());
        builder.userName(usersRecord.getName())
                .userLanguage(language)
                .dailyAim(userAim.scale(DEFAULT_SCALE));

        Map<LocalDate, DailyReportData.DailyReportDataBuilder> days = Stream.iterate(from, date -> !date.isAfter(to), prevDate -> prevDate.plusDays(1))
                .collect(Collectors.toMap(Function.identity(), ignored -> DailyReportData.builder()));
        List<MealDeconstructTask> tasksForDeconstruction = new ArrayList<>();

        Field<String> asName = field(name("name"), String.class);

        ctx.select(
                if_(MEAL.DISH_ID.isNull(), FOOD.NAME, DISH.NAME).as(asName),
                MEAL.EATEN_ON, MEAL.PROTEIN, MEAL.FAT, MEAL.CARBOHYDRATES, MEAL.CALORIES,
                MEAL.WEIGHT, MEAL.FOOD_ID, MEAL.DISH_ID
        ).from(MEAL
                .leftJoin(DISH).on(MEAL.DISH_ID.isNotNull().and(MEAL.DISH_ID.eq(DISH.ID)))
                .leftJoin(FOOD).on(MEAL.DISH_ID.isNull().and(MEAL.FOOD_ID.eq(FOOD.ID)))
        ).where(MEAL.EATEN_ON.between(from.atTime(LocalTime.MIN), to.atTime(LocalTime.MAX))
                .and(MEAL.OWNER_ID.eq(userId))
        ).forEach(rec -> {
            LocalDate date = rec.get(MEAL.EATEN_ON).toLocalDate();

            days.get(date)
                    .meal(MealForDailyReport.builder()
                            .date(date)
                            .name(rec.get(asName))
                            .pfcc(new PfccDto(
                                    rec.get(MEAL.PROTEIN),
                                    rec.get(MEAL.FAT),
                                    rec.get(MEAL.CARBOHYDRATES),
                                    rec.get(MEAL.CALORIES)
                            ).scale(DEFAULT_SCALE))
                            .weight(rec.get(MEAL.WEIGHT).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                            .build());

            tasksForDeconstruction.add(new MealDeconstructTask(
                    rec.get(MEAL.WEIGHT),
                    rec.get(MEAL.FOOD_ID),
                    rec.get(MEAL.DISH_ID),
                    rec.get(MEAL.EATEN_ON).toLocalDate()
            ));
        });

        tasksForDeconstruction.stream()
                .map(this::deconstructMeal)
                .flatMap(List::stream)
                .forEach(deconstructed -> days.get(deconstructed.getFirst()).deconstructedMeal(deconstructed.getSecond()));

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

        if (task.dishId != null) {
            return ctx.select(DISH_INGREDIENTS.INGREDIENT_ID,
                            DISH_INGREDIENTS.INGREDIENT_WEIGHT
                                    .multiply(task.weight)
                                    .multiply(DISH.RECIPE_WEIGHT.divide(DISH.COOKED_WEIGHT))
                                    .divide(sum(DISH_INGREDIENTS.INGREDIENT_WEIGHT)
                                            .over().partitionBy(DISH_INGREDIENTS.DISH_ID)).as(weight))
                    .from(DISH.join(DISH_INGREDIENTS).on(DISH.ID.eq(DISH_INGREDIENTS.DISH_ID)))
                    .where(DISH.ID.eq(task.dishId))
                    .stream()
                    .flatMap(rec -> deconstructMeal(new MealDeconstructTask(rec.get(weight), rec.get(DISH_INGREDIENTS.INGREDIENT_ID), null, task.eatenOn)).stream())
                    .toList();
        }

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
                        INGREDIENTS.INGREDIENT_ID,
                        INGREDIENTS.INGREDIENT_WEIGHT.multiply(task.weight)
                                .divide(sum(INGREDIENTS.INGREDIENT_WEIGHT).over().partitionBy(INGREDIENTS.RECIPE_ID))
                                .as(weight)
                )
                .from(FOOD.leftJoin(INGREDIENTS).on(INGREDIENTS.RECIPE_ID.eq(FOOD.ID)))
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
                        rec.get(INGREDIENTS.INGREDIENT_ID),
                        rec.get(weight)
                ));

        if (res.get(0).type == FoodType.RECIPE) {
            return res.stream().flatMap(ing -> this.deconstructMeal(
                    new MealDeconstructTask(
                            ing.weight,
                            ing.id,
                            null,
                            task.eatenOn)).stream()).toList();
        }

        Pair<LocalDate, MealForDailyReport> result = Pair.of(
                task.eatenOn,
                MealForDailyReport.builder()
                        .name(res.get(0).name)
                        .pfcc(res.get(0).pfcc)
                        .date(task.eatenOn)
                        .weight(task.weight.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP))
                        .build());
        return List.of(result);
    }

    private record MealDeconstructTask(BigDecimal weight, Long foodId, Long dishId, LocalDate eatenOn) {
    }

    private record RecipeIngredientDeconstructed(FoodType type, String name, PfccDto pfcc, Long id, BigDecimal weight) {
    }
}
