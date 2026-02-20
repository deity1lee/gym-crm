package org.example.integration.dto;

import org.example.dto.TrainingCreationRequest;
import org.example.entities.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingInfoDto {

    private Training training;
    private TrainingCreationRequest trainingCreationRequest;
    private Long initialWorkload;

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public TrainingCreationRequest getTrainingCreationRequest() {
        return trainingCreationRequest;
    }

    public void setTrainingCreationRequest(TrainingCreationRequest trainingCreationRequest) {
        this.trainingCreationRequest = trainingCreationRequest;
    }

    public Long getInitialWorkload() {
        return initialWorkload;
    }

    public void setInitialWorkload(Long initialWorkload) {
        this.initialWorkload = initialWorkload;
    }
}
