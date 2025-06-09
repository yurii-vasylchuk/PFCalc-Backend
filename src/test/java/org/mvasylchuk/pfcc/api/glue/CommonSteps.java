package org.mvasylchuk.pfcc.api.glue;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.mvasylchuk.pfcc.api.ApiTestContext;
import org.mvasylchuk.pfcc.api.constants.Constants.TestUser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mvasylchuk.pfcc.jooq.Tables.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RequiredArgsConstructor
public class CommonSteps {
    private final MockMvc api;
    private final ApiTestContext ctx;
    private final DSLContext db;
    private final ObjectMapper mapper;

    @Before
    public void cleanUpDb() {
        List<Table<?>> tables = List.of(FOOD_INGREDIENTS, MEAL, FOOD, SECURITY_TOKENS, USERS, REPORTS);
        for (Table<?> table : tables) {
            db.delete(table)
                    .execute();
        }
    }

    @When("I'm sending POST request to {string}")
    public void iMSendingPOSTRequestTo(String path) throws Exception {
        MockHttpServletRequestBuilder req = post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ctx.getRequest());

        if (ctx.getAuthCookie() != null) {
            req.cookie(ctx.getAuthCookie());
        }

        ResultActions performedCall = api.perform(req);
        MockHttpServletResponse rsp = performedCall.andReturn().getResponse();
        log.info("Received response:\n{}\n\n{}",
                Arrays.stream(rsp.getCookies()).map(Cookie::toString).collect(Collectors.joining("")),
                rsp.getContentAsString());
        ctx.setPerformedCalls(performedCall);
    }

    @Then("I should receive successful response")
    public void iShouldReceiveResponseStatusCode() throws Exception {
        this.ctx.getPerformedCalls()
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Given("User '{}' is present")
    public void userIsPresent(TestUser user) {
        db.insertInto(USERS)
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.NAME, user.getName())
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.PREFERRED_LANGUAGE, user.getPreferredLanguage().name())
                .set(USERS.EMAIL_CONFIRMED, user.getEmailConfirmed())
                .set(USERS.PROTEIN_AIM, user.getProteinAim())
                .set(USERS.FAT_AIM, user.getFatAim())
                .set(USERS.CARBOHYDRATES_AIM, user.getCarbohydratesAim())
                .set(USERS.CALORIES_AIM, user.getCaloriesAim())
                .set(USERS.ROLES, user.getRoles())
                .execute();
    }

    @And("prepared request with following data:")
    public void preparedRequestWithFollowingData(String request) {
        ctx.setRequest(request);
    }

    @Then("Response should look like:")
    public void iGetResponseLike(String responseStr) throws Exception {
        ctx.getPerformedCalls()
                .andExpect(content().json(responseStr, false));
    }

    @And("I'm authenticated as '{}'")
    public void iMAuthenticatedAsAlpha(TestUser user) throws Exception {
        MockHttpServletResponse rsp = this.api.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(user.getEmail(), user.getPassword())))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andReturn().getResponse();

        String refreshToken = mapper.readValue(rsp.getContentAsString(), JsonNode.class)
                .at("/data/refreshToken")
                .asText();
        Cookie accessTokenCookie = rsp.getCookie("access-token");
        assertThat(accessTokenCookie).isNotNull();
        assertThat(refreshToken).isNotBlank();

        this.ctx.setAuthCookie(accessTokenCookie);
        this.ctx.setRefreshToken(refreshToken);
    }
}
