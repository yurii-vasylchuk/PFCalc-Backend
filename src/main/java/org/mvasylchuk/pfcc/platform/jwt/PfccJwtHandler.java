package org.mvasylchuk.pfcc.platform.jwt;

import io.jsonwebtoken.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class PfccJwtHandler extends JwtHandlerAdapter<PfccAuthToken> implements JwtHandler<PfccAuthToken> {
    @Override
    public PfccAuthToken onClaimsJwt(Jwt<Header, Claims> jwt) {
        Claims body = jwt.getBody();
        return new PfccAuthToken(
                body.get(PfccAuthToken.ID_CLAIM_NAME, Long.class),
                body.getSubject(),
                body.get(PfccAuthToken.ROLES_CLAIM_NAME, List.class),
                LocalDateTime.ofInstant(body.getExpiration().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(body.getNotBefore().toInstant(), ZoneId.systemDefault()),
                body.getIssuer());
    }

    @Override
    public PfccAuthToken onClaimsJws(Jws<Claims> jws) {
        Claims body = jws.getBody();
        return new PfccAuthToken(
                body.get(PfccAuthToken.ID_CLAIM_NAME, Long.class),
                body.getSubject(),
                body.get(PfccAuthToken.ROLES_CLAIM_NAME, List.class),
                LocalDateTime.ofInstant(body.getExpiration().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(body.getNotBefore().toInstant(), ZoneId.systemDefault()),
                body.getIssuer());
    }
}
