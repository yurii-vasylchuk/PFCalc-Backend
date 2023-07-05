package org.mvasylchuk.pfcc.user;

import io.jsonwebtoken.Jwts;
import org.mvasylchuk.pfcc.platform.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
@Component
public class TokenService {
    private final SecretKey key;

    public TokenService(@Qualifier(SecurityConfiguration.JWT_SIGNING_KEY_BEAN_NAME) SecretKey key) {
        this.key = key;
    }
    public String generateToken(UserEntity user){
        return Jwts.builder()
                .setIssuer("pfcc-app")
                .setSubject(user.getEmail())
                .signWith(this.key)
                .compact();
    }
}
