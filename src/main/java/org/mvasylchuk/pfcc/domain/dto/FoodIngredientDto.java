package org.mvasylchuk.pfcc.domain.dto;

import jakarta.validation.constraints.Min;
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
public class FoodIngredientDto extends FoodDto {
    @Min(value = 0, message = "Weight should not be less than 0")
    private BigDecimal ingredientWeight;
    private Long ingredientIndex;


    public FoodIngredientDto(long id,
                             Long ingredientIndex,
                             String name,
                             String description,
                             PfccDto pfcc,
                             Boolean isHidden,
                             FoodType foodType,
                             Boolean ownedByUser,
                             List<FoodIngredientDto> ingredients,
                             BigDecimal ingredientWeight) {
        super(id, name, description, pfcc, isHidden, foodType, ownedByUser, ingredients);
        this.ingredientWeight = ingredientWeight;
        this.ingredientIndex = ingredientIndex;
    }

}
