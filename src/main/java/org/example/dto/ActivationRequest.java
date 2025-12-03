package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "Request for activating or deactivating a trainee")
public class ActivationRequest {

    @ApiModelProperty(value = "Trainee username", required = true, example = "John.Doe3")
    @NotBlank(message = "Username is required")
    private String username;

    @ApiModelProperty(value = "Active status (true/false)", required = true, example = "true")
    @NotNull(message = "isActive is required")
    private Boolean isActive;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
