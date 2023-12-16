package org.mvasylchuk.pfcc.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
