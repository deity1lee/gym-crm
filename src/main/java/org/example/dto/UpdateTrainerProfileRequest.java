package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.example.entities.TrainingType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("Update trainer profile request")
public class UpdateTrainerProfileRequest {

    @ApiModelProperty(required = true, example = "John.Do")
    @NotBlank
    private String username;

    @ApiModelProperty(required = true, example = "Serj")
    @NotBlank
    private String firstName;

    @ApiModelProperty(required = true, example = "Tankyan")
    @NotBlank
    private String lastName;

    @ApiModelProperty
    private TrainingType trainingType;

    @ApiModelProperty(required = true, example = "true")
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
