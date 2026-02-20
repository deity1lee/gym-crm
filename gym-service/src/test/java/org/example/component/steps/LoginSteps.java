package org.example.component.steps;

import io.cucumber.java.en.*;
import org.example.utilities.Credentials;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class LoginSteps {

    private final MockMvc mockMvc;
    private final Credentials credentials;
    private MvcResult result;

    public LoginSteps(MockMvc mockMvc, Credentials credentials) {
        this.mockMvc = mockMvc;
        this.credentials = credentials;
    }

    @When("the user logs in with provided credentials")
    public void loginRequest() throws Exception {

        result = mockMvc.perform(
                get("/auth/login")
                        .header("X-Auth-Username", credentials.getUserCredentials().getUsername())
                        .header("X-Auth-Password", credentials.getUserCredentials().getPassword())
        ).andReturn();
    }

    @Then("the login response status should be {int}")
    public void verifyStatus(int status) {
        assertThat(result.getResponse().getStatus()).isEqualTo(status);
    }

    @And("the response should contain a JWT token")
    public void responseContainsJwt() {

        String authHeader = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);

        assertThat(authHeader).isNotNull();
        assertThat(authHeader).startsWith("Bearer ");
    }

    @Given("a user that does not exist")
    public void aUserThatDoesNotExist() {}

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String username, String password) throws Exception {
        result = mockMvc.perform(
                get("/auth/login")
                        .header("X-Auth-Username", username)
                        .header("X-Auth-Password", password)
        ).andReturn();
    }

    @And("the response should not contain a JWT token")
    public void theResponseShouldNotContainAJWTToken() {
        String authHeader = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);

        assertThat(authHeader == null || !authHeader.startsWith("Bearer")).isTrue();
    }
}
