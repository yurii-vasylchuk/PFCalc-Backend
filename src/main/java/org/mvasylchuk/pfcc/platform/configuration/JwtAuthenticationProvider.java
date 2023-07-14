package org.mvasylchuk.pfcc.platform.configuration;

import io.jsonwebtoken.Jwts;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccSecurityConfigurationProperties;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.mvasylchuk.pfcc.platform.jwt.PfccJwtHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final PfccSecurityConfigurationProperties configuration;
    private final KeyPair rsaKey;
    private final PfccJwtHandler pfccHandler;

    public JwtAuthenticationProvider(PfccSecurityConfigurationProperties configuration,
                                     @Qualifier(SecurityConfiguration.JWT_SIGNING_KEY_BEAN_NAME) KeyPair rsaKey) {
        this.configuration = configuration;
        this.rsaKey = rsaKey;

        this.pfccHandler = new PfccJwtHandler();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        if (jwtAuthentication == null) {
            throw new BadCredentialsException("Authentication is null");
        }

        PfccAuthToken decodedToken = Jwts.parserBuilder()
                .setSigningKey(rsaKey.getPublic())
                .build()
                .parse(jwtAuthentication.getCredentials(), pfccHandler);

        if (decodedToken.expiration().isBefore(LocalDateTime.now())) {
            throw new CredentialsExpiredException("Token is expired");
        }

        if (!decodedToken.issuer().equals(configuration.jwt.issuer)) {
            throw new BadCredentialsException("Invalid issuer");
        }

        if (decodedToken.notBefore().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Token's 'not before' is in future");
        }

        return JwtAuthentication.authenticated(jwtAuthentication.getCredentials(), decodedToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthentication.class);
    }
}
