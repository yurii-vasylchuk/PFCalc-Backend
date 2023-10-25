package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.entity.IngredientEntity;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

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
        FoodEntity result = new FoodEntity();
        List<IngredientEntity> ingredientList;
        Pfcc pfcc;
        result.setId(foodDto.getId());
        result.setName(foodDto.getName());
        result.setType(foodDto.getType());
        result.setIsHidden(foodDto.isHidden());
        result.setOwner(userService.currentUser());//todo: подумать
        result.setDescription(foodDto.getDescription());
        result.setIsDeleted(false);//todo: подумать

        if (foodDto.getType() == FoodType.RECIPE) {

            ingredientList = foodDto.getIngredients()
                    .stream()
                    .map(ingredientDto -> {
                        IngredientEntity ingredientEntity = new IngredientEntity();

                        ingredientEntity.setIngredientWeight(ingredientDto.getIngredientWeight());

                        ingredientEntity.setIngredient(foodRepository.findById(ingredientDto.getId()).orElseThrow());

                        ingredientEntity.setRecipe(result);

                        return ingredientEntity;
                    }).toList();

            pfcc = Pfcc.combine(ingredientList.stream()
                    .map(ingredientEntity -> ingredientEntity.getIngredient().getPfcc()).toList());

            result.setIngredients(ingredientList);
            result.setPfcc(pfcc);

        } else result.setPfcc(pfccMappingService.toPfcc(foodDto.getPfcc()));


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
