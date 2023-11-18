package org.mvasylchuk.pfcc.util;

import org.mvasylchuk.pfcc.platform.configuration.JwtAuthentication;
import org.mvasylchuk.pfcc.platform.jwt.PfccAuthToken;
import org.mvasylchuk.pfcc.user.UserRole;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDateTime;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithTestUser.SecurityContextFactory.class)
public @interface WithTestUser {
    long id();

    String email();

    String[] roles();

    class SecurityContextFactory implements WithSecurityContextFactory<WithTestUser> {
        @Override
        public SecurityContext createSecurityContext(WithTestUser annotation) {
            PfccAuthToken authToken = new PfccAuthToken(annotation.id(),
                    annotation.email(),
                    Arrays.stream(annotation.roles()).map(UserRole::valueOf).toList(),
                    LocalDateTime.now().plusMinutes(10),
                    LocalDateTime.now().minusMinutes(5),
                    "http://localhost:8080"
            );
            return new SecurityContextImpl(JwtAuthentication.authenticated("header.body.sign", authToken));

        }
    }
}
