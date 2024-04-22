package org.mvasylchuk.pfcc.platform.jwt;

import org.mvasylchuk.pfcc.user.UserRole;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

public record PfccAuthToken(Long id,
                            String email,
                            List<UserRole> roles,
                            LocalDateTime expiration,
                            LocalDateTime notBefore,
                            String issuer) implements Principal {
    public static final String ID_CLAIM_NAME = "id";
    public static final String ROLES_CLAIM_NAME = "roles";

    @Override
    public String getName() {
        return this.email;
    }
}
