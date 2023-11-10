package org.mvasylchuk.pfcc.api.glue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;
import org.jooq.DSLContext;
import org.mvasylchuk.pfcc.api.ApiTestContext;
import org.mvasylchuk.pfcc.api.constants.Constants.TestUser;
import org.mvasylchuk.pfcc.api.utils.PfccMatchersFactory;
import org.mvasylchuk.pfcc.jooq.tables.records.SecurityTokensRecord;
import org.mvasylchuk.pfcc.jooq.tables.records.UsersRecord;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccAppConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mvasylchuk.pfcc.api.constants.Constants.Db.FALSE;
import static org.mvasylchuk.pfcc.api.constants.Constants.Db.TRUE;
import static org.mvasylchuk.pfcc.jooq.Tables.SECURITY_TOKENS;
import static org.mvasylchuk.pfcc.jooq.Tables.USERS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationSteps {
    private final ApiTestContext ctx;
    private final DSLContext db;
    private final PfccMatchersFactory pfccMatchers;
    private final PfccAppConfigurationProperties conf;
    private final MockMvc api;

    @Then("User {string} has been saved in db")
    public void userHasBeenSavedInDb(String email) {
        assertThat(db.fetchCount(USERS, USERS.EMAIL.eq(email)))
                .isEqualTo(1);
    }

    @And("response contain proper auth tokens for user '{}'")
    public void responseContainJwtProperToken(TestUser user) throws Exception {
        Matcher<String> accessTokenMatcher = pfccMatchers.accessToken(user);
        Matcher<String> accessTokenExpirityMatcher = pfccMatchers.stringDateIsNearTo(
                LocalDateTime.now()
                             .plus(conf.jwt.authTokenExpiration), Duration.ofMillis(3_000));
        Matcher<String> refreshTokenMatcher = pfccMatchers.refreshToken(user);

        String name = "access-token";

        ctx.getPerformedCalls()
           .andExpect(jsonPath("$.data.refreshToken").value(refreshTokenMatcher))
           .andExpect(cookie().exists(name))
           .andExpect(cookie().httpOnly(name, true))
           .andExpect(cookie().domain(name, new URI(conf.jwt.issuer).getHost()))
           .andExpect(cookie().attribute(name, "Expires", accessTokenExpirityMatcher))
           .andExpect(cookie().path(name, "/"))
           .andExpect(cookie().value(name, accessTokenMatcher));
    }


    @Given("User '{}' with unconfirmed email is present")
    public void userWithUnconfirmedEmailIsPresent(TestUser user) {
        db.insertInto(USERS)
          .set(USERS.EMAIL, user.getEmail())
          .set(USERS.NAME, user.getName())
          .set(USERS.PASSWORD, user.getPassword())
          .set(USERS.PREFERRED_LANGUAGE, user.getPreferredLanguage().name())
          .set(USERS.PROFILE_CONFIGURED, (user.getProfileConfigured() ? TRUE : FALSE))
          .set(USERS.EMAIL_CONFIRMED, FALSE)
          .set(USERS.PROTEIN_AIM, user.getProteinAim())
          .set(USERS.FAT_AIM, user.getFatAim())
          .set(USERS.CARBOHYDRATES_AIM, user.getCarbohydratesAim())
          .set(USERS.CALORIES_AIM, user.getCaloriesAim())
          .set(USERS.ROLES, user.getRoles())
          .execute();
    }

    @And("Verify email token for {string} has been saved in db")
    public void verifyEmailTokenForHasBeenSavedInDb(String email) {
        List<SecurityTokensRecord> tokens = db.selectFrom(SECURITY_TOKENS.join(USERS)
                                                                         .on(USERS.ID.eq(SECURITY_TOKENS.USER_ID)))
                                              .where(USERS.EMAIL.eq(email))
                                              .and(SECURITY_TOKENS.TYPE.eq("EMAIL_VERIFICATION"))
                                              .fetchInto(SecurityTokensRecord.class);
        assertThat(tokens)
                .hasSize(1)
                .allSatisfy(token -> {
                    assertThat(token.getIsActive()).isEqualTo(TRUE);
                    assertThat(token.getType()).isEqualTo("EMAIL_VERIFICATION");
                });
    }

    @And("prepared request with that verification token")
    public void preparedRequestWithCorrectVerificationToken() {
        String verificationToken = this.ctx.getVerifyEmailToken();

        assertThat(verificationToken).isNotNull();

        this.ctx.setRequest("""
                {
                  "token": "%s"
                }
                """.formatted(verificationToken));
    }

    @And("verify email token is generated for user '{}'")
    public void verifyEmailTokenIsGeneratedForUserAlpha(TestUser user) {
        String code = "mx5aYiTZSXXYKz+UNBE4wjj1K3FMoaSH+QGeA0agUIfrQVnq7tQEFeK7mHqyts791p4jQhdH6ZBWPESSoYEXDqRECg9l6a0TqB02TX4NHxabPEaKFzjw4vQB19hZ32H+qZtNbTqRX7kiI4bk7dy1gdpVZOFKCMR1u+4tvJ2FOdc3Vrhd9G2XMygtJSYPfTZZYFVh3dGGKStbcWEQ4K9AIXaIHEKmMx16y4lQ0rYfUtVLT5JyN0NU71tJ946v4V3";

        db.insertInto(SECURITY_TOKENS)
          .set(SECURITY_TOKENS.TYPE, "EMAIL_VERIFICATION")
          .set(SECURITY_TOKENS.IS_ACTIVE, TRUE)
          .set(SECURITY_TOKENS.CODE, code)
          .set(SECURITY_TOKENS.USER_ID, db.select(USERS.ID).from(USERS).where(USERS.EMAIL.eq(user.getEmail())))
          .execute();

        ctx.setVerifyEmailToken(code);
    }


    @And("email of user '{}' become confirmed")
    public void userEmailBecomeConfirmed(TestUser user) {
        Boolean isEmailConfirmed = db.select(USERS.EMAIL_CONFIRMED)
                                     .from(USERS)
                                     .where(USERS.EMAIL.eq(user.getEmail()))
                                     .fetchOneInto(Boolean.class);

        assertThat(isEmailConfirmed)
                .isNotNull()
                .isTrue();
    }

    @Given("User '{}' with uncompleted profile is present")
    public void userAlphaWithUncompletedProfileIsPresent(TestUser user) {
        db.insertInto(USERS)
          .set(USERS.EMAIL, user.getEmail())
          .set(USERS.NAME, user.getName())
          .set(USERS.PASSWORD, user.getPassword())
          .set(USERS.PREFERRED_LANGUAGE, user.getPreferredLanguage().name())
          .set(USERS.PROFILE_CONFIGURED, FALSE)
          .set(USERS.EMAIL_CONFIRMED, user.getEmailConfirmed() ? TRUE : FALSE)
          .set(USERS.ROLES, user.getRoles())
          .execute();
    }

    @And("user '{}' should have completed profile with following aims")
    public void userAlphaShouldHaveCompletedProfileWithFollowingAims(TestUser user, Map<String, String> aims) {
        UsersRecord record = db.selectFrom(USERS)
                               .where(USERS.EMAIL.eq(user.getEmail()))
                               .fetchOne();

        assertNotNull(record);
        assertThat(record.getProfileConfigured()).isEqualTo(TRUE);
        assertThat(record.getProteinAim()).isEqualByComparingTo(aims.get("protein"));
        assertThat(record.getFatAim()).isEqualByComparingTo(aims.get("fat"));
        assertThat(record.getCarbohydratesAim()).isEqualByComparingTo(aims.get("carbohydrates"));
        assertThat(record.getCaloriesAim()).isEqualByComparingTo(aims.get("calories"));
    }

    @When("I'm refreshing auth token")
    public void iAmRefreshingAuthToken() throws Exception {
        MockHttpServletRequestBuilder req = post("/api/user/refresh-auth-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "refreshToken": "%s"
                            }
                        """.formatted(ctx.getRefreshToken()));

        ResultActions performedCall = api.perform(req);
        MockHttpServletResponse rsp = performedCall.andReturn().getResponse();
        log.info("Received response:\n{}\n\n{}",
                Arrays.stream(rsp.getCookies()).map(Cookie::toString).collect(Collectors.joining("")),
                rsp.getContentAsString());
        ctx.setPerformedCalls(performedCall);
    }
}
