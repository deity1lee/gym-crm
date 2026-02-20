package org.example.integration.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.example.integration.dto.TrainingInfoDto;
import org.example.service.TrainerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WorkloadRetrievalSteps {

    private final TrainerService trainerService;
    private final TrainingInfoDto trainingInfo;

    public WorkloadRetrievalSteps(TrainerService trainerService, TrainingInfoDto trainingInfo) {
        this.trainerService = trainerService;
        this.trainingInfo = trainingInfo;
    }
    @Then("an exception should be thrown, stating that trainer {string} is not found")
    public void anExceptionShouldBeThrownStatingThatTrainerIsNotFound(String username) {
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.selectTrainerByUsername(username));
    }

    @And("the trainer's workload should be returned")
    public void theTrainerSWorkloadShouldBeReturned() {
        assertDoesNotThrow(trainingInfo::getInitialWorkload);
        assertThat(trainingInfo.getInitialWorkload()).isGreaterThanOrEqualTo(0L);
    }
}
