package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.dto.WeightDto;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.dto.FoodIngredientDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.jooq.tables.Food;
import org.mvasylchuk.pfcc.measurement.MeasurementDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mvasylchuk.pfcc.jooq.Tables.MEAL;
import static org.mvasylchuk.pfcc.jooq.Tables.MEASUREMENT;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;
import static org.mvasylchuk.pfcc.jooq.tables.FoodIngredients.FOOD_INGREDIENTS;

@Component
@RequiredArgsConstructor
public class FoodJooqRepository {
    private final DSLContext ctx;

    public Page<FoodDto> getFoodList(Integer page, Integer size, String name, FoodType type, Long userId) {
        Page<FoodDto> result = new Page<>();
        result.setPage(page);
        result.setPageSize(size);

        // Базовые условия фильтрации
        // TODO: Add full-text search, tags support
        Condition condition = FOOD.DELETED.isFalse()
                .and(FOOD.OWNER_ID.eq(userId).or(FOOD.IS_HIDDEN.isFalse()));

        if (name != null) {
            condition = condition.and(FOOD.NAME.likeIgnoreCase("%" + name + "%"));
        }

        if (type != null) {
            condition = condition.and(FOOD.TYPE.eq(type.name()));
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
        List<FoodDto> foods = ctx.with(candidates, deduplicated)
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
                                DSL.max(MEAL.EATEN_ON)
                                        .over(DSL.partitionBy(DEDUPLICATED.ID))
                                        .as("last_eaten")
                )
                .from(deduplicated)
                .leftJoin(MEAL).on(MEAL.FOOD_ID.eq(DEDUPLICATED.ID).and(MEAL.OWNER_ID.eq(userId)))
                .orderBy(DSL.field("last_eaten").desc().nullsLast(), DSL.field("id"))
                .limit(size)
                .offset(size * page)
                .fetch(dbFood -> {
                    FoodDto food = new FoodDto();
                    food.setId(dbFood.get(DEDUPLICATED.ID));
                    food.setName(dbFood.get(DEDUPLICATED.NAME));
                    food.setType(FoodType.valueOf(dbFood.get(DEDUPLICATED.TYPE)));
                    food.setPfcc(new PfccDto(
                            dbFood.get(DEDUPLICATED.PROTEIN),
                            dbFood.get(DEDUPLICATED.FAT),
                            dbFood.get(DEDUPLICATED.CARBOHYDRATES),
                            dbFood.get(DEDUPLICATED.CALORIES)
                    ));
                    food.setDescription(dbFood.get(DEDUPLICATED.DESCRIPTION));
                    food.setHidden(dbFood.get(DEDUPLICATED.IS_HIDDEN));
                    food.setOwnedByUser(Objects.equals(dbFood.get(DEDUPLICATED.OWNER_ID), userId));
                    return food;
                });
        Map<Long, FoodDto> foodsById = foods.stream().collect(Collectors.toMap(
                FoodDto::getId,
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

                    FoodDto foodDto = foodsById.get(dto.getFoodId());
                    if (foodDto.getMeasurements() == null) {
                        foodDto.setMeasurements(new ArrayList<>());
                    }
                    foodDto.getMeasurements().add(dto);
                });

        result.setData(foods);
        return result;
    }

    public FoodDto getFoodById(Long id, Long userId) {
        FoodDto result = ctx.selectFrom(FOOD)
                .where(FOOD.ID.equal(id)
                               .and(FOOD.OWNER_ID.equal(userId)
                                            .or(FOOD.IS_HIDDEN.isFalse())))
                .fetchOne(dbFood -> {
                    FoodDto food = new FoodDto();
                    food.setId(dbFood.get(FOOD.ID));
                    food.setName(dbFood.get(FOOD.NAME));
                    food.setType(FoodType.valueOf(dbFood.get(FOOD.TYPE)));
                    food.setPfcc(new PfccDto(dbFood.get(FOOD.PROTEIN),
                                             dbFood.get(FOOD.FAT),
                                             dbFood.get(FOOD.CARBOHYDRATES),
                                             dbFood.get(FOOD.CALORIES)
                    ));
                    food.setDescription(dbFood.get(FOOD.DESCRIPTION));
                    food.setHidden(dbFood.get(FOOD.IS_HIDDEN, Boolean.class));
                    food.setOwnedByUser(Objects.equals(dbFood.get(FOOD.OWNER_ID), userId));

                    return food;
                });
        if (result == null) {
            return null;
        }

        if (result.getType().equals(FoodType.RECIPE)) {
            List<FoodIngredientDto> ingredientList = ctx.selectFrom(
                            FOOD_INGREDIENTS
                                    .join(FOOD)
                                    .on(FOOD_INGREDIENTS.INGREDIENT_ID.equal(FOOD.ID)))
                    .where(FOOD_INGREDIENTS.RECIPE_ID.equal(id))
                    .fetch(dbIngredient ->
                           {
                               FoodIngredientDto ingredient = new FoodIngredientDto();
                               ingredient.setId(dbIngredient.get(FOOD.ID));
                               ingredient.setName(dbIngredient.get(FOOD.NAME));
                               ingredient.setDescription(dbIngredient.get(FOOD.DESCRIPTION));
                               ingredient.setPfcc(new PfccDto(dbIngredient.get(FOOD.PROTEIN),
                                                              dbIngredient.get(FOOD.FAT),
                                                              dbIngredient.get(FOOD.CARBOHYDRATES),
                                                              dbIngredient.get(FOOD.CALORIES)
                               ));
                               ingredient.setHidden(dbIngredient.get(FOOD.IS_HIDDEN, boolean.class));
                               ingredient.setType(FoodType.valueOf(dbIngredient.get(FOOD.TYPE)));
                               ingredient.setOwnedByUser(dbIngredient.get(FOOD.OWNER_ID)
                                                                 .equals(userId));
                               ingredient.setIngredientIndex(dbIngredient.get(FOOD_INGREDIENTS.INGREDIENT_INDEX));
                               ingredient.setIngredientWeight(new WeightDto(
                                       dbIngredient.get(FOOD_INGREDIENTS.MEASUREMENT_WEIGHT),
                                       dbIngredient.get(FOOD_INGREDIENTS.MEASUREMENT_ID),
                                       dbIngredient.get(FOOD_INGREDIENTS.WEIGHT_IN_GRAM),
                                       dbIngredient.get(FOOD_INGREDIENTS.MEASUREMENT_NAME)
                               ));
                               ingredient.setIsDefault(dbIngredient.get(FOOD_INGREDIENTS.IS_DEFAULT));

                               return ingredient;
                           });

            result.setIngredients(ingredientList);
        }

        return result;
    }
}
