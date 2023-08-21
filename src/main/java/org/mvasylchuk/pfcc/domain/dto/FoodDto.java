package org.mvasylchuk.pfcc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.entity.FoodType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
    private long id;
    private String name;
    private String description;
    private Pfcc pfcc;
    private Boolean isHidden;
    private FoodType foodType;
    private Boolean ownedByUser;
    private List<IngredientDto> ingredients;

}
