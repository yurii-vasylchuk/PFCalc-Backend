package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.MealOptionDto;
import org.mvasylchuk.pfcc.domain.dto.QueryMealDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;
import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;

@Slf4j
@Component
@RequiredArgsConstructor
public class MealJooqRepository {
    private final DSLContext ctx;

    public Page<QueryMealDto> getMealList(Integer page,
                                          Integer pageSize,
                                          LocalDateTime from,
                                          LocalDateTime to,
                                          Long userId) {
        Page<QueryMealDto> result = new Page<>();
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
        List<QueryMealDto> meals = ctx.selectFrom(MEAL.leftJoin(FOOD)
                        .on(MEAL.FOOD_ID.equal(FOOD.ID)))
                .where(condition)
                .limit(pageSize)
                .offset(page * pageSize)
                .fetch(dbMeal -> {
                    QueryMealDto meal = new QueryMealDto();
                    meal.setId(dbMeal.get(MEAL.ID));
                    meal.setName(dbMeal.get(FOOD.NAME));
                    meal.setEatenOn(dbMeal.get(MEAL.EATEN_ON));
                    meal.setWeight(dbMeal.get(MEAL.WEIGHT));
                    meal.setPfcc(new PfccDto(dbMeal.get(MEAL.PROTEIN),
                            dbMeal.get(MEAL.FAT),
                            dbMeal.get(MEAL.CARBOHYDRATES),
                            dbMeal.get(MEAL.CALORIES)));
                    meal.setFoodId(dbMeal.get(MEAL.FOOD_ID));

                    return meal;
                });
        result.setData(meals);
        return result;
    }

    public QueryMealDto getById(Long id) {

        return ctx.selectFrom(MEAL.leftJoin(FOOD)
                        .on(MEAL.FOOD_ID.equal(FOOD.ID)))
                .where(MEAL.ID.equal(id))
                .fetchOne(dbMeal -> {
                    QueryMealDto meal = new QueryMealDto();
                    meal.setId(dbMeal.get(MEAL.ID));
                    meal.setName(dbMeal.get(FOOD.NAME));
                    meal.setEatenOn(dbMeal.get(MEAL.EATEN_ON));
                    meal.setWeight(dbMeal.get(MEAL.WEIGHT));
                    meal.setPfcc(new PfccDto(dbMeal.get(MEAL.PROTEIN),
                            dbMeal.get(MEAL.FAT),
                            dbMeal.get(MEAL.CARBOHYDRATES),
                            dbMeal.get(MEAL.CALORIES)));
                    meal.setFoodId(dbMeal.get(MEAL.FOOD_ID));

                    return meal;
                });
    }

    public Page<MealOptionDto> getMealOptions(String filter, Long userId, Integer page, Integer pageSize) {
        Condition foodCondition = FOOD.DELETED.isFalse().and(FOOD.IS_HIDDEN.isFalse().or(FOOD.OWNER_ID.eq(userId)));

        if (filter != null && !filter.isBlank()) {
            foodCondition = foodCondition.and(FOOD.NAME.likeIgnoreCase("%" + filter + "%"));
        }

        var select = ctx.select(FOOD.ID.as("food_id"),
                        FOOD.NAME.as("name"),
                        FOOD.PROTEIN.as("protein"),
                        FOOD.FAT.as("fat"),
                        FOOD.CARBOHYDRATES.as("carbohydrates"),
                        FOOD.CALORIES.as("calories"),
                        FOOD.TYPE.as("type"),
                        FOOD.OWNER_ID.as("owner_id"),
                        DSL.max(MEAL.EATEN_ON).as("eaten_on"))
                .from(FOOD)
                .leftJoin(MEAL).on(MEAL.FOOD_ID.eq(FOOD.ID))
                .where(foodCondition)
                .groupBy(FOOD.ID)
                .orderBy(DSL.field("eaten_on").desc().nullsLast(), DSL.field("food_id"));

        int count = ctx.fetchCount(select);

        List<MealOptionDto> data = select
                .limit(pageSize)
                .offset(page * pageSize)
                .fetch(fromDb -> new MealOptionDto(
                        fromDb.get("food_id", Long.class),
                        fromDb.get("name", String.class),
                        new PfccDto(fromDb.get("protein", BigDecimal.class),
                                fromDb.get("fat", BigDecimal.class),
                                fromDb.get("carbohydrates", BigDecimal.class),
                                fromDb.get("calories", BigDecimal.class)),
                        FoodType.valueOf(fromDb.get("type", String.class)),
                        fromDb.get("owner_id", Long.class).equals(userId)
                ));

        return new Page<>(page,
                pageSize,
                (count / pageSize) + (count % pageSize > 0 ? 1 : 0),
                count,
                data);
    }
}
