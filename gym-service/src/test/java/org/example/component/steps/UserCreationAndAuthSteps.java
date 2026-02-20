package org.example.component.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.example.utilities.Credentials;
import org.example.dto.UserCredentials;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class UserCreationAndAuthSteps {

    private final Credentials credentials;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final MockMvc mockMvc;

    public UserCreationAndAuthSteps(Credentials credentials, TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, MockMvc mockMvc) {
        this.credentials = credentials;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.mockMvc = mockMvc;
    }

    @Given("a trainee exists with first name {string}, last name {string} and address {string}")
    public void aTraineeExists(String firstName, String lastName, String address) {
        Trainee trainee = traineeService.createTrainee(new Trainee(firstName, lastName, LocalDate.now(), address, true));
        credentials.setUserCredentials(new UserCredentials(trainee.getUsername(), trainee.getRawPassword()));
    }

    @Given("a trainer exists with first name {string}, last name {string}")
    public void aTrainerExists(String firstName, String lastName) {
        Trainer trainer = trainerService.createTrainer(new Trainer(firstName, lastName,  trainingService.findTrainingTypeById(1L)));
        credentials.setUserCredentials(new UserCredentials(trainer.getUsername(), trainer.getRawPassword()));
    }

    @And("user is logged in")
    public void userIsLoggedIn() throws Exception {
         String jwt = mockMvc.perform(
                 get("/auth/login")
                         .header("X-Auth-Username", credentials.getUserCredentials().getUsername())
                         .header("X-Auth-Password", credentials.getUserCredentials().getPassword())
         ).andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
         credentials.setJwt(jwt);
    }
}
