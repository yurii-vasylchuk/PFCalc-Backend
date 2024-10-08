package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dish")
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "dish", cascade = {CascadeType.ALL})
    List<DishIngredientEntity> ingredients;

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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;
}
