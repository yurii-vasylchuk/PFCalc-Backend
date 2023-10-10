package org.mvasylchuk.pfcc.domain.service;

import lombok.RequiredArgsConstructor;
import org.mvasylchuk.pfcc.common.dto.Page;
import org.mvasylchuk.pfcc.domain.dto.FoodDto;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.entity.IngredientEntity;
import org.mvasylchuk.pfcc.domain.repository.FoodJooqRepository;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.domain.repository.IngredientRepository;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final UserService userService;
    private final FoodRepository foodRepository;
    private final FoodJooqRepository foodJooqRepository;
    private final IngredientRepository ingredientRepository;

    public FoodDto addFood(FoodDto request) {
        List<IngredientEntity> ingredientEntities = new ArrayList<>();

        FoodEntity food = new FoodEntity(null,
                request.getName(),
                request.getFoodType(),
                request.getPfcc().toPfcc(),
                request.getIsHidden(),
                userService.currentUser(),
                request.getDescription(),
                false,
                ingredientEntities);

        if (request.getFoodType() == FoodType.RECIPE) {
            ingredientEntities = request.getIngredients()
                    .stream()
                    .map(ingredientDto -> {
                        IngredientEntity ingredientEntity = new IngredientEntity();

                        ingredientEntity.setIngredientWeight(ingredientDto.getIngredientWeight());

                        ingredientEntity.setIngredient(foodRepository.getReferenceById(ingredientDto.getId()));

                        ingredientEntity.setRecipe(food);

                        return ingredientEntity;
                    }).toList();

            food.setIngredients(ingredientEntities);
        }

        foodRepository.save(food);

        return new FoodDto(food.getId(),
                food.getName(),
                food.getDescription(),
                request.getPfcc(),
                request.getIsHidden(),
                food.getType(),
                true,
                request.getIngredients());
    }

    public void remove(Long id) {
        FoodEntity food = foodRepository.findById(id).orElseThrow();
        food.setIsDeleted(true);
        foodRepository.save(food);
    }

    public Page<FoodDto> getFoodList(Integer page, Integer size) {
        Long userId = userService.currentUser().getId();
        return foodJooqRepository.getFoodList(page, size, userId);
    }

    public FoodDto getFoodById(Long id) {
        Long userId = userService.currentUser().getId();
        return foodJooqRepository.getFoodById(id, userId);
    }
}
