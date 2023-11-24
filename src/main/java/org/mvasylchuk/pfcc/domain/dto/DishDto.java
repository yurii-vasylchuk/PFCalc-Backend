package org.mvasylchuk.pfcc.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;

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
    @Valid
    private PfccDto pfcc;
    @NotEmpty
    private String name;
    private Long foodId;
    private List<IngredientDto> ingredients;
    @Min(value = 0, message = "Weight should not be less than 0")
    private BigDecimal recipeWeight;
    @Min(value = 0, message = "Weight should not be less than 0")
    private BigDecimal cookedWeight;
    private Boolean deleted;

}
