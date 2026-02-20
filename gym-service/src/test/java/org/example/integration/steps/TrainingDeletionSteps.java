package org.example.integration.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.TrainingCreationRequest;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.integration.dto.ResponseDto;
import org.example.integration.dto.TrainingInfoDto;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.utilities.Credentials;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainingDeletionSteps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TestRestTemplate restTemplate;
    private final Credentials credentials;
    private final TrainingService trainingService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingInfoDto trainingInfo;
    private final ResponseDto responseDto;
    private ResponseEntity<Long> getWorkloadResponse;

    @Value("${gym.service.url}")
    private String gymServiceUrl;

    public TrainingDeletionSteps(TestRestTemplate restTemplate, Credentials credentials, TrainingService trainingService, TrainerService trainerService, TraineeService traineeService, TrainingInfoDto trainingInfo, ResponseDto responseDto) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
        this.trainingService = trainingService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingInfo = trainingInfo;
        this.responseDto = responseDto;
    }

    @When("the user deletes the training")
    public void theUserDeletesTheTraining() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(credentials.getJwt().substring(7));
        HttpEntity<TrainingCreationRequest> entity = new HttpEntity<>(headers);
        Training training = trainingService.findTrainingByDateAndUsers(
                traineeService.selectTraineeByUsername(trainingInfo.getTrainingCreationRequest().getTraineeUsername()).getId(),
                trainerService.selectTrainerByUsername(trainingInfo.getTrainingCreationRequest().getTrainerUsername()).getId(),
                trainingInfo.getTrainingCreationRequest().getTrainingDate());
        Assert.assertNotNull(training);
        trainingInfo.setTraining(training);
        ResponseEntity<?> response = restTemplate.exchange(
                String.format("%s/trainings/{id}",
                        gymServiceUrl),
                HttpMethod.DELETE,
                entity,
                Void.class,
                trainingInfo.getTraining().getId()
        );
        responseDto.setResult(response);

    }

    @Then("training should {string} be deleted")
    public void trainingShouldBeDeleted(String state) {
        if (!state.isEmpty()) {
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
    @And("the {string} trainer's workload on {int} month of {int} year should {string} be decreased by {long} hours")
    public void verifyTrainerWorkload(String trainerUsername, int month, int year, String state, long duration) {
        String jwtToken = credentials.getJwt().substring(7);
        await().atMost(10, SECONDS).untilAsserted(() -> {
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
            assertEquals(Math.max(0L, trainingInfo.getInitialWorkload() - duration), getWorkloadResponse.getBody());
        } else {
            assertEquals(trainingInfo.getInitialWorkload(), getWorkloadResponse.getBody());
        }
    }

    @When("the user deletes a non existing training")
    public void theUserDeletesANonExistingTraining() {
    }

    @Given("the user creates a training for trainee {string} and trainer {string} on {string}, but workload is not updated")
    public void theUserCreatesATrainingForTraineeAndTrainerOnButWorkloadIsNotUpdated(String traineeUsername, String trainerUsername, String date) {
        Trainee trainee = traineeService.selectTraineeByUsername(traineeUsername);
        Trainer trainer = trainerService.selectTrainerByUsername(trainerUsername);
        Training training = trainingService.addTraining( trainee.getUsername(),
                trainer.getUsername(),
                "training-name",
                trainingService.findTrainingTypeById(1L),
                LocalDate.parse(date, FORMATTER),
                6L
        );
        trainingInfo.setTraining(training);
    }

    @And("the trainer's workload should be {int}")
    public void theTrainerSWorkloadShouldBe(int duration) {
        String jwtToken = credentials.getJwt().substring(7);
        await().atMost(15, SECONDS).untilAsserted(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            getWorkloadResponse = restTemplate.exchange(String.format("%s/trainers/%s/trainingHours?year=%s&month=%s",
                            gymServiceUrl,
                            trainingInfo.getTraining().getTrainer().getUsername(),
                            trainingInfo.getTraining().getDate().getYear(),
                            trainingInfo.getTraining().getDate().getMonthValue()),
                    HttpMethod.GET,
                    entity,
                    Long.class
            );
        });
        assertEquals(duration, 0L);
    }
}
