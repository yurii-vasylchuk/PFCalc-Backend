package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mvasylchuk.pfcc.common.dto.PfccDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MealOptionDto {
    private Long foodId;
    private Long dishId;
    private String name;
    private PfccDto pfcc;
    private MealOptionType type;
}
