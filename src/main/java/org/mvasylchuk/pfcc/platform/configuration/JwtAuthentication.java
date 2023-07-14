package org.mvasylchuk.pfcc.platform.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtAuthentication implements Authentication {
    @Getter
    private final String jwtToken;
    @Getter
    private final PfccAuthToken decoded;
    private boolean isAuthenticated;

    public static JwtAuthentication authenticated(String jwtToken, PfccAuthToken decoded) {
        return new JwtAuthentication(jwtToken, decoded, true);
    }

    public static JwtAuthentication unauthenticated(String jwtToken) {
        return new JwtAuthentication(jwtToken, null, false);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return decoded.roles();
    }

    @Override
    public String getCredentials() {
        return jwtToken;
    }

    @Override
    public Object getDetails() {
        return jwtToken;
    }

    @Override
    public PfccAuthToken getPrincipal() {
        return decoded;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException();
        }
        this.isAuthenticated = false;
    }

    @Override
    public String getName() {
        return decoded.email();
    }
}
