package org.mvasylchuk.pfcc.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.measurement.MeasurementDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
    private Long id;
    @NotEmpty
    private String name;
    private String description;
    @Valid
    private PfccDto pfcc;
    private boolean isHidden;
    @NotNull
    private FoodType type;
    private Boolean ownedByUser;
    private List<FoodIngredientDto> ingredients;
    private List<MeasurementDto> measurements;
}
