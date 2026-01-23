package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.example.entities.TrainingType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Request body for trainer registration")
public class TrainerRegistrationRequest {

    @ApiModelProperty(value = "First name of trainer", required = true, example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @ApiModelProperty(value = "Last name of trainer", required = true, example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @ApiModelProperty(value = "Details of the training specialization (ID and Name).", required = true, example = "{\"id\": 1, \"trainingTypeName\": \"yoga\"}")
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

