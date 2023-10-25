package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DishIngredientPrimaryKey implements Serializable {
    @Column(name = "dish_id")
    private Long dishId;

    @Column(name = "ingredient_id")
    private Long ingredientId;
}
