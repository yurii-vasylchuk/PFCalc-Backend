package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Weight;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "food_ingredients")
public class FoodIngredientEntity {
    @EmbeddedId
    private FoodIngredientPrimaryKey id = new FoodIngredientPrimaryKey();

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private FoodEntity recipe;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private FoodEntity ingredient;

    @Embedded
    private Weight ingredientWeight;

    @Column(name = "ingredient_index")
    private Long ingredientIndex;

    @Column(name = "is_default")
    private Boolean isDefault;
}
