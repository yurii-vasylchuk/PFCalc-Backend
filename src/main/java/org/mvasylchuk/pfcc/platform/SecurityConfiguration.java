package org.mvasylchuk.pfcc.platform;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@Configuration
public class SecurityConfiguration {
    public static final String JWT_SIGNING_KEY_BEAN_NAME = "JWT_SIGNING_SECRET_KEY";
    private final SecretKey key;

    public SecurityConfiguration(@Value("${pfcc.jwt.signing-key}") String signingKeyRaw) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signingKeyRaw));
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(JWT_SIGNING_KEY_BEAN_NAME)
    public SecretKey jwtSigningKey() {
        return this.key;
    }

}
