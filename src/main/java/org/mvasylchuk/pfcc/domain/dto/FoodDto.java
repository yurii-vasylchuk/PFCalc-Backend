package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.entity.FoodType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
    private Long id;
    private String name;
    private String description;
    private PfccDto pfcc;
    private boolean isHidden ;
    private FoodType type;
    private Boolean ownedByUser;
    private List<IngredientDto> ingredients;


}
