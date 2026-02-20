package org.example.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.component.dto.ResponseDto;
import org.example.dto.TraineeRegistrationRequest;
import org.example.service.TraineeService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TraineeCreationSteps {

    private final MockMvc mockMvc;
    private final TraineeService traineeService;
    private final ResponseDto responseDto;

    public TraineeCreationSteps(MockMvc mockMvc, TraineeService traineeService, ResponseDto responseDto) {
        this.mockMvc = mockMvc;
        this.traineeService = traineeService;
        this.responseDto = responseDto;
    }

    @Then("the trainee {string} should {string} be persisted")
    public void theTraineeShouldBePersisted(String username, String state) {
        if (!state.isEmpty()) {
            assertThrows(IllegalArgumentException.class,
                    () -> traineeService.selectTraineeByUsername(username));
        } else {
            assertDoesNotThrow(() -> traineeService.selectTraineeByUsername(username));
        }
    }

    @When("the user tries to create a trainee with first name {string}, last name {string} and address {string}")
    public void theUserTriesToCreateATraineeWithFirstNameLastNameAndAddress(String firstName, String lastName, String address) throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setDateOfBirth(LocalDate.now());
        request.setAddress(address);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/trainees")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        responseDto.setResult(result);
    }
}
