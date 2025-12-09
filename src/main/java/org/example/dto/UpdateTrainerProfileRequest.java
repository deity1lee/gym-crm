package org.example.dto;

import org.example.entities.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Update trainer profile request")
public class UpdateTrainerProfileRequest {

    @Schema(
            description = "Username of the trainer",
            example = "John.Do",
            required = true
    )
    @NotBlank
    private String username;

    @Schema(
            description = "First name of the trainer",
            example = "Serj",
            required = true
    )
    @NotBlank
    private String firstName;

    @Schema(
            description = "Last name of the trainer",
            example = "Tankyan",
            required = true
    )
    @NotBlank
    private String lastName;

    @Schema(
            description = "Training specialization (ID and Name)"
    )
    private TrainingType trainingType;

    @Schema(
            description = "Whether the trainer is active",
            example = "true",
            required = true
    )
    @NotNull
    private Boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
