package org.mvasylchuk.pfcc.securitytoken;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SecurityTokenEntity {
    private static final String ID_GENERATOR_NAME = "security_token_id_gen";
    private static final String ID_SEQ_NAME = "security_token_id_seq";

    @Id
    @GeneratedValue(generator = ID_GENERATOR_NAME)
    @SequenceGenerator(name = ID_GENERATOR_NAME, sequenceName = ID_SEQ_NAME, allocationSize = 1)
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
}
