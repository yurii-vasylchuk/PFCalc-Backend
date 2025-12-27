package org.mvasylchuk.pfcc.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.dto.WeightDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.measurement.MeasurementDto;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MealIngredientDto extends FoodDto {
    @NotNull
    private WeightDto ingredientWeight;
    @NotNull
    @PositiveOrZero
    private Long ingredientIndex;

    public MealIngredientDto(long id,
                             Long ingredientIndex,
                             String name,
                             String description,
                             PfccDto pfcc,
                             Boolean isHidden,
                             FoodType foodType,
                             Boolean ownedByUser,
                             WeightDto ingredientWeight,
                             List<MeasurementDto> measurements) {
        super(id, name, description, pfcc, isHidden, foodType, ownedByUser, Collections.emptyList(), measurements);
        this.ingredientWeight = ingredientWeight;
        this.ingredientIndex = ingredientIndex;
    }
}
