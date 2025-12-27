package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.dto.WeightDto;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.mvasylchuk.pfcc.domain.dto.MealIngredientDto;
import org.mvasylchuk.pfcc.domain.dto.MealOptionDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.jooq.Tables;
import org.mvasylchuk.pfcc.jooq.tables.Food;
import org.mvasylchuk.pfcc.measurement.MeasurementDto;
import org.mvasylchuk.pfcc.measurement.MeasurementInternalController;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mvasylchuk.pfcc.jooq.Tables.MEAL_INGREDIENTS;
import static org.mvasylchuk.pfcc.jooq.Tables.MEASUREMENT;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;
import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;

@Slf4j
@Component
@RequiredArgsConstructor
public class MealJooqRepository {
    private final DSLContext ctx;
    private final MeasurementInternalController measurementInternalController;

    public Page<MealDto> getMealList(Integer page,
                                     Integer pageSize,
                                     LocalDateTime from,
                                     LocalDateTime to,
                                     Long userId) {
        Page<MealDto> result = new Page<>();
        result.setPage(page);

        Condition condition = MEAL.OWNER_ID.equal(userId);
        if (from != null) {
            condition = condition.and(MEAL.EATEN_ON.greaterOrEqual(from));
        }
        if (to != null) {
            condition = condition.and(MEAL.EATEN_ON.lessOrEqual(to));
        }

        Integer totalElements = ctx.fetchCount(MEAL, condition);

        result.setTotalElements(totalElements);
        result.setPageSize(pageSize);
        result.setTotalPages((totalElements / pageSize) + (totalElements % pageSize > 0 ? 1 : 0));
        List<MealDto> meals = ctx.selectFrom(MEAL.leftJoin(FOOD)
                                                     .on(MEAL.FOOD_ID.equal(FOOD.ID)))
                .where(condition)
                .limit(pageSize)
                .offset(page * pageSize)
                .fetch(createExtendedMapper(userId));
        result.setData(meals);
        return result;
    }

    public MealDto getById(Long id, Long userId) {
        return ctx.selectFrom(MEAL.leftJoin(FOOD)
                                      .on(MEAL.FOOD_ID.equal(FOOD.ID)))
                .where(MEAL.ID.equal(id))
                .fetchOne(createExtendedMapper(userId));
    }

    public Page<MealOptionDto> getMealOptions(String filter, Long userId, Integer page, Integer size) {
        Page<MealOptionDto> result = new Page<>();
        result.setPage(page);
        result.setPageSize(size);

        Condition condition = FOOD.DELETED.isFalse().and(FOOD.IS_HIDDEN.isFalse().or(FOOD.OWNER_ID.eq(userId)));

        if (filter != null && !filter.isBlank()) {
            condition = condition.and(FOOD.NAME.likeIgnoreCase("%" + filter + "%"));
        }

        // CTE для дедупликации записей по имени с приоритетом по owner_id
        var candidates = DSL.name("candidates").as(
                DSL.select(FOOD.asterisk(),
                           DSL.rowNumber()
                                   .over(DSL.partitionBy(DSL.lower(FOOD.NAME))
                                                 .orderBy(DSL.when(FOOD.OWNER_ID.eq(userId), DSL.inline(1))
                                                                  .otherwise(DSL.inline(0)).desc(),
                                                          FOOD.ID
                                                 ))
                                   .as("rn")
                        )
                        .from(FOOD)
                        .where(condition)
        );

        // Промежуточная таблица для подсчета общего количества
        var deduplicated = DSL.name("deduplicated").as(
                DSL.select(DSL.asterisk())
                        .from(candidates)
                        .where(DSL.field("rn").eq(1))
        );

        // Подсчет общего количества элементов после дедупликации
        Integer totalElements = ctx.with(candidates, deduplicated)
                .selectCount()
                .from(deduplicated)
                .fetchOne(0, Integer.class);

        result.setTotalPages((totalElements / size) + (totalElements % size > 0 ? 1 : 0));
        result.setTotalElements(totalElements);
        Food DEDUPLICATED = FOOD.as("deduplicated");

        // Основной запрос с пагинацией и сортировкой по последнему приему пищи
        List<MealOptionDto> options = ctx.with(candidates, deduplicated)
                .selectDistinct(DEDUPLICATED.ID,
                                DEDUPLICATED.NAME,
                                DEDUPLICATED.TYPE,
                                DEDUPLICATED.PROTEIN,
                                DEDUPLICATED.FAT,
                                DEDUPLICATED.CARBOHYDRATES,
                                DEDUPLICATED.CALORIES,
                                DEDUPLICATED.DESCRIPTION,
                                DEDUPLICATED.IS_HIDDEN,
                                DEDUPLICATED.OWNER_ID,
                                DSL.max(Tables.MEAL.EATEN_ON)
                                        .over(DSL.partitionBy(DEDUPLICATED.ID))
                                        .as("last_eaten")
                )
                .from(deduplicated)
                .leftJoin(Tables.MEAL).on(Tables.MEAL.FOOD_ID.eq(DEDUPLICATED.ID).and(Tables.MEAL.OWNER_ID.eq(userId)))
                .orderBy(DSL.field("last_eaten").desc().nullsLast(), DSL.field("id"))
                .limit(size)
                .offset(size * page)
                .fetch(dbFood -> new MealOptionDto(
                        dbFood.get(DEDUPLICATED.ID),
                        dbFood.get(DEDUPLICATED.NAME),
                        new PfccDto(
                                dbFood.get(DEDUPLICATED.PROTEIN),
                                dbFood.get(DEDUPLICATED.FAT),
                                dbFood.get(DEDUPLICATED.CARBOHYDRATES),
                                dbFood.get(DEDUPLICATED.CALORIES)
                        ),
                        FoodType.valueOf(dbFood.get(DEDUPLICATED.TYPE)),
                        Objects.equals(dbFood.get(DEDUPLICATED.OWNER_ID), userId),
                        null
                        ));

        Map<Long, MealOptionDto> foodsById = options.stream().collect(Collectors.toMap(
                MealOptionDto::getFoodId,
                Function.identity()
        ));

        ctx.selectFrom(MEASUREMENT)
                .where(MEASUREMENT.FOOD_ID.in(foodsById.keySet()))
                .forEach(m -> {
                    MeasurementDto dto = new MeasurementDto(
                            m.getId(),
                            m.getFoodId(),
                            m.getToGramMultiplier(),
                            m.getName(),
                            m.getDefaultValue()
                    );

                    MealOptionDto foodDto = foodsById.get(dto.getFoodId());
                    if (foodDto.getMeasurements() == null) {
                        foodDto.setMeasurements(new ArrayList<>());
                    }
                    foodDto.getMeasurements().add(dto);
                });

        result.setData(options);
        return result;
    }

