package org.mvasylchuk.pfcc.api.glue;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.mvasylchuk.pfcc.api.ApiTestContext;
import org.mvasylchuk.pfcc.api.constants.Constants.TestUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mvasylchuk.pfcc.api.constants.Constants.Db.FALSE;
import static org.mvasylchuk.pfcc.api.constants.Constants.Db.TRUE;
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
        List<Table<?>> tables = List.of(DISH_INGREDIENTS, INGREDIENTS, MEAL, DISH, FOOD, SECURITY_TOKENS, USERS);
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

        if (ctx.getAuthToken() != null) {
            req.header("Authorization", "Bearer %s".formatted(ctx.getAuthToken()));
        }

        ResultActions performedCall = api.perform(req);
        log.info("Received response:\n{}", performedCall.andReturn().getResponse().getContentAsString());
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
    public void userYvaIsPresent(TestUser user) {
        db.insertInto(USERS)
          .set(USERS.EMAIL, user.getEmail())
          .set(USERS.NAME, user.getName())
          .set(USERS.PASSWORD, user.getPassword())
          .set(USERS.PREFERRED_LANGUAGE, user.getPreferredLanguage().name())
          .set(USERS.PROFILE_CONFIGURED, user.getProfileConfigured() ? TRUE : FALSE)
          .set(USERS.EMAIL_CONFIRMED, user.getEmailConfirmed() ? TRUE : FALSE)
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
        String loginRsp = this.api.perform(post("/api/user/login")
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
                                  .andReturn().getResponse().getContentAsString();

        String authToken = mapper.readValue(loginRsp, JsonNode.class).at("/data/token").asText();

        this.ctx.setAuthToken(authToken);
    }
}
