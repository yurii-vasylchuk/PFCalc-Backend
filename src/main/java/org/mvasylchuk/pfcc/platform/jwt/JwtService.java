package org.mvasylchuk.pfcc.platform.jwt;

import io.jsonwebtoken.Jwts;
import org.mvasylchuk.pfcc.platform.configuration.SecurityConfiguration;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.mvasylchuk.pfcc.user.UserEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final PfccAppConfigurationProperties configuration;
    private final KeyPair rsaKey;

    public JwtService(PfccAppConfigurationProperties configuration,
                      @Qualifier(SecurityConfiguration.JWT_SIGNING_KEY_BEAN_NAME) KeyPair rsaKey) {
        this.configuration = configuration;
        this.rsaKey = rsaKey;
    }

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setIssuer(this.configuration.auth.issuer)
                .setSubject(user.getEmail())
                .setNotBefore(new Date())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(
                        LocalDateTime.now()
                                .atZone(ZoneId.systemDefault())
                                .plus(configuration.auth.authTokenExpiration)
                                .toInstant()))
                .addClaims(Map.of(
                        PfccAuthToken.ID_CLAIM_NAME, user.getId(),
                        PfccAuthToken.ROLES_CLAIM_NAME, user.getRoles()))
                .signWith(rsaKey.getPrivate())
                .compact();
    }
}
