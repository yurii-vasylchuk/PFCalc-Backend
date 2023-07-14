package org.mvasylchuk.pfcc.platform.jwt;

import org.mvasylchuk.pfcc.user.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public record PfccAuthToken(Long id,
                            String email,
                            List<UserRole> roles,
                            LocalDateTime expiration,
                            LocalDateTime notBefore,
                            String issuer) {
    public static final String ID_CLAIM_NAME = "id";
    public static final String ROLES_CLAIM_NAME = "roles";
}
