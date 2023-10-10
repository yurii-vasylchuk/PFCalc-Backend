package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dish_ingredients")
public class DishIngredientEntity {
    @EmbeddedId
    private DishIngredientPrimaryKey id = new DishIngredientPrimaryKey();

    @ManyToOne
    @MapsId("dishId")
    @JoinColumn(name = "dish_id")
    private DishEntity dish;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private FoodEntity ingredient;

    @Column(name = "ingredient_weight")
    private BigDecimal ingredientWeight;

}
