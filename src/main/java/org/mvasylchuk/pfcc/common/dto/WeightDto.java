package org.mvasylchuk.pfcc.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeightDto {
    private BigDecimal measurementCount;
    private Long measurementId;
    private BigDecimal inGram;
    private String measurementName;
}
