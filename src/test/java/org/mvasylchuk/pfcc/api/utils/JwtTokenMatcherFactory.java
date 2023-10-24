package org.mvasylchuk.pfcc.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.api.constants.Constants.TestUser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mvasylchuk.pfcc.jooq.Tables.USERS;

@Component
@RequiredArgsConstructor
public class JwtTokenMatcherFactory {
    private final ObjectMapper mapper;
    private final DSLContext db;

    public Matcher<String> getMatcher(TestUser user) {
        return new JwtTokenMatcher(user, mapper, db);
    }

    @RequiredArgsConstructor
    private static class JwtTokenMatcher extends BaseMatcher<String> {
        private final TestUser user;
        private final ObjectMapper mapper;
        private final DSLContext db;

        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String)) {
                return false;
            }

            JsonNode claims;
            try {
                claims = mapper.readValue(
                        Base64.getDecoder().decode(((String) actual).split("\\.")[1]),
                        JsonNode.class
                );
            } catch (IOException e) {
                return false;
            }

            assertEquals("http://localhost:8080", claims.get("iss").asText());
            assertEquals(user.getEmail(), claims.get("sub").asText());
            Long expectedId = db.select(USERS.ID).from(USERS).where(USERS.EMAIL.eq(user.getEmail())).fetchOne(USERS.ID);
            assertEquals((long) expectedId, claims.get("id").asLong());

            JsonNode rolesNode = claims.get("roles");
            assertTrue(rolesNode.isArray());

            List<String> roles = new ArrayList<>();
            for (JsonNode roleNode : rolesNode) {
                roles.add(roleNode.asText());
            }
            assertThat(roles).containsExactlyInAnyOrder(user.getRoles().split(","));

            return true;
        }

        @Override
        public void describeTo(Description description) {
//TODO: implement
        }
    }
}
