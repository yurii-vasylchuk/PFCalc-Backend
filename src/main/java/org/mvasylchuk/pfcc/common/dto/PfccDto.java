package org.mvasylchuk.pfcc.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PfccDto {
    @NotNull
    @PositiveOrZero
    private BigDecimal protein;
    @NotNull
    @PositiveOrZero
    private BigDecimal fat;
    @NotNull
    @PositiveOrZero
    private BigDecimal carbohydrates;
    @NotNull
    @PositiveOrZero
    private BigDecimal calories;


}
