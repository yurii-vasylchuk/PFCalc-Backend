package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.mvasylchuk.pfcc.domain.entity.DishEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.MealEntity;
import org.mvasylchuk.pfcc.domain.repository.DishRepository;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.domain.repository.MealJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.MealRepository;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final DishRepository dishRepository;
    private final FoodRepository foodRepository;
    private final UserService userService;
    private final MealJooqRepository mealJooqRepository;

    @Transactional(rollbackOn = Exception.class)
    public MealDto addMeal(MealDto request) {
        BigDecimal coef = request.getWeight().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        Pfcc pfcc = new Pfcc();

        if (request.getDishId() != null) {
            DishEntity dish = dishRepository.findById(request.getDishId()).orElseThrow();
            pfcc.setProtein(dish.getPfcc().getProtein());
            pfcc.setFat(dish.getPfcc().getFat());
            pfcc.setCarbohydrates(dish.getPfcc().getCarbohydrates());
            pfcc.setCalories(dish.getPfcc().getCalories());

        } else {
            FoodEntity food = foodRepository.findById(request.getFoodId()).orElseThrow();
            pfcc.setProtein(food.getPfcc().getProtein());
            pfcc.setFat(food.getPfcc().getFat());
            pfcc.setCarbohydrates(food.getPfcc().getCarbohydrates());
            pfcc.setCalories(food.getPfcc().getCalories());
        }

        MealEntity meal = new MealEntity();
        meal.setWeight(request.getWeight());
        meal.setPfcc(new Pfcc(pfcc.getProtein().multiply(coef),
                pfcc.getFat().multiply(coef),
                pfcc.getCalories().multiply(coef),
                pfcc.getCalories().multiply(coef)));
        if (request.getDishId() != null) {
            DishEntity dish = dishRepository.findById(request.getDishId()).orElseThrow();
            meal.setFood(dish.getFood());
            meal.setDish(dish);
        } else {
            meal.setFood(foodRepository.findById(request.getFoodId()).orElseThrow());
        }

        meal.setEatenOn(request.getEatenOn());
        meal.setUser(userService.currentUser());
        mealRepository.save(meal);
        return MealDto.fromMealEntity(meal);
    }

    @Transactional(rollbackOn = Exception.class)
    public void remove(Long id) {
        mealRepository.delete(mealRepository.getReferenceById(id));
    }

    @Transactional(rollbackOn = Exception.class)
    public MealDto getById(Long id) {
        MealEntity mealEntity = mealRepository.findById(id).orElseThrow();
        return MealDto.fromMealEntity(mealEntity);
    }

    @Transactional(rollbackOn = Exception.class)
    public Page<MealDto> getMealList(Integer page, Integer pageSize, LocalDateTime from, LocalDateTime to) {
        Long userId = userService.currentUser().getId();
        return mealJooqRepository.getMealList(page, pageSize, from, to, userId);
    }
}
