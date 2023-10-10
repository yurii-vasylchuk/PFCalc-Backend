package org.mvasylchuk.pfcc.api.glue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class AuthenticationScenario {
    private final MockMvc api;

    private ResultActions performedCalls;
    @Given("I make a GET call on \\/status")
    public void makeStatusCall() throws Exception {
        performedCalls = api.perform(get("/actuator/health"));
    }

    @Then("I should receive {int} response status code")
    public void iShouldReceiveResponseStatusCode(int expectedStatusCode) throws Exception {
        performedCalls.andExpect(status().is(expectedStatusCode));
    }
}
