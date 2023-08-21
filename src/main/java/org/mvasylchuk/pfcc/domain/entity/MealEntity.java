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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meal")
public class MealEntity {
    private static final String ID_GENERATOR_NAME = "meal_id_gen";
    private static final String ID_SEQ_NAME = "meal_id_seq";
    @Id
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    @SequenceGenerator(name = ID_GENERATOR_NAME, sequenceName = ID_SEQ_NAME)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "weight")
    private BigDecimal weight;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "protein")),
            @AttributeOverride(name = "fat", column = @Column(name = "fat")),
            @AttributeOverride(name = "carbohydrates", column = @Column(name = "carbohydrates")),
            @AttributeOverride(name = "calories", column = @Column(name = "calories"))
    })
    private Pfcc pfcc;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private FoodEntity food;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private DishEntity dish;

    @Column(name = "eaten_on")
    private LocalDateTime eatenOn;
}
