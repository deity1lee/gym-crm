package org.example.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.example.component.dto.ResponseDto;
import org.example.utilities.Credentials;
import org.example.dto.ChangePasswordRequest;
import org.example.service.TraineeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class PasswordChangeSteps {

    private final MockMvc mockMvc;
    private final TraineeService traineeService;
    private final Credentials credentials;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;
    private final ResponseDto responseDto;

    public PasswordChangeSteps(MockMvc mockMvc, TraineeService traineeService, Credentials credentials, BCryptPasswordEncoder bCryptPasswordEncoder, ObjectMapper objectMapper, ResponseDto responseDto) {
        this.mockMvc = mockMvc;
        this.traineeService = traineeService;
        this.credentials = credentials;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
        this.responseDto = responseDto;
    }

    @When("the user tries to change password to valid {string} password")
    public void theUserTriesToChangeHerHisPasswordToValidPassword(String newPassword) throws Exception {

        ChangePasswordRequest request = new ChangePasswordRequest(
                credentials.getUserCredentials().getPassword(),
                newPassword
        );

        MvcResult result = mockMvc.perform(
                put("/users/{username}/password", credentials.getUserCredentials().getUsername())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-Username", credentials.getUserCredentials().getUsername())
                        .header("X-Auth-Password", credentials.getUserCredentials().getPassword())
                        .header(HttpHeaders.AUTHORIZATION, credentials.getJwt())
        ).andReturn();
        responseDto.setResult(result);
    }

    @And("password should be changed to {string}")
    public void usersPasswordShouldBeChanged(String newPassword) {
        String storedPassword = traineeService
                .selectTraineeByUsername(credentials.getUserCredentials().getUsername())
                .getPassword();

        assertThat(bCryptPasswordEncoder.matches(newPassword, storedPassword)).isTrue();
    }

    @And("password should not be changed")
    public void passwordShouldNotBeChanged() {
        String storedPassword = traineeService
                .selectTraineeByUsername(credentials.getUserCredentials().getUsername())
                .getPassword();

        assertThat(bCryptPasswordEncoder.matches(credentials.getUserCredentials().getPassword(), storedPassword)).isTrue();
    }
}
