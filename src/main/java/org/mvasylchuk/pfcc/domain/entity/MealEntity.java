package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.common.jpa.Weight;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meal")
public class MealEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Embedded
    private Weight weight;

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
    @JoinColumn(name = "owner_id")
    private UserEntity user;

    @Column(name = "eaten_on")
    private LocalDateTime eatenOn;

    @OneToMany(mappedBy = "meal", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredientEntity> ingredients;
}
