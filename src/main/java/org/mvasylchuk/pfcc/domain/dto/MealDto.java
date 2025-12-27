package org.mvasylchuk.pfcc.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.dto.WeightDto;
import org.mvasylchuk.pfcc.measurement.MeasurementDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealDto {
    @NotEmpty
    private String name;
    private Long id;
    private LocalDateTime eatenOn;
    @Valid
    private WeightDto weight;
    @Valid
    private PfccDto pfcc;
    private Long foodId;
    private List<MealIngredientDto> ingredients;
    private List<MeasurementDto> measurements;
}
