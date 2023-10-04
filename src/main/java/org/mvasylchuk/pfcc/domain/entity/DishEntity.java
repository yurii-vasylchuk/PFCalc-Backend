package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dish")
public class DishEntity {
    private static final String ID_GENERATOR_NAME = "dish_id_gen";
    private static final String ID_SEQ_NAME = "dish_id_seq";
    @Id
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    @SequenceGenerator(name = ID_GENERATOR_NAME, sequenceName = ID_SEQ_NAME, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private FoodEntity food;

    @Column(name = "recipe_weight")
    private BigDecimal recipeWeight;

    @Column(name = "cooked_weight")
    private BigDecimal cookedWeight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "protein")),
            @AttributeOverride(name = "fat", column = @Column(name = "fat")),
            @AttributeOverride(name = "carbohydrates", column = @Column(name = "carbohydrates")),
            @AttributeOverride(name = "calories", column = @Column(name = "calories"))
    })
    private Pfcc pfcc;
    @Column(name = "cooked_on")
    private LocalDateTime cookedOn;

    @Column(name = "deleted")
    private Boolean deleted;


}
