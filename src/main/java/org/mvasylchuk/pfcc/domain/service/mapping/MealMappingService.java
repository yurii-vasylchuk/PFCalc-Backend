package org.mvasylchuk.pfcc.domain.service.mapping;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.common.jpa.Weight;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
import org.mvasylchuk.pfcc.domain.dto.MealDto;
import org.mvasylchuk.pfcc.domain.dto.MealIngredientDto;
import org.mvasylchuk.pfcc.domain.entity.*;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.measurement.MeasurementInternalController;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;
import static org.mvasylchuk.pfcc.platform.error.ApiErrorCode.FOOD_IS_NOT_FOUND;
import static org.mvasylchuk.pfcc.platform.error.ApiErrorCode.VALIDATION;

@Service
@RequiredArgsConstructor
public class MealMappingService {

    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private final FoodRepository foodRepository;
    private final UserService userService;
    private final PfccMappingService pfccMappingService;
    private final WeightMappingService weightMappingService;
    private final MeasurementInternalController measurementInternalController;

    @Transactional(rollbackOn = Exception.class)
    public MealEntity toEntity(CommandMealDto mealDto) {
        FoodEntity food = foodRepository.findById(mealDto.getFoodId())
                .orElseThrow(() -> new PfccException(FOOD_IS_NOT_FOUND));

        MealEntity result = new MealEntity();
        result.setId(mealDto.getId());
        result.setFood(food);
        result.setUser(userService.currentUser());
        result.setEatenOn(mealDto.getEatenOn());

        switch (mealDto) {
            case CommandMealDto.SimpleCommandMealDto simple -> {
                Weight weight = weightMappingService.toValueObject(food.getId(), simple.getWeight());
                result.setWeight(weight);
                result.setPfcc(food.getPfcc()
                                       .multiply(weight.getInGram().divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP)));

                if (food.getType() == FoodType.RECIPE) {
                    List<FoodIngredientEntity> defaultIngredients = food.getIngredients()
                            .stream()
                            .filter(FoodIngredientEntity::getIsDefault)
                            .toList();

                    BigDecimal totalWeight = defaultIngredients
                            .stream()
                            .map(i -> i.getIngredientWeight().getInGram())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal weightCoef = totalWeight.divide(weight.getInGram(), 4, RoundingMode.HALF_UP);

                    List<MealIngredientEntity> mealIngredients = defaultIngredients.stream()
                            .map(fi -> new MealIngredientEntity(
                                    new MealIngredientPrimaryKey(mealDto.getId(), fi.getIngredient().getId()),
                                    result,
                                    fi.getIngredient(),
                                    fi.getIngredientWeight().multiply(weightCoef),
                                    fi.getIngredientIndex()
                            ))
                            .toList();
                    result.setIngredients(mealIngredients);
                }
            }

            case CommandMealDto.CustomizedCommandMealDto customized -> {
                Map<Long, FoodEntity> ingredientsById = foodRepository.findAllByIdIn(
                                customized.getIngredients()
                                        .stream()
                                        .map(MealIngredientDto::getId)
                                        .toList())
                        .stream()
                        .collect(Collectors.toMap(FoodEntity::getId, identity()));

                List<MealIngredientEntity> ingredients = customized.getIngredients().stream()
                        .map(i -> {
                            FoodEntity ingredientFood = ingredientsById.get(i.getId());
                            if (ingredientFood == null) {
                                throw new PfccException(
                                        "Illegal ingredient id: %d".formatted(i.getId()), FOOD_IS_NOT_FOUND
                                );
                            }
                            return new MealIngredientEntity(
                                    new MealIngredientPrimaryKey(mealDto.getId(), i.getId()),
                                    result,
                                    ingredientFood,
                                    weightMappingService.toValueObject(i.getId(), i.getIngredientWeight()),
                                    i.getIngredientIndex()
                            );
                        })
                        .toList();

                BigDecimal totalWeight = ingredients
                        .stream()
                        .map(MealIngredientEntity::getIngredientWeight)
                        .map(Weight::getInGram)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Pfcc combined = ingredients.stream()
                        .map(mi -> {
                            BigDecimal coef = mi.getIngredientWeight()
                                    .getInGram()
                                    .divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);

                            return mi.getFood().getPfcc().multiply(coef);
                        })
                        .reduce(Pfcc.ZERO, Pfcc::add);

                result.setWeight(new Weight(totalWeight, null, null, null));
                result.setIngredients(ingredients);
                result.setPfcc(combined);
            }
        }

        //TODO: get weight from ingredients if provided
