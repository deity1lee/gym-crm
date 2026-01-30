package org.example.dto;

import org.example.entities.TrainingType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for trainer registration")
public class TrainerRegistrationRequest {

    @Schema(
            description = "First name of trainer",
            example = "John",
            required = true
    )
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(
            description = "Last name of trainer",
            example = "Doe",
            required = true
    )
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(
            description = "Details of the training specialization (ID and Name).",
            example = "{\"id\": 1, \"trainingTypeName\": \"yoga\"}",
            required = true
    )
    @NotNull(message = "Training type is required")
    private TrainingType trainingType;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public TrainerRegistrationRequest(String firstName, String lastName, TrainingType trainingType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.trainingType = trainingType;
    }

    public TrainerRegistrationRequest() {
    }
}
