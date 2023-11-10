package org.mvasylchuk.pfcc.platform.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (request.getCookies() == null) {
                log.debug("Cookies is empty, skipping authorization");
                //NOT Authenticated
                SecurityContextHolder.getContext().setAuthentication(null);
                filterChain.doFilter(request, response);
                return;
            }

            Cookie authCookie = Arrays.stream(request.getCookies())
                                      .filter(c -> c.getName().equals("access-token"))
                                      .findFirst()
                                      .orElse(null);

            if (authCookie == null) {
                log.debug("Can't found Authentication cookie");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Authorization cookie '{}'", authCookie);

            String token = authCookie.getValue();

            JwtAuthentication authenticationReq = JwtAuthentication.unauthenticated(token);

            Authentication authentication = this.authenticationManager.authenticate(authenticationReq);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Failed to authenticate request.", e);
        }

        filterChain.doFilter(request, response);
    }
}
