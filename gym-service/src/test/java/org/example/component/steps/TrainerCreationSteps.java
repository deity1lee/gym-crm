package org.example.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.component.dto.ResponseDto;
import org.example.dto.TrainerRegistrationRequest;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainerCreationSteps {

    private final MockMvc mockMvc;
    private final ResponseDto responseDto;
    private final TrainingService trainingService;
    private final TrainerService trainerService;

    public TrainerCreationSteps(MockMvc mockMvc, ResponseDto responseDto, TrainingService trainingService, TrainerService trainerService) {
        this.mockMvc = mockMvc;
        this.responseDto = responseDto;
        this.trainingService = trainingService;
        this.trainerService = trainerService;
    }

    @When("the user tries to create a trainer with first name {string}, last name {string}")
    public void theUserTriesToCreateATrainerWithFirstNameLastName(String firstName, String lastName) throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(firstName, lastName, trainingService.findTrainingTypeById(1L));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/trainers")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        responseDto.setResult(result);
    }

    @Then("the trainer {string} should {string} be persisted")
    public void theTrainerShouldBePersisted(String username, String state) {
        if (!state.isEmpty()) {
            assertThrows(IllegalArgumentException.class,
                    () -> trainerService.selectTrainerByUsername(username));
        } else {
            assertDoesNotThrow(() -> trainerService.selectTrainerByUsername(username));
        }
    }
}
