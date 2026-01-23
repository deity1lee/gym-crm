package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.*;
import java.util.Date;

@ApiModel(description = "Request body for training creation")
public class TrainingCreationRequest {

    @ApiModelProperty(
            value = "Username of trainer",
            required = true,
            example = "John.Do")
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @ApiModelProperty(
            value = "Username of trainee",
            required = true,
            example = "John.Doe3")
    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @ApiModelProperty(
            value = "Training type ID",
            required = true,
            example = "1")
    @NotNull(message = "Training type ID is required")
    private Long trainingTypeId;

    @ApiModelProperty(
            value = "Training name",
            required = true,
            example = "Morning Yoga Session")
    @NotBlank(message = "Training name is required")
    private String trainingName;

    @ApiModelProperty(
            value = "Training date",
            required = true,
            example = "2025-01-15")
    @NotNull(message = "Training date is required")
    private Date trainingDate;

    @ApiModelProperty(
            value = "Training duration in minutes",
            required = true,
            example = "60")
    @NotNull(message = "Training duration is required")
    @Min(value = 1, message = "Training duration must be greater than 0")
    private Long trainingDuration;

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public Long getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(Long trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}


