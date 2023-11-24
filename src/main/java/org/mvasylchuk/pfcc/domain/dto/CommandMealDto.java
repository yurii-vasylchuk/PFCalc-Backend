package org.mvasylchuk.pfcc.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class CommandMealDto {
    private Long id;
    private LocalDateTime eatenOn;
    @Min(value = 0, message = "Weight should not be less than 0")
    private BigDecimal weight;
    @Valid
    private PfccDto pfcc;
    private Long foodId;
    private Long dishId;

}
