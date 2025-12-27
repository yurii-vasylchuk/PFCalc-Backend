package org.mvasylchuk.pfcc.platform.jwt;

import io.jsonwebtoken.*;
import org.jetbrains.annotations.NotNull;
import org.mvasylchuk.pfcc.user.UserRole;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class PfccJwtHandler extends JwtHandlerAdapter<PfccAuthToken> implements JwtHandler<PfccAuthToken> {
    @Override
    public PfccAuthToken onClaimsJwt(Jwt<Header, Claims> jwt) {
        return parsePfccAuthToken(jwt.getBody());
    }

    @Override
    public PfccAuthToken onClaimsJws(Jws<Claims> jws) {
        return parsePfccAuthToken(jws.getBody());
    }

    @NotNull
    private PfccAuthToken parsePfccAuthToken(Claims body) {
        return new PfccAuthToken(
                body.get(PfccAuthToken.ID_CLAIM_NAME, Long.class),
                body.getSubject(),
                body.get(PfccAuthToken.ROLES_CLAIM_NAME, List.class)
                        .stream()
                        .map(o -> UserRole.valueOf((String) o))
                        .toList(),
                LocalDateTime.ofInstant(body.getExpiration().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(body.getNotBefore().toInstant(), ZoneId.systemDefault()),
                body.getIssuer()
        );
    }
}
