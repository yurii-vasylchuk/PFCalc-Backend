package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MealOptionDto {
    private Long foodId;
    private String name;
    private PfccDto pfcc;
    private FoodType type;
    private Boolean ownedByUser;
}
