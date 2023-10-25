package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealDto {
    private Long id;
    private LocalDateTime eatenOn;
    private BigDecimal weight;
    private PfccDto pfcc;
    private Long foodId;
    private Long dishId;

}
