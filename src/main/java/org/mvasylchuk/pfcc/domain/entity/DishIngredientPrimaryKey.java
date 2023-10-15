package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class DishIngredientPrimaryKey implements Serializable {
    @Column(name = "dish_id")
    private Long dishId;

    @Column(name = "ingredient_id")
    private Long ingredientId;
}
