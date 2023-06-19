package org.mvasylchuk.pfcc.user;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

class TokenServiceTest {

    @Test
    void generateToken() {
        String rawKey = "fpgomhkkkkkkkkkkkkkkkkkkkkkkkkkkkgfkgkgkgkgflgfhkkgmhklgmbklmlfgld";
        String email = "email1@test.com";

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(rawKey));
        TokenService tokenService = new TokenService(key);
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(email);
        String actual = tokenService.generateToken(user);
        Assertions.assertThat(actual).isEqualTo("eyJhbGciOiJIUzM4NCJ9.eyJpc3MiOiJwZmNjLWFwcCIsInN1YiI6ImVtYWlsMUB0ZXN0LmNvbSJ9.FBISXmKh9ycrG91KCypDtIBYp1GQWYjBVP4HdZP_g4WT0pAk2el_uIKKb1fFpwd_");
    }
}