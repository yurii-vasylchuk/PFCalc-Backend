package org.mvasylchuk.pfcc.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "food")
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private FoodType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "protein", nullable = false)),
            @AttributeOverride(name = "fat", column = @Column(name = "fat", nullable = false)),
            @AttributeOverride(name = "carbohydrates", column = @Column(name = "carbohydrates", nullable = false)),
            @AttributeOverride(name = "calories", column = @Column(name = "calories", nullable = false))
    })
    private Pfcc pfcc;

    @Column(name = "is_hidden")
    private Boolean isHidden;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Column(name = "description")
    private String description;

    @Column(name = "deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    List<FoodIngredientEntity> ingredients;
}