//        Weight weight = weightMappingService.toValueObject(mealDto.getFoodId(), mealDto.getWeight());
//
//        BigDecimal coef = weight.getInGram().divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
//
//        if (food.getType() == FoodType.INGREDIENT) {
//            Pfcc pfcc = food.getPfcc();
//            result.setPfcc(pfcc.multiply(coef));
//        } else {
//            List<MealIngredientDto> ingredients = mealDto.getIngredients();
//
//            Pfcc pfcc = Pfcc.ZERO;
//            List<MealIngredientEntity> ingredientsEntities;
//
//            if (ingredients == null || ingredients.isEmpty()) {
//                ingredientsEntities = food.getIngredients()
//                        .stream()
//                        .filter(FoodIngredientEntity::getIsDefault)
//                        .map(fi -> new MealIngredientEntity(
//                                new MealIngredientPrimaryKey(mealDto.getId(), fi.getIngredient().getId()),
//                                result,
//                                fi.getIngredient(),
//                                fi.getIngredientWeight(),
//                                fi.getIngredientIndex()
//                        ))
//                        .toList();
//            } else {
//                ingredientsEntities = mapIngredients(ingredients, mealDto.getId(), mealDto.getFoodId());
//                for (MealIngredientEntity ie : ingredientsEntities) {
//                    ie.setMeal(result);
//                }
//            }
//
//            for (MealIngredientEntity ie : ingredientsEntities) {
//                BigDecimal ingCoef = ie.getIngredientWeight()
//                        .getInGram()
//                        .divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
//
//                Pfcc ingPfcc = ie.getFood().getPfcc().multiply(ingCoef);
//                pfcc = pfcc.add(ingPfcc);
//            }
//
//            result.setIngredients(ingredientsEntities);
//            result.setPfcc(pfcc);
//        }
//
//        result.setWeight(weight);

        return result;
    }

    private List<MealIngredientEntity> mapIngredients(List<MealIngredientDto> ingredients, Long mealId, Long foodId) {
        List<MealIngredientEntity> ingredientsEntities = new ArrayList<>();

        for (MealIngredientDto ingredient : ingredients) {
            FoodEntity ingFood = foodRepository.findById(ingredient.getId())
                    .orElseThrow(() -> new PfccException("Ingredient is not found", VALIDATION));

            Weight ingWeight = weightMappingService.toValueObject(
                    ingredient.getId(),
                    ingredient.getIngredientWeight()
            );

            ingredientsEntities.add(new MealIngredientEntity(
                    new MealIngredientPrimaryKey(mealId, foodId),
                    null,
                    ingFood,
                    ingWeight,
                    ingredient.getIngredientIndex()
            ));
        }

        return ingredientsEntities;
    }

    @Transactional(rollbackOn = Exception.class)
    public MealDto toDto(MealEntity mealEntity) {
        List<MealIngredientDto> ingredients = new ArrayList<>();

        if (mealEntity.getIngredients() != null) {
            for (MealIngredientEntity i : mealEntity.getIngredients()) {

                boolean ownedByUser = Objects.equals(i.getFood().getOwner().getId(), userService.currentUser().getId());

                ingredients.add(new MealIngredientDto(
                        i.getId().getIngredientId(),
                        i.getIngredientIndex(),
                        i.getFood().getName(),
                        i.getFood().getDescription(),
                        pfccMappingService.toPfccDto(i.getFood().getPfcc()),
                        i.getFood().getIsHidden(),
                        i.getFood().getType(),
                        ownedByUser,
                        weightMappingService.toDto(i.getIngredientWeight()),
                        measurementInternalController.byFoodId(i.getFood().getId())
                ));
            }
        }

        return new MealDto(
                mealEntity.getFood().getName(),
                mealEntity.getId(),
                mealEntity.getEatenOn(),
                weightMappingService.toDto(mealEntity.getWeight()),
                pfccMappingService.toPfccDto(mealEntity.getPfcc()),
                mealEntity.getFood().getId(),
                ingredients,
                measurementInternalController.byFoodId(mealEntity.getFood().getId())
        );
    }
}
