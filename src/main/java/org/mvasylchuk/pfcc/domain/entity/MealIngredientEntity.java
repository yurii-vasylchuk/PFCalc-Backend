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
@Table(name = "meal_ingredients")
public class MealIngredientEntity {
    @EmbeddedId
    private MealIngredientPrimaryKey id = new MealIngredientPrimaryKey();

    @ManyToOne
    @MapsId("mealId")
    @JoinColumn(name = "meal_id")
    private MealEntity meal;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private FoodEntity food;

    @Embedded
    private Weight ingredientWeight;

    @Column(name = "ingredient_index")
    private Long ingredientIndex;
}
