package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.entity.IngredientEntity;
import org.mvasylchuk.pfcc.domain.entity.IngredientPrimaryKey;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodMappingService {
    private final FoodRepository foodRepository;
    private final UserService userService;
    private final PfccMappingService pfccMappingService;

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
        List<IngredientEntity> ingredientList;
        Pfcc pfcc;
        result.setId(foodDto.getId());
        result.setName(foodDto.getName());
        result.setType(foodDto.getType());
        result.setIsHidden(foodDto.isHidden());
        if (foodDto.getId() != null) {
            result.setOwner(dbFood.getOwner());
        } else {
            result.setOwner(userService.currentUser());
        }
        result.setDescription(foodDto.getDescription());
        result.setIsDeleted(false);

        if (foodDto.getType() == FoodType.RECIPE) {

            ingredientList = foodDto.getIngredients()
                    .stream()
                    .map(ingredientDto -> {
                        IngredientEntity ingredientEntity = new IngredientEntity();

                        if (result.getId() != null) {
                            ingredientEntity.setId(new IngredientPrimaryKey(result.getId(), ingredientDto.getId()));
                        }

                        ingredientEntity.setIngredientWeight(ingredientDto.getIngredientWeight());
                        ingredientEntity.setIngredientIndex(ingredientDto.getIngredientIndex());

                        ingredientEntity.setIngredient(foodRepository.findById(ingredientDto.getId())
                                .orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND)));

                        ingredientEntity.setRecipe(result);

                        return ingredientEntity;
                    }).toList();

            pfcc = Pfcc.combine(ingredientList.stream()
                    .map(ingredientEntity -> ingredientEntity.getIngredient().getPfcc()
                            .multiply(ingredientEntity.getIngredientWeight())
                            .divide(new BigDecimal("100")))
                    .toList())
                    .multiply(new BigDecimal("100"))
                    .divide(
                            ingredientList.stream()
                                    .map(IngredientEntity::getIngredientWeight)
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
        List<IngredientDto> ingredientList = new ArrayList<>();
        if (foodEntity.getIngredients() != null) {
            ingredientList = foodEntity.getIngredients().stream()
                    .map(ingredientEntity -> {
                        FoodEntity ing = ingredientEntity.getIngredient();
                        UserEntity user = userService.currentUser();
                        Boolean ownedByUser = user.getId().equals(ing.getOwner().getId());

                        return new IngredientDto(
                                ing.getId(),
                                ingredientEntity.getIngredientIndex(),
                                ing.getName(),
                                ing.getDescription(),
                                pfccMappingService.toPfccDto(ing.getPfcc()),
                                ing.getIsHidden(),
                                ing.getType(),
                                ownedByUser,
                                null,
                                ingredientEntity.getIngredientWeight()
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
                ingredientList);

    }
}
