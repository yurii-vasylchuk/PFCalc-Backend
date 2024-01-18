package org.mvasylchuk.pfcc.measurement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementDto {
    private Long id;
    private Long foodId;
    private BigDecimal toGramMultiplier;
    private String name;
    private BigDecimal defaultValue;

}
