package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
import org.mvasylchuk.pfcc.domain.dto.QueryMealDto;
import org.mvasylchuk.pfcc.domain.entity.DishEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.MealEntity;
import org.mvasylchuk.pfcc.domain.repository.DishRepository;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MealMappingService {

    private final FoodRepository foodRepository;
    private final DishRepository dishRepository;
    private final UserService userService;
    private final PfccMappingService pfccMappingService;

    @Transactional(rollbackOn = Exception.class)
    public MealEntity toEntity(CommandMealDto mealDto) {
        MealEntity result = new MealEntity();

        BigDecimal coef = mealDto.getWeight().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        Pfcc pfcc;

        if (mealDto.getDishId() != null) {
            DishEntity dish = dishRepository.findById(mealDto.getDishId()).orElseThrow(() -> new PfccException(ApiErrorCode.DISH_IS_NOT_FOUND));
            pfcc = dish.getPfcc();
        } else {
            FoodEntity food = foodRepository.findById(mealDto.getFoodId()).orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND));
            pfcc = food.getPfcc();
        }

        result.setId(mealDto.getId());
        result.setWeight(mealDto.getWeight());
        result.setPfcc(pfcc.multiply(coef));
        if (mealDto.getDishId() != null) {
            DishEntity dish = dishRepository.findById(mealDto.getDishId()).orElseThrow(() -> new PfccException(ApiErrorCode.DISH_IS_NOT_FOUND));
            result.setFood(dish.getFood());
            result.setDish(dish);
        } else {
            result.setFood(foodRepository.findById(mealDto.getFoodId()).orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND)));
        }
        result.setUser(userService.currentUser());
        result.setEatenOn(mealDto.getEatenOn());

        return result;
    }

    @Transactional(rollbackOn = Exception.class)
    public QueryMealDto toDto(MealEntity mealEntity) {
        return new QueryMealDto(
                mealEntity.getDish() != null ?
                        mealEntity.getDish().getName() :
                        mealEntity.getFood().getName(),
                mealEntity.getId(),
                mealEntity.getEatenOn(),
                mealEntity.getWeight(),
                pfccMappingService.toPfccDto(mealEntity.getPfcc()),
                mealEntity.getFood() != null ? mealEntity.getFood().getId() : null,
                mealEntity.getDish() != null ? mealEntity.getDish().getId() : null);
    }
}
