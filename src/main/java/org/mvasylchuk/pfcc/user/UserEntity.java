package org.mvasylchuk.pfcc.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "preferred_language")
    @Enumerated(value = EnumType.STRING)
    private Language preferredLanguage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "protein", column = @Column(name = "protein_aim")),
            @AttributeOverride(name = "fat", column = @Column(name = "fat_aim")),
            @AttributeOverride(name = "carbohydrates", column = @Column(name = "carbohydrates_aim")),
            @AttributeOverride(name = "calories", column = @Column(name = "calories_aim"))
    })
    private Pfcc aims;

    @Column(name = "email_confirmed")
    private Boolean emailConfirmed;

    @Column(name = "roles")
    @Convert(converter = UserRoleConverter.class)
    private List<UserRole> roles;
}
