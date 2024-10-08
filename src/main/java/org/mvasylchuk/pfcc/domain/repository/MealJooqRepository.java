package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.MealOptionDto;
import org.mvasylchuk.pfcc.domain.dto.MealOptionType;
import org.mvasylchuk.pfcc.domain.dto.QueryMealDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Dish.DISH;
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

        return ctx.selectFrom(MEAL.leftJoin(FOOD)
                                  .on(MEAL.FOOD_ID.equal(FOOD.ID))
                                  .leftJoin(DISH)
                                  .on(MEAL.DISH_ID.equal(DISH.ID))).where(MEAL.ID.equal(id)).fetchOne(dbMeal -> {
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

    public Page<MealOptionDto> getMealOptions(String filter, Long userId, Integer page, Integer pageSize) {
        Condition foodCondition = FOOD.DELETED.isFalse().and(FOOD.IS_HIDDEN.isFalse().or(FOOD.OWNER_ID.eq(userId)));
        Condition dishCondition = DISH.DELETED.isFalse().and(DISH.OWNER_ID.eq(userId));

        if (filter != null && !filter.isBlank()) {
            foodCondition = foodCondition.and(FOOD.NAME.likeIgnoreCase("%" + filter + "%"));
            dishCondition = dishCondition.and(DISH.NAME.likeIgnoreCase("%" + filter + "%"));
        }

        var selectFoods = ctx.select(FOOD.ID.as("food_id"),
                                     DSL.field("NULL", Long.class).as("dish_id"),
                                     FOOD.NAME.as("name"),
                                     FOOD.PROTEIN.as("protein"),
                                     FOOD.FAT.as("fat"),
                                     FOOD.CARBOHYDRATES.as("carbohydrates"),
                                     FOOD.CALORIES.as("calories"),
                                     FOOD.TYPE.as("type"),
                                     FOOD.OWNER_ID.as("owner_id"),
                                     DSL.max(MEAL.EATEN_ON).as("eaten_on"))
                .from(FOOD)
                .leftJoin(MEAL).on(MEAL.FOOD_ID.eq(FOOD.ID).and(MEAL.DISH_ID.isNull()))
                .where(foodCondition)
                .groupBy(FOOD.ID);

        var selectDishes = ctx.select(DISH.FOOD_ID.as("food_id"),
                                      DISH.ID.as("dish_id"),
                                      DISH.NAME.as("name"),
                                      DISH.PROTEIN.as("protein"),
                                      DISH.FAT.as("fat"),
                                      DISH.CARBOHYDRATES.as("carbohydrates"),
                                      DISH.CALORIES.as("calories"),
                                      DSL.field("'%s'".formatted(MealOptionType.DISH.name()), String.class).as("type"),
                                      DISH.OWNER_ID.as("owner_id"),
                                      DSL.max(MEAL.EATEN_ON).as("eaten_on"))
                .from(DISH)
                .leftJoin(MEAL).on(MEAL.DISH_ID.eq(DISH.ID))
                .where(dishCondition)
                .groupBy(DISH.ID);

        var completeSelect = selectDishes.unionAll(selectFoods)
                .orderBy(DSL.field("eaten_on").desc(), DSL.field("food_id"), DSL.field("dish_id"));

        int count = ctx.fetchCount(completeSelect);

        List<MealOptionDto> data = completeSelect
                .limit(pageSize)
                .offset(page * pageSize)
                .fetch(fromDb -> new MealOptionDto(
                        fromDb.get("food_id", Long.class),
                        fromDb.get("dish_id", Long.class),
                        fromDb.get("name", String.class),
                        new PfccDto(fromDb.get("protein", BigDecimal.class),
                                    fromDb.get("fat", BigDecimal.class),
                                    fromDb.get("carbohydrates", BigDecimal.class),
                                    fromDb.get("calories", BigDecimal.class)),
                        MealOptionType.valueOf(fromDb.get("type", String.class)),
                        fromDb.get("owner_id", Long.class).equals(userId)
                ));

        return new Page<>(page,
                          pageSize,
                          (count / pageSize) + (count % pageSize > 0 ? 1 : 0),
                          count,
                          data);
    }
}
