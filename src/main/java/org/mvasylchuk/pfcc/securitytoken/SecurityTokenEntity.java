package org.mvasylchuk.pfcc.securitytoken;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_tokens")
@Getter
@Setter
@NoArgsConstructor
public class SecurityTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, updatable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private UserEntity user;

    @Column(name = "type", nullable = false, updatable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private SecurityTokenType type;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "valid_until")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime validUntil;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    public SecurityTokenEntity(Long id, String code, UserEntity user, SecurityTokenType type, Boolean isActive, LocalDateTime validUntil) {
        this.id = id;
        this.code = code;
        this.user = user;
        this.type = type;
        this.isActive = isActive;
        this.validUntil = validUntil;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }
}
