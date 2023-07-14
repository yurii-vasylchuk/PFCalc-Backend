package org.mvasylchuk.pfcc.platform.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_TOKEN_PREFIX =  "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(AUTH_HEADER);

            if (header == null) {
                log.debug("Can't found Authorization header");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Authorization header '{}'", header);

            if (!header.startsWith(BEARER_TOKEN_PREFIX)) {
                log.debug("Unsupported authorization token prefix");
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(BEARER_TOKEN_PREFIX.length());

            JwtAuthentication authenticationReq = JwtAuthentication.unauthenticated(token);

            Authentication authentication = this.authenticationManager.authenticate(authenticationReq);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.error("Failed to authenticate request.", e);
        }

        filterChain.doFilter(request, response);
    }
}
