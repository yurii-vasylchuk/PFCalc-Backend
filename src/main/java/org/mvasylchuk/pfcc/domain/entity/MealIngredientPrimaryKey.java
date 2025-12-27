package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MealIngredientPrimaryKey implements Serializable {
    @Column(name = "meal_id")
    private Long mealId;

    @Column(name = "ingredient_id")
    private Long ingredientId;
}
