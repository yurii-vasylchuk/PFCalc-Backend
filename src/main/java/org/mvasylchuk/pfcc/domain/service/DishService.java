package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.DishRequest;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.DishEntity;
import org.mvasylchuk.pfcc.domain.entity.DishIngredientEntity;
import org.mvasylchuk.pfcc.domain.repository.DishIngredientRepository;
import org.mvasylchuk.pfcc.domain.repository.DishJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.DishRepository;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final FoodRepository foodRepository;
    private final DishIngredientRepository dishIngredientRepository;
    private final UserService userService;
    private final DishJooqRepository jooqRepository;

    @Transactional(rollbackOn = Exception.class)
    public DishDto addDish(DishRequest request) {
        List<IngredientDto> ingredients = request.getIngredients();

        List<PfccDto> pfccList = ingredients.stream()
                .map(this::getFullPfcc)
                .toList();

        BigDecimal recipeWeight = ingredients.stream()
                .map(IngredientDto::getIngredientWeight)
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0));

        BigDecimal protein = pfccList.stream()
                .map(PfccDto::getProtein)
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0))
                .multiply(BigDecimal.valueOf(100))
                .divide(request.getCookedWeight(), RoundingMode.HALF_UP);
        BigDecimal fat = pfccList.stream()
                .map(PfccDto::getFat)
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0))
                .multiply(BigDecimal.valueOf(100))
                .divide(request.getCookedWeight(), RoundingMode.HALF_UP);
        BigDecimal carbohydrate = pfccList.stream()
                .map(PfccDto::getCarbohydrates)
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0))
                .multiply(BigDecimal.valueOf(100))
                .divide(request.getCookedWeight(), RoundingMode.HALF_UP);
        BigDecimal calories = pfccList.stream()
                .map(PfccDto::getCalories)
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0))
                .multiply(BigDecimal.valueOf(100))
                .divide(request.getCookedWeight(), RoundingMode.HALF_UP);

        PfccDto pfcc = new PfccDto(protein, fat, carbohydrate, calories);
        DishEntity dish = new DishEntity();
        dish.setName(request.getName());
        dish.setFood(foodRepository.getReferenceById(request.getFoodId()));
        dish.setRecipeWeight(recipeWeight);
        dish.setCookedWeight(request.getCookedWeight());
        dish.setPfcc(pfcc.toPfcc());
        dish.setCookedOn(request.getCookedOn());
        dish.setIngredients(request.getIngredients().stream()
                .map(i -> {
                    DishIngredientEntity res = new DishIngredientEntity();
                    res.setIngredient(foodRepository.getReferenceById(i.getId()));
                    res.setIngredientWeight(i.getIngredientWeight());
                    res.setDish(dish);
                    return res;
                })
                .toList());
        dish.setDeleted(false);
        dish.setOwner(userService.currentUser());

        dishRepository.save(dish);

        return DishDto.fromDishEntity(dish);
    }

    private PfccDto getFullPfcc(IngredientDto ingredient) {
        PfccDto ingredientPfcc = ingredient.getPfcc();
        BigDecimal multiplier = ingredient.getIngredientWeight().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return new PfccDto(ingredientPfcc.getProtein().multiply(multiplier),
                ingredientPfcc.getFat().multiply(multiplier),
                ingredientPfcc.getCarbohydrates().multiply(multiplier),
                ingredientPfcc.getCalories().multiply(multiplier));
    }

    public void remove(Long id) {
        DishEntity dish = dishRepository.findById(id).orElseThrow();
        dish.setDeleted(true);
        dishRepository.save(dish);
    }

    public DishDto getDishById(Long id) {
        Long userId = userService.currentUser().getId();
        return jooqRepository.getDishById(id, userId);
    }

    public Page<DishDto> getDishList(Integer page, Integer pageSize) {
        Long userId = userService.currentUser().getId();
        return jooqRepository.getDishList(page, pageSize, userId);
    }
}
