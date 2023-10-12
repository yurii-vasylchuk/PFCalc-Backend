package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Meal.MEAL;

@Component
@RequiredArgsConstructor
public class MealJooqRepository {
    private final DSLContext ctx;

    public Page<MealDto> getMealList(Integer page, Integer pageSize, LocalDateTime from, LocalDateTime to, Long userId) {
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
        List<MealDto> meals = ctx.selectFrom(MEAL)
                .where(condition)
                .limit(pageSize)
                .offset(page * pageSize)

                .fetch(dbMeal -> {
                    MealDto meal = new MealDto();
                    meal.setId(dbMeal.get(MEAL.ID));
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
}
