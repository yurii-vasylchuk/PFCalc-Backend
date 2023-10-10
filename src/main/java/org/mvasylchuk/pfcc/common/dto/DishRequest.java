package org.mvasylchuk.pfcc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.domain.dto.IngredientDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishRequest {
    private LocalDateTime cookedOn;
    private String name;
    private Long foodId;
    private List<IngredientDto> ingredients;
    private BigDecimal cookedWeight;
}
