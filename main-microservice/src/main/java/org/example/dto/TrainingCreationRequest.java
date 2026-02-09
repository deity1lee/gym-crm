package org.example.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Request body for training creation")
public class TrainingCreationRequest {

    @Schema(
            description = "Username of trainer",
            example = "John.Do",
            required = true
    )
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @Schema(
            description = "Username of trainee",
            example = "John.Doe3",
            required = true
    )
    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @Schema(
            description = "Training type ID",
            example = "1",
            required = true
    )
    @NotNull(message = "Training type ID is required")
    private Long trainingTypeId;

    @Schema(
            description = "Training name",
            example = "Morning Yoga Session",
            required = true
    )
    @NotBlank(message = "Training name is required")
    private String trainingName;

    @Schema(
            description = "Training date",
            example = "2025-01-15",
            required = true
    )
    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @Schema(
            description = "Training duration in minutes",
            example = "60",
            required = true
    )
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

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public String toString() {
        return "TrainingCreationRequest{" +
                "trainerUsername='" + trainerUsername + '\'' +
                ", traineeUsername='" + traineeUsername + '\'' +
                ", trainingTypeId=" + trainingTypeId +
                ", trainingName='" + trainingName + '\'' +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
