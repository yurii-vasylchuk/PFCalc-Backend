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
import java.util.*;
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
        FoodEntity result;
        if (foodDto.getId() != null) {
            result = foodRepository.findById(foodDto.getId())
                    .orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND));

            if (result.getIsDeleted()) {
                throw new PfccException(ApiErrorCode.FOOD_IS_DELETED);
            }
        } else {
            result = new FoodEntity();
            result.setIngredients(new ArrayList<>());
        }

        Pfcc pfcc;

        result.setName(foodDto.getName().trim());
        result.setType(foodDto.getType());
        result.setIsHidden(foodDto.isHidden());
        if (foodDto.getId() != null) {
            result.setOwner(result.getOwner());
        } else {
            result.setOwner(userService.currentUser());
        }
        String description = foodDto.getDescription();
        result.setDescription(description != null ? description.trim() : null);
        result.setIsDeleted(false);

        if (foodDto.getType() == FoodType.RECIPE) {
            List<FoodIngredientEntity> originalIngredients = result.getIngredients();
            List<FoodIngredientDto> ingredientsDtos = foodDto.getIngredients();

            Iterator<FoodIngredientEntity> originIter = originalIngredients.iterator();
            while (originIter.hasNext()) {
                FoodIngredientEntity next = originIter.next();
                boolean absentInRequest = ingredientsDtos.stream()
                        .noneMatch(dto ->
                                           Objects.equals(next.getIngredientIndex(), dto.getIngredientIndex()) &&
                                           Objects.equals(next.getId().getIngredientId(), dto.getId()));
                if (absentInRequest) {
                    originIter.remove();
                }
            }

            Set<Long> uniqueIndexes = ingredientsDtos.stream()
                    .map(FoodIngredientDto::getIngredientIndex)
                    .collect(Collectors.toSet());
            Long nextIndexCounter = 0L;

            for (FoodIngredientDto ingredientDto : ingredientsDtos) {
                FoodIngredientEntity foodIngredientEntity = new FoodIngredientEntity();
                Optional<FoodIngredientEntity> existing = originalIngredients.stream()
                        .filter(oi ->
                                        Objects.equals(oi.getId().getIngredientId(), ingredientDto.getId()) &&
                                        Objects.equals(oi.getIngredientIndex(), ingredientDto.getIngredientIndex())
                        )
                        .findFirst();
                if (result.getId() != null && existing.isPresent()) {
                    foodIngredientEntity = existing.get();
                } else {
                    FoodEntity ingredientFood = foodRepository.findById(ingredientDto.getId())
                            .orElseThrow(() -> new PfccException(ApiErrorCode.FOOD_IS_NOT_FOUND));

                    foodIngredientEntity.setId(new FoodIngredientPrimaryKey(
                            result.getId(),
                            ingredientDto.getId()
                    ));
                    foodIngredientEntity.setIngredient(ingredientFood);
                    foodIngredientEntity.setRecipe(result);
                    originalIngredients.add(foodIngredientEntity);
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
                foodIngredientEntity.setIsDefault(ingredientDto.getIsDefault());
            }

            pfcc = Pfcc.combine(
                            result.getIngredients()
                                    .stream()
                                    .map(foodIngredientEntity -> foodIngredientEntity.getIngredient()
                                            .getPfcc()
                                            .multiply(foodIngredientEntity.getIngredientWeight().getInGram())
                                            .divide(new BigDecimal("100")))
                                    .toList())
                    .multiply(new BigDecimal("100"))
                    .divide(
                            result.getIngredients().stream()
                                    .map(FoodIngredientEntity::getIngredientWeight)
                                    .map(Weight::getInGram)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add));

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
