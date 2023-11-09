package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.QueryMealDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Dish.DISH;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;
import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;

@Component
@RequiredArgsConstructor
public class MealJooqRepository {
    private final DSLContext ctx;

    public Page<QueryMealDto> getMealList(Integer page, Integer pageSize, LocalDateTime from, LocalDateTime to, Long userId) {
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
        List<QueryMealDto> meals = ctx.selectFrom(MEAL
                        .leftJoin(FOOD)
                        .on(MEAL.FOOD_ID.equal(FOOD.ID))
                        .leftJoin(DISH)
                        .on(MEAL.DISH_ID.equal(DISH.ID)))
                .where(condition)
                .limit(pageSize)
                .offset(page * pageSize)
                .fetch(dbMeal -> {
                    QueryMealDto meal = new QueryMealDto();
                    meal.setId(dbMeal.get(MEAL.ID));
                    if (dbMeal.get(MEAL.DISH_ID) != null) {
                        meal.setName(dbMeal.get(DISH.NAME));
                    } else {
                        meal.setName(dbMeal.get(FOOD.NAME));
                    }
                    meal.setEatenOn(dbMeal.get(MEAL.EATEN_ON));
                    meal.setWeight(dbMeal.get(MEAL.WEIGHT));
                    meal.setPfcc(new PfccDto(dbMeal.get(MEAL.PROTEIN),
                            dbMeal.get(MEAL.FAT),
                            dbMeal.get(MEAL.CARBOHYDRATES),
                            dbMeal.get(MEAL.CALORIES)));
                    meal.setFoodId(dbMeal.get(MEAL.FOOD_ID));
                    meal.setDishId(dbMeal.get(MEAL.DISH_ID));

                    return meal;
                });
        result.setData(meals);
        return result;
    }

    public QueryMealDto getById(Long id) {

        return ctx.selectFrom(MEAL
                .leftJoin(FOOD)
                .on(MEAL.FOOD_ID.equal(FOOD.ID))
                .leftJoin(DISH)
                .on(MEAL.DISH_ID.equal(DISH.ID)))
                .where(MEAL.ID.equal(id))
                .fetchOne(dbMeal -> {
                    QueryMealDto meal = new QueryMealDto();
                    meal.setId(dbMeal.get(MEAL.ID));
                    if (dbMeal.get(MEAL.DISH_ID) != null) {
                        meal.setName(dbMeal.get(DISH.NAME));
                    } else {
                        meal.setName(dbMeal.get(FOOD.NAME));
                    }
                    meal.setEatenOn(dbMeal.get(MEAL.EATEN_ON));
                    meal.setWeight(dbMeal.get(MEAL.WEIGHT));
                    meal.setPfcc(new PfccDto(dbMeal.get(MEAL.PROTEIN),
                            dbMeal.get(MEAL.FAT),
                            dbMeal.get(MEAL.CARBOHYDRATES),
                            dbMeal.get(MEAL.CALORIES)));
                    meal.setFoodId(dbMeal.get(MEAL.FOOD_ID));
                    meal.setDishId(dbMeal.get(MEAL.DISH_ID));

                    return meal;
                });
    }
}
