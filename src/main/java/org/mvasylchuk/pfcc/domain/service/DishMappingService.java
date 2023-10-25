package org.mvasylchuk.pfcc.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;
import org.mvasylchuk.pfcc.domain.entity.*;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DishMappingService {
    private final FoodRepository foodRepository;
    private final UserService userService;
    private final PfccMappingService pfccMappingService;

    @Transactional(rollbackOn = Exception.class)
    public DishEntity toEntity(DishDto dishDto) {
        FoodEntity food = foodRepository.findById(dishDto.getFoodId()).orElseThrow();
        List<IngredientDto> ingredients = dishDto.getIngredients();
        Pfcc resultPfcc;
        BigDecimal recipeWeight;

        if (food.getType() == FoodType.RECIPE) {

            resultPfcc = Pfcc.combine(
                    ingredients.stream()
                            .map(di -> foodRepository.findById(di.getId()).orElseThrow().getPfcc().multiply(di.getIngredientWeight()))
                            .toList()
            ).divide(dishDto.getCookedWeight());

            recipeWeight = ingredients.stream()
                    .map(IngredientDto::getIngredientWeight)
                    .reduce(BigDecimal::add).orElse(BigDecimal.valueOf(0));
        } else {
            resultPfcc = food.getPfcc().multiply(dishDto.getRecipeWeight())
                    .divide(dishDto.getCookedWeight());

            recipeWeight = dishDto.getRecipeWeight();
        }


        DishEntity result = new DishEntity();

        result.setId(dishDto.getId());
        result.setName(dishDto.getName());
        result.setFood(food);
        result.setRecipeWeight(recipeWeight);
        result.setCookedWeight(dishDto.getCookedWeight());
        result.setPfcc(resultPfcc);
        result.setCookedOn(dishDto.getCookedOn());
        result.setDeleted(false);
        result.setOwner(userService.currentUser());
        if (food.getType() == FoodType.RECIPE) {
            result.setIngredients(ingredients
                    .stream()
                    .map(dto -> {
                        DishIngredientEntity entity = new DishIngredientEntity();
                        entity.setId(new DishIngredientPrimaryKey(dishDto.getId(), dto.getId()));
                        entity.setIngredientWeight(dto.getIngredientWeight());
                        entity.setDish(result);
                        entity.setIngredient(foodRepository.getReferenceById(dto.getId()));
                        return entity;
                    })
                    .toList());
        }

        return result;
    }

    @Transactional(rollbackOn = Exception.class)
    public DishDto toDto(DishEntity dishEntity) {
        List<IngredientDto> ingredientDtoList;
        if ((dishEntity.getIngredients() != null)) {
            ingredientDtoList = dishEntity.getIngredients().stream()
                    .map(i -> {
                        UserEntity user = userService.currentUser();
                        FoodEntity ing = i.getIngredient();
                        Boolean ownedByUser = user != null &&
                                ing.getOwner() != null &&
                                Objects.equals(user.getId(), ing.getOwner().getId());

                        return new IngredientDto(ing.getId(),
                                ing.getName(),
                                ing.getDescription(),
                                pfccMappingService.toPfccDto(ing.getPfcc()),
                                ing.getIsHidden(),
                                ing.getType(),
                                ownedByUser,
                                null,
                                i.getIngredientWeight());
                    })
                    .toList();
        } else ingredientDtoList = null;

        return new DishDto(dishEntity.getId(),
                dishEntity.getCookedOn(),
                pfccMappingService.toPfccDto(dishEntity.getPfcc()),
                dishEntity.getName(),
                dishEntity.getFood().getId(),
                ingredientDtoList,
                dishEntity.getRecipeWeight(),
                dishEntity.getCookedWeight(),
                dishEntity.getDeleted());

    }
}
