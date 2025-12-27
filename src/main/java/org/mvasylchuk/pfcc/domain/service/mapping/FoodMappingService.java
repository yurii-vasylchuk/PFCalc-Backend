package org.mvasylchuk.pfcc.domain.service.mapping;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.common.jpa.Weight;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.dto.FoodIngredientDto;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodIngredientEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodIngredientPrimaryKey;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.measurement.MeasurementInternalController;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodMappingService {
    private final FoodRepository foodRepository;
    private final UserService userService;
    private final PfccMappingService pfccMappingService;
    private final WeightMappingService weightMappingService;
    private final MeasurementInternalController measurementInternalController;

    @Transactional(rollbackOn = Exception.class)
    public FoodEntity toEntity(FoodDto foodDto) {
        FoodEntity dbFood = null;
        if (foodDto.getId() != null) {
            dbFood = foodRepository.findById(foodDto.getId())
                    .orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND));

            if (dbFood.getIsDeleted()) {
                throw new PfccException(ApiErrorCode.FOOD_IS_DELETED);
            }
        }
        FoodEntity result = new FoodEntity();
        List<FoodIngredientEntity> ingredientList;
        Pfcc pfcc;
        result.setId(foodDto.getId());
        result.setName(foodDto.getName().trim());
        result.setType(foodDto.getType());
        result.setIsHidden(foodDto.isHidden());
        if (foodDto.getId() != null) {
            result.setOwner(dbFood.getOwner());
        } else {
            result.setOwner(userService.currentUser());
        }
        String description = foodDto.getDescription();
        result.setDescription(description != null ? description.trim() : null);
        result.setIsDeleted(false);

        if (foodDto.getType() == FoodType.RECIPE) {
            ingredientList = new ArrayList<>();

            List<FoodIngredientDto> ingredients = foodDto.getIngredients();

            Set<Long> uniqueIndexes = ingredients.stream()
                    .map(FoodIngredientDto::getIngredientIndex)
                    .collect(Collectors.toSet());
            Long nextIndexCounter = 0L;

            for (FoodIngredientDto ingredientDto : ingredients) {
                FoodIngredientEntity foodIngredientEntity = new FoodIngredientEntity();

                if (result.getId() != null) {
                    foodIngredientEntity.setId(new FoodIngredientPrimaryKey(
                            result.getId(),
                            ingredientDto.getId()
                    ));
                }

                foodIngredientEntity.setIngredientWeight(
                        weightMappingService.toValueObject(ingredientDto.getId(), ingredientDto.getIngredientWeight())
                );

                Long index = ingredientDto.getIngredientIndex();
                if (index == null) {
                    while (uniqueIndexes.contains(nextIndexCounter)) {
                        nextIndexCounter++;
                    }
                    index = nextIndexCounter;
                }

                foodIngredientEntity.setIngredientIndex(index);

                foodIngredientEntity.setIngredient(foodRepository.findById(ingredientDto.getId())
                                                           .orElseThrow(() -> new PfccException(
                                                                   ApiErrorCode.FOOD_IS_NOT_FOUND)));

                foodIngredientEntity.setRecipe(result);
                foodIngredientEntity.setIsDefault(ingredientDto.getIsDefault());

                ingredientList.add(foodIngredientEntity);
            }

            pfcc = Pfcc.combine(
                            ingredientList
                                    .stream()
                                    .map(foodIngredientEntity -> foodIngredientEntity.getIngredient()
                                            .getPfcc()
                                            .multiply(foodIngredientEntity.getIngredientWeight().getInGram())
                                            .divide(new BigDecimal("100")))
                                    .toList())
                    .multiply(new BigDecimal("100"))
                    .divide(
                            ingredientList.stream()
                                    .map(FoodIngredientEntity::getIngredientWeight)
                                    .map(Weight::getInGram)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            result.setIngredients(ingredientList);
            result.setPfcc(pfcc);
        } else {
            result.setPfcc(pfccMappingService.toPfcc(foodDto.getPfcc()));
        }

        return result;
    }

    @Transactional(rollbackOn = Exception.class)
    public FoodDto toDto(FoodEntity foodEntity) {
        List<FoodIngredientDto> ingredientList = new ArrayList<>();
        if (foodEntity.getIngredients() != null) {
            ingredientList = foodEntity.getIngredients().stream()
                    .map(foodIngredientEntity -> {
                        FoodEntity ing = foodIngredientEntity.getIngredient();
                        UserEntity user = userService.currentUser();
                        Boolean ownedByUser = user.getId().equals(ing.getOwner().getId());

                        return new FoodIngredientDto(
                                ing.getId(),
                                foodIngredientEntity.getIngredientIndex(),
                                ing.getName(),
                                ing.getDescription(),
                                pfccMappingService.toPfccDto(ing.getPfcc()),
                                ing.getIsHidden(),
                                ing.getType(),
                                ownedByUser,
                                null,
                                measurementInternalController.byFoodId(ing.getId()),//TODO: Load measurements
                                weightMappingService.toDto(foodIngredientEntity.getIngredientWeight()),
                                foodIngredientEntity.getIsDefault()
                        );
                    }).toList();
        }
        return new FoodDto(foodEntity.getId(),
                           foodEntity.getName(),
                           foodEntity.getDescription(),
                           pfccMappingService.toPfccDto(foodEntity.getPfcc()),
                           foodEntity.getIsHidden(),
                           foodEntity.getType(),
                           true,
                           ingredientList,
                           measurementInternalController.byFoodId(foodEntity.getId())
        );

    }
}
