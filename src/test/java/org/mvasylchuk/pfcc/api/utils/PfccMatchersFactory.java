package org.mvasylchuk.pfcc.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.api.constants.Constants.TestUser;
import org.mvasylchuk.pfcc.jooq.tables.records.SecurityTokensRecord;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.trilead.ssh2.crypto.Base64;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.mvasylchuk.pfcc.jooq.Tables.SECURITY_TOKENS;
import static org.mvasylchuk.pfcc.jooq.Tables.USERS;

@Component
@RequiredArgsConstructor
public class PfccMatchersFactory {
    private final ObjectMapper mapper;
    private final DSLContext db;

    public Matcher<String> accessToken(TestUser user, LocalDateTime expectedExp, Duration expGap) {
        return new AccessTokenMatcher(user, expectedExp, expGap, mapper, db);
    }

    public Matcher<String> stringDateIsNearTo(ZonedDateTime expected, Duration gap) {
        return new StringDateIsNearToMatcher(expected, gap);
    }

    public Matcher<String> refreshToken(TestUser user) {
        return new RefreshTokenMatcher(user, db);
    }

    @RequiredArgsConstructor
    private static class StringDateIsNearToMatcher extends BaseMatcher<String> {
        private final ZonedDateTime expected;
        private final Duration gap;


        @Override
        public boolean matches(Object actualStr) {
            if (!(actualStr instanceof String)) {
                return false;
            }

            ZonedDateTime actual;
            try {
                actual = ZonedDateTime.parse((String) actualStr, DateTimeFormatter.RFC_1123_DATE_TIME);
            } catch (Exception e) {
                return false;
            }
            actual = actual.withZoneSameInstant(expected.getZone());

            ZonedDateTime lowerBoundary = expected.minus(gap);
            ZonedDateTime upperBoundary = expected.plus(gap);
            return (lowerBoundary.isBefore(actual) || expected.isEqual(actual)) &&
                    (upperBoundary.isAfter(actual) || expected.isEqual(actual));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(this.expected.format(DateTimeFormatter.ofPattern("EEE, dd LLL yyyy hh:mm:ss VV", Locale.US)))
                    .appendText(", with gap ")
                    .appendText(gap.toString());
        }
    }

    @RequiredArgsConstructor
    private static class AccessTokenMatcher extends BaseMatcher<String> {
        private final TestUser user;
        private final LocalDateTime expectedExp;
        private final Duration expGap;
        private final ObjectMapper mapper;
        private final DSLContext db;


        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String)) {
                return false;
            }

            JsonNode claims;
            try {
                claims = mapper.readValue(Base64.decode(((String) actual).split("\\.")[1].toCharArray()), JsonNode.class);
            } catch (Exception e) {
                return false;
            }


            Long expectedId = db.select(USERS.ID).from(USERS).where(USERS.EMAIL.eq(user.getEmail())).fetchOne(USERS.ID);
            JsonNode rolesNode = claims.get("roles");

            if (!Objects.equals("http://localhost:8080", claims.get("iss").asText()) &&
                    Objects.equals(user.getEmail(), claims.get("sub").asText()) &&
                    Objects.equals(expectedId, claims.get("id").asLong()) &&
                    rolesNode.isArray()) {
                return false;
            }


            LocalDateTime exp = LocalDateTime.ofEpochSecond(
                    claims.get("exp").asLong(),
                    0,
                    OffsetDateTime.now().getOffset());
            LocalDateTime lowerBoundary = expectedExp.minus(expGap);
            LocalDateTime upperBoundary = expectedExp.plus(expGap);
            if (lowerBoundary.isAfter(exp) || upperBoundary.isBefore(exp)) {
                return false;
            }

            List<String> roles = new ArrayList<>();
            for (JsonNode roleNode : rolesNode) {
                roles.add(roleNode.asText());
            }

            return Matchers.containsInAnyOrder(user.getRoles().split(",")).matches(roles);
        }

        @Override
        public void describeTo(Description description) {
            //TODO: implement
        }
    }

    @RequiredArgsConstructor
    private static class RefreshTokenMatcher extends BaseMatcher<String> {
        private final TestUser user;
        private final DSLContext db;

        @Override
        public boolean matches(Object actual) {
            if (!(actual instanceof String)) {
                return false;
            }
            Long expectedId = db.select(USERS.ID).from(USERS).where(USERS.EMAIL.eq(user.getEmail())).fetchOne(USERS.ID);
            SecurityTokensRecord token = db.selectFrom(SECURITY_TOKENS)
                    .where(SECURITY_TOKENS.CODE.eq((String) actual))
                    .and(SECURITY_TOKENS.TYPE.eq("REFRESH_TOKEN"))
                    .fetchAny();

            return token != null &&
                    token.getIsActive().equals((byte) 1) &&
                    token.getUserId().equals(expectedId);

        }

        @Override
        public void describeTo(Description description) {
            //TODO: implement
        }
    }
}
