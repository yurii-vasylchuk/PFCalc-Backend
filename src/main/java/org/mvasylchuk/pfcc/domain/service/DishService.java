package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.DishRequest;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.DishEntity;
import org.mvasylchuk.pfcc.domain.entity.DishIngredientEntity;
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
    private final UserService userService;
    private final DishJooqRepository jooqRepository;

    @Transactional(rollbackOn = Exception.class)
    public DishDto addDish(DishRequest request) {
        BigDecimal coefByCookedWeight = BigDecimal.valueOf(100).divide(request.getCookedWeight(), RoundingMode.HALF_UP);

        List<IngredientDto> ingredients = request.getIngredients();

        List<Pfcc> pfccList = ingredients.stream()
                .map(this::getFullPfcc)
                .map(PfccDto::toPfcc)
                .toList();

        Pfcc completePfcc = Pfcc.combine(pfccList).multiply(coefByCookedWeight);

        BigDecimal recipeWeight = ingredients.stream()
                .map(IngredientDto::getIngredientWeight)
                .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0));

        PfccDto pfcc = PfccDto.fromPfcc(completePfcc);

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

    @Transactional(rollbackOn = Exception.class)
    public void remove(Long id) {
        DishEntity dish = dishRepository.findById(id).orElseThrow();
        dish.setDeleted(true);
        dishRepository.save(dish);
    }

    @Transactional(rollbackOn = Exception.class)
    public DishDto getDishById(Long id) {
        Long userId = userService.currentUser().getId();
        return jooqRepository.getDishById(id, userId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Page<DishDto> getDishList(Integer page, Integer pageSize) {
        Long userId = userService.currentUser().getId();
        return jooqRepository.getDishList(page, pageSize, userId);
    }
}
