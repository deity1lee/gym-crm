package org.example.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.component.dto.ResponseDto;
import org.example.dto.TrainingCreationRequest;
import org.example.entities.Training;
import org.example.integration.dto.TrainingInfoDto;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.utilities.Credentials;
import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingCreationSteps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MockMvc mockMvc;
    private final Credentials credentials;
    private final ResponseDto responseDto;
    private final TrainingService trainingService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingInfoDto trainingInfo;

    public TrainingCreationSteps(MockMvc mockMvc, Credentials credentials, ResponseDto responseDto, TrainingService trainingService, TrainerService trainerService, TraineeService traineeService, TrainingInfoDto trainingInfo) {
        this.mockMvc = mockMvc;
        this.credentials = credentials;
        this.responseDto = responseDto;
        this.trainingService = trainingService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingInfo = trainingInfo;
    }
    @When("the user creates a training for trainee {string} and trainer {string} on {string}")
    public void createTraining(String traineeUsername, String trainerUsername, String date) throws Exception {
        TrainingCreationRequest trainingCreationRequest = new TrainingCreationRequest();
        trainingCreationRequest.setTraineeUsername(traineeUsername);
        trainingCreationRequest.setTrainerUsername(trainerUsername);
        trainingCreationRequest.setTrainingName("Strength");
        trainingCreationRequest.setTrainingTypeId(1L);
        trainingCreationRequest.setTrainingDate(LocalDate.parse(date, FORMATTER));
        trainingCreationRequest.setTrainingDuration(6L);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequest = objectMapper.writeValueAsString(trainingCreationRequest);

        MvcResult result = mockMvc.perform(post("/trainings")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, credentials.getJwt()))
                .andReturn();
        responseDto.setResult(result);
        trainingInfo.setTrainingCreationRequest(trainingCreationRequest);
    }

    @Then("training should {string} be added")
    public void trainingShouldBeAdded(String state) {
        if (state.isEmpty()) {
            Training training = trainingService.findTrainingByDateAndUsers(
                    traineeService.selectTraineeByUsername(trainingInfo.getTrainingCreationRequest().getTraineeUsername()).getId(),
                    trainerService.selectTrainerByUsername(trainingInfo.getTrainingCreationRequest().getTrainerUsername()).getId(),
                    trainingInfo.getTrainingCreationRequest().getTrainingDate());
            Assert.assertNotNull(training);
            trainingInfo.setTraining(training);
        } else {
            await().atMost(1, SECONDS).untilAsserted(() ->
                    assertThrows(Exception.class, () -> trainingService.findTrainingByDateAndUsers(
                            traineeService.selectTraineeByUsername(trainingInfo.getTrainingCreationRequest().getTraineeUsername()).getId(),
                            trainerService.selectTrainerByUsername(trainingInfo.getTrainingCreationRequest().getTrainerUsername()).getId(),
                            trainingInfo.getTrainingCreationRequest().getTrainingDate()))
            );
        }
    }
}