    private ExtendedMealMapper createExtendedMapper(Long userId) {
        return new ExtendedMealMapper(userId);
    }

    private static class BasicMealMapper implements RecordMapper<Record, MealDto> {
        @Override
        public MealDto map(Record dbMeal) {
            MealDto meal = new MealDto();
            meal.setId(dbMeal.get(MEAL.ID));
            meal.setName(dbMeal.get(FOOD.NAME));
            meal.setEatenOn(dbMeal.get(MEAL.EATEN_ON));
            meal.setWeight(new WeightDto(
                    dbMeal.get(MEAL.MEASUREMENT_WEIGHT),
                    dbMeal.get(MEAL.MEASUREMENT_ID),
                    dbMeal.get(MEAL.WEIGHT_IN_GRAM),
                    dbMeal.get(MEAL.MEASUREMENT_NAME)
            ));
            meal.setPfcc(new PfccDto(
                    dbMeal.get(MEAL.PROTEIN),
                    dbMeal.get(MEAL.FAT),
                    dbMeal.get(MEAL.CARBOHYDRATES),
                    dbMeal.get(MEAL.CALORIES)
            ));
            meal.setFoodId(dbMeal.get(MEAL.FOOD_ID));

            return meal;
        }
    }

    @RequiredArgsConstructor
    private class ExtendedMealMapper extends BasicMealMapper {
        private final Long userId;

        @Override
        public MealDto map(Record dbMeal) {
            MealDto result = super.map(dbMeal);
            if (result == null) {
                return result;
            }

            result.setMeasurements(measurementInternalController.byFoodId(result.getFoodId()));

            ctx.selectFrom(
                            MEAL_INGREDIENTS
                                    .leftJoin(FOOD).on(MEAL_INGREDIENTS.INGREDIENT_ID.eq(FOOD.ID))
                                    .leftJoin(MEAL).on(MEAL_INGREDIENTS.MEAL_ID.eq(MEAL.ID)))
                    .where(MEAL_INGREDIENTS.MEAL_ID.eq(result.getId()))
                    .forEach(dbRecord -> {
                        BigDecimal ingPfccMultiplier = dbRecord.get(MEAL_INGREDIENTS.WEIGHT_IN_GRAM)
                                .divide(dbRecord.get(MEAL.WEIGHT_IN_GRAM), RoundingMode.HALF_UP);
                        MealIngredientDto ing = new MealIngredientDto(
                                dbRecord.get(MEAL_INGREDIENTS.INGREDIENT_ID),
                                dbRecord.get(MEAL_INGREDIENTS.INGREDIENT_INDEX),
                                dbRecord.get(FOOD.NAME),
                                dbRecord.get(FOOD.DESCRIPTION),
                                new PfccDto(
                                        dbRecord.get(MEAL.PROTEIN).multiply(ingPfccMultiplier),
                                        dbRecord.get(MEAL.FAT).multiply(ingPfccMultiplier),
                                        dbRecord.get(MEAL.CALORIES).multiply(ingPfccMultiplier),
                                        dbRecord.get(MEAL.CARBOHYDRATES).multiply(ingPfccMultiplier)
                                ),
                                dbRecord.get(FOOD.IS_HIDDEN),
                                FoodType.valueOf(dbRecord.get(FOOD.TYPE)),
                                Objects.equals(dbRecord.get(FOOD.OWNER_ID), userId),
                                new WeightDto(
                                        dbRecord.get(MEAL_INGREDIENTS.MEASUREMENT_WEIGHT),
                                        dbRecord.get(MEAL_INGREDIENTS.MEASUREMENT_ID),
                                        dbRecord.get(MEAL_INGREDIENTS.WEIGHT_IN_GRAM),
                                        dbRecord.get(MEAL_INGREDIENTS.MEASUREMENT_NAME)
                                ),
                                measurementInternalController.byFoodId(dbRecord.get(FOOD.ID))
                        );

                        if (result.getIngredients() == null) {
                            result.setIngredients(new ArrayList<>());
                        }
                        result.getIngredients().add(ing);
                    });

            return result;
        }
    }
}
