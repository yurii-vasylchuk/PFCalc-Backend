package org.mvasylchuk.pfcc.domain.dto;

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
    private PfccDto pfcc;
    private String name;
    private Long foodId;
    private List<IngredientDto> ingredients;
    private BigDecimal recipeWeight;
    private BigDecimal cookedWeight;
    private Boolean deleted;
}
