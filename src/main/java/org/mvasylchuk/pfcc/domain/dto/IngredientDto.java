package org.mvasylchuk.pfcc.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.entity.DishIngredientEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class IngredientDto extends FoodDto {
    private BigDecimal ingredientWeight;


    public IngredientDto(long id, String name, String description, Pfcc pfcc, Boolean isHidden, FoodType foodType,
                         Boolean ownedByUser, List<IngredientDto> ingredients, BigDecimal ingredientWeight) {
        super(id, name, description, PfccDto.fromPfcc(pfcc), isHidden, foodType, ownedByUser, ingredients);
        this.ingredientWeight = ingredientWeight;
    }

    public static IngredientDto fromIngredientEntity(DishIngredientEntity i) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        FoodEntity ing = i.getIngredient();
        Boolean ownedByUser = auth != null &&
                auth.isAuthenticated() &&
                Objects.equals(((PfccAuthToken) auth.getPrincipal()).id(), ing.getOwner().getId());

        return new IngredientDto(ing.getId(),
                ing.getName(),
                ing.getDescription(),
                ing.getPfcc(),
                ing.getIsHidden(),
                ing.getType(),
                ownedByUser,
                null,
                i.getIngredientWeight());
    }
}
