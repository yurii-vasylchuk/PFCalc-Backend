package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.DishEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishDto {
    private Long id;
    private LocalDateTime cookedOn;
    private PfccDto pfcc;
    private String name;
    private Long foodId;
    private List<IngredientDto> ingredients;
    private BigDecimal recipeWeight;
    private BigDecimal cookedWeight;
    private Boolean deleted;

    public static DishDto fromDishEntity(DishEntity dishEntity)
    {
        return new DishDto(dishEntity.getId(), dishEntity.getCookedOn(),
                PfccDto.fromPfcc(dishEntity.getPfcc()),
                dishEntity.getName(),
                dishEntity.getFood().getId(),
                dishEntity.getIngredients().stream().map(IngredientDto::fromIngredientEntity).toList(),
                dishEntity.getRecipeWeight(),dishEntity.getCookedWeight(),
                dishEntity.getDeleted());
    }
}
