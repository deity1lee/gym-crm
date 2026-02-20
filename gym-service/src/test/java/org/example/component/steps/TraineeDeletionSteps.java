package org.example.component.steps;

import io.cucumber.java.en.*;
import org.example.component.dto.ResponseDto;
import org.example.utilities.Credentials;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainerWorkloadClient;
import org.example.service.TrainingService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.junit.jupiter.api.Assertions.*;

public class TraineeDeletionSteps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MockMvc mockMvc;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainerWorkloadClient trainerWorkloadClient;
    private final List<Training> traineeTrainings = new ArrayList<>();
    private final Credentials credentials;
    private final ResponseDto responseDto;

    public TraineeDeletionSteps(MockMvc mockMvc, TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, TrainerWorkloadClient trainerWorkloadClient, Credentials credentials, ResponseDto responseDto) {
        this.mockMvc = mockMvc;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainerWorkloadClient = trainerWorkloadClient;
        this.credentials = credentials;
        this.responseDto = responseDto;
    }

    @Given("a trainee {string} {string} with trainings on {string} and {string}")
    public void givenTraineeWithTrainings(String firstName, String lastName, String date1, String date2) {
        Trainee trainee = traineeService.createTrainee(new Trainee(firstName, lastName, LocalDate.now(), "Brooklyn 1", true));
        Trainer trainer = trainerService.createTrainer(new Trainer(firstName, lastName,  trainingService.findTrainingTypeById(1L)));
        Training training1 = trainingService.addTraining( trainee.getUsername(),
                trainer.getUsername(),
                "training-name",
                trainingService.findTrainingTypeById(1L),
                LocalDate.parse(date1, FORMATTER),
               8L
        );
        Training training2 = trainingService.addTraining( trainee.getUsername(),
                trainer.getUsername(),
                "training-name-1",
                trainingService.findTrainingTypeById(1L),
                LocalDate.parse(date2, FORMATTER),
                8L
        );
        traineeTrainings.add(training1);
        traineeTrainings.add(training2);
    }

    @When("I delete the trainee {string}")
    public void whenDeleteTrainee(String username) throws Exception {
        MvcResult result = mockMvc.perform(delete("/trainees/{username}", username)
                        .header(HttpHeaders.AUTHORIZATION, credentials.getJwt()))
                .andReturn();
        responseDto.setResult(result);
    }

    @Then("the trainee {string} with its trainings should no longer exist")
    public void theTraineeWithTrainingsOnAndShouldNoLongerExist(String username) {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.selectTraineeByUsername(username));
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> trainingService.findTrainingById(traineeTrainings.get(0).getId()));
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> trainingService.findTrainingById(traineeTrainings.get(1).getId()));
    }

    @And("the workload service should be notified for {int} trainings")
    public void thenWorkloadServiceNotified(int count) {

        verify(trainerWorkloadClient, times(count))
                .changeTrainerWorkload(any());
    }

    @Then("an exception should be thrown, stating that trainee {string} is not found")
    public void anExceptionShouldBeThrownStatingThatTraineeIsNotFound(String username) {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.selectTraineeByUsername(username));
    }
}
