package org.mvasylchuk.pfcc.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IngredientDto extends FoodDto {
    private BigDecimal ingredientWeight;


    public IngredientDto(long id, String name, String description, PfccDto pfcc, Boolean isHidden, FoodType foodType,
                         Boolean ownedByUser, List<IngredientDto> ingredients, BigDecimal ingredientWeight) {
        super(id, name, description, pfcc, isHidden, foodType, ownedByUser, ingredients);
        this.ingredientWeight = ingredientWeight;
    }

}
