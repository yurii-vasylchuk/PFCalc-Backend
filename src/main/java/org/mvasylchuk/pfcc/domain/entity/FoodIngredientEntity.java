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
@Table(name = "food_ingredients")
public class FoodIngredientEntity {
    @EmbeddedId
    private IngredientPrimaryKey id = new IngredientPrimaryKey();

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private FoodEntity recipe;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private FoodEntity ingredient;

    @Column(name = "ingredient_weight")
    private BigDecimal ingredientWeight;
    @Column(name="ingredient_index")
    private Long ingredientIndex;

}
