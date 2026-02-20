package org.example.integration.steps;//package org.example.integration.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.entities.Training;
import org.example.integration.dto.ResponseDto;
import org.example.integration.dto.TrainingInfoDto;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.utilities.Credentials;
import org.example.dto.TrainingCreationRequest;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainingCreationSteps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TestRestTemplate restTemplate;
    private final Credentials credentials;
    private final TrainingService trainingService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingInfoDto trainingInfo;
    private final ResponseDto responseDto;

    @Value("${gym.service.url}")
    private String gymServiceUrl;

    private ResponseEntity<Long> getWorkloadResponse;

    public TrainingCreationSteps(TestRestTemplate restTemplate, Credentials credentials, TrainingService trainingService, TrainerService trainerService, TraineeService traineeService, TrainingInfoDto trainingInfo, ResponseDto responseDto) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
        this.trainingService = trainingService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingInfo = trainingInfo;
        this.responseDto = responseDto;
    }

    @When("the user creates a training for trainee {string} and trainer {string} on {string}")
    public void createTraining(String traineeUsername, String trainerUsername, String date) {
        TrainingCreationRequest trainingCreationRequest = new TrainingCreationRequest();
        trainingCreationRequest.setTraineeUsername(traineeUsername);
        trainingCreationRequest.setTrainerUsername(trainerUsername);
        trainingCreationRequest.setTrainingName("Strength");
        trainingCreationRequest.setTrainingTypeId(1L);
        trainingCreationRequest.setTrainingDate(LocalDate.parse(date, FORMATTER));
        trainingCreationRequest.setTrainingDuration(6L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(credentials.getJwt().substring(7));
        HttpEntity<TrainingCreationRequest> entity = new HttpEntity<>(trainingCreationRequest, headers);

        ResponseEntity<?> response = restTemplate.postForEntity(
                String.format("%s/trainings", gymServiceUrl),
                entity,
                Void.class
        );
        trainingInfo.setTrainingCreationRequest(trainingCreationRequest);
        responseDto.setResult(response);
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
            await().atMost(10, SECONDS).untilAsserted(() ->
                    assertThrows(Exception.class, () -> trainingService.findTrainingByDateAndUsers(
                            traineeService.selectTraineeByUsername(trainingInfo.getTrainingCreationRequest().getTraineeUsername()).getId(),
                            trainerService.selectTrainerByUsername(trainingInfo.getTrainingCreationRequest().getTrainerUsername()).getId(),
                            trainingInfo.getTrainingCreationRequest().getTrainingDate()))
            );
        }
    }

    @And("the {string} trainer's workload on {int} month of {int} year should {string} be increased by {long} hours")
    public void verifyTrainerWorkload(String trainerUsername, int month, int year, String state, long duration) {
        String jwtToken = credentials.getJwt().substring(7);
        await().atMost(15, SECONDS).untilAsserted(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            getWorkloadResponse = restTemplate.exchange(String.format("%s/trainers/%s/trainingHours?year=%d&month=%d",
                            gymServiceUrl, trainerUsername, year, month),
                    HttpMethod.GET,
                    entity,
                    Long.class
            );
        });
        if (state.isEmpty()) {
            assertEquals(trainingInfo.getInitialWorkload()+duration, getWorkloadResponse.getBody());
        } else {
            assertEquals(trainingInfo.getInitialWorkload(), getWorkloadResponse.getBody());
        }
    }

    @And("the {string} trainer's workload on {int} month of {int} year is retrieved")
    public void theTraineeSWorkloadOnOfIsRetrieved(String trainerUsername, int month, int year) {
        String jwtToken = credentials.getJwt().substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                String.format("%s/trainers/%s/trainingHours?year=%d&month=%d",
                        gymServiceUrl, trainerUsername, year, month),
                HttpMethod.GET,
                entity,
                String.class
        );
        responseDto.setResult(response);

        if (response.getStatusCode().is2xxSuccessful()) {
            trainingInfo.setInitialWorkload(Long.parseLong(response.getBody()));
        }
    }
}
