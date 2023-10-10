package org.mvasylchuk.pfcc.domain.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mvasylchuk.pfcc.jooq.tables.Dish.DISH;
import static org.mvasylchuk.pfcc.jooq.tables.DishIngredients.DISH_INGREDIENTS;
import static org.mvasylchuk.pfcc.jooq.tables.Food.FOOD;

@Component
@RequiredArgsConstructor
public class DishJooqRepository {
    private final DSLContext ctx;

    public DishDto getDishById(Long id, Long userId) {
        DishDto result = ctx.selectFrom(
                        DISH
                )
                .where(DISH.ID.equal(id))
                .and(DISH.OWNER_ID.equal(userId))
                .and(DISH.DELETED.isFalse())
                .fetchOne(dbDish -> {
                    DishDto dish = new DishDto();
                    dish.setId(dbDish.get(DISH.ID));
                    dish.setCookedOn(dbDish.get(DISH.COOKED_ON));
                    dish.setPfcc(new PfccDto(dbDish.get(DISH.PROTEIN),
                            dbDish.get(DISH.FAT),
                            dbDish.get(DISH.CARBOHYDRATES),
                            dbDish.get(DISH.CALORIES)));
                    dish.setName(dbDish.get(DISH.NAME));
                    dish.setFoodId(dbDish.get(DISH.FOOD_ID));
                    dish.setRecipeWeight(dbDish.get(DISH.RECIPE_WEIGHT));
                    dish.setCookedWeight(dbDish.get(DISH.COOKED_WEIGHT));
                    dish.setDeleted(dbDish.get(DISH.DELETED, Boolean.class));

                    return dish;
                });
        if (result == null) {
            return null;
        }
        List<IngredientDto> ingredientList = ctx.selectFrom(
                        DISH_INGREDIENTS
                                .join(FOOD)
                                .on(DISH_INGREDIENTS.INGREDIENT_ID.equal(FOOD.ID)))
                .where(DISH_INGREDIENTS.DISH_ID.equal(id))
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
                    ingredient.setIsHidden(dbIngredient.get(FOOD.IS_HIDDEN, boolean.class));
                    ingredient.setFoodType(FoodType.valueOf(dbIngredient.get(FOOD.TYPE)));
                    ingredient.setOwnedByUser(dbIngredient.get(FOOD.OWNER_ID).equals(userId));
                    ingredient.setIngredientWeight(dbIngredient.get(DISH_INGREDIENTS.INGREDIENT_WEIGHT));

                    return ingredient;
                });
        result.setIngredients(ingredientList);

        return result;
    }

    public Page<DishDto> getDishList(Integer page, Integer pageSize, Long userId) {
        Page<DishDto> result = new Page<>();
        Integer totalElements = ctx.fetchCount(DISH, DISH.OWNER_ID.equal(userId).and(DISH.DELETED.isFalse()));
        result.setTotalPages((totalElements / pageSize) + (totalElements % pageSize > 0 ? 1 : 0));
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotalElements(totalElements);

        List<DishDto> dishes = ctx.selectFrom(DISH)
                .where(DISH.OWNER_ID.equal(userId))
                .and(DISH.DELETED.isFalse())
                .limit(pageSize)
                .offset(page * pageSize)

                .fetch(dbDish -> {
                   DishDto dish = new DishDto();
                   dish.setId(dbDish.get(DISH.ID));
                   dish.setCookedOn(dbDish.get(DISH.COOKED_ON));
                   dish.setPfcc(new PfccDto(dbDish.get(DISH.PROTEIN),
                           dbDish.get(DISH.FAT),
                           dbDish.get(DISH.CARBOHYDRATES),
                           dbDish.get(DISH.CALORIES)));
                   dish.setName(dbDish.get(DISH.NAME));
                   dish.setFoodId(dbDish.get(DISH.FOOD_ID));
                   dish.setRecipeWeight(dbDish.get(DISH.RECIPE_WEIGHT));
                   dish.setCookedWeight(dbDish.get(DISH.COOKED_WEIGHT));
                   dish.setDeleted(dbDish.get(DISH.DELETED,Boolean.class));

                   return dish;
                });
        result.setData(dishes);
        return result;
    }
}
