package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static org.mvasylchuk.pfcc.jooq.tables.Ingredients.INGREDIENTS;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;

@Component
@RequiredArgsConstructor
public class FoodJooqRepository {
    private final DSLContext ctx;

    public Page<FoodDto> getFoodList(Integer page, Integer size, Long userId) {
        Page<FoodDto> foodList = new Page<>();
        Integer totalElements = ctx.fetchCount(FOOD, FOOD.OWNER_ID.equal(userId).or(FOOD.IS_HIDDEN.isFalse()));
        foodList.setPage(page);
        foodList.setPageSize(size);
        foodList.setTotalPages((totalElements / size) + (totalElements % size > 0 ? 1 : 0));
        foodList.setTotalElements(totalElements);

        List<FoodDto> foods = ctx.selectFrom(FOOD)
                .where(FOOD.OWNER_ID.equal(userId))
                .or(FOOD.IS_HIDDEN.isFalse())
                .limit(DSL.inline(size))
                .offset(DSL.inline(size * page))

                .fetch(dbFood -> {
                    FoodDto food = new FoodDto();
                    food.setId(dbFood.get(FOOD.ID));
                    food.setName(dbFood.get(FOOD.NAME));
                    food.setType(FoodType.valueOf(dbFood.get(FOOD.TYPE)));
                    food.setPfcc(new PfccDto(dbFood.get(FOOD.PROTEIN), dbFood.get(FOOD.FAT), dbFood.get(FOOD.CARBOHYDRATES), dbFood.get(FOOD.CALORIES)));
                    food.setDescription(dbFood.get(FOOD.DESCRIPTION));
                    food.setHidden(dbFood.get(FOOD.IS_HIDDEN, Boolean.class));
                    food.setOwnedByUser(Objects.equals(dbFood.get(FOOD.OWNER_ID), userId));

                    return food;
                });

        foodList.setData(foods);

        return foodList;
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
                    food.setPfcc(new PfccDto(dbFood.get(FOOD.PROTEIN), dbFood.get(FOOD.FAT), dbFood.get(FOOD.CARBOHYDRATES), dbFood.get(FOOD.CALORIES)));
                    food.setDescription(dbFood.get(FOOD.DESCRIPTION));
                    food.setHidden(dbFood.get(FOOD.IS_HIDDEN, Boolean.class));
                    food.setOwnedByUser(Objects.equals(dbFood.get(FOOD.OWNER_ID), userId));

                    return food;
                });

        if (result.getType().equals(FoodType.RECIPE) ) {
            List<IngredientDto> ingredientList = ctx.selectFrom(
                            INGREDIENTS
                                    .join(FOOD)
                                    .on(INGREDIENTS.INGREDIENT_ID.equal(FOOD.ID)))
                    .where(INGREDIENTS.RECIPE_ID.equal(id))
                    .fetch(dbIngredient ->
                    {
                        IngredientDto ingredient = new IngredientDto();
                        ingredient.setId(dbIngredient.get(FOOD.ID));
                        ingredient.setName(dbIngredient.get(FOOD.NAME));
                        ingredient.setDescription(dbIngredient.get(FOOD.DESCRIPTION));
                        ingredient.setPfcc(new PfccDto(dbIngredient.get(FOOD.PROTEIN),
                                dbIngredient.get(FOOD.FAT),
                                dbIngredient.get(FOOD.CARBOHYDRATES),
                                dbIngredient.get(FOOD.CALORIES)));
                        ingredient.setHidden(dbIngredient.get(FOOD.IS_HIDDEN, boolean.class));
                        ingredient.setType(FoodType.valueOf(dbIngredient.get(FOOD.TYPE)));
                        ingredient.setOwnedByUser(dbIngredient.get(FOOD.OWNER_ID).equals(userId));
                        ingredient.setIngredientWeight(dbIngredient.get(INGREDIENTS.INGREDIENT_WEIGHT));

                        return ingredient;
                    });

           result.setIngredients(ingredientList);
        }

        return result;
    }
}