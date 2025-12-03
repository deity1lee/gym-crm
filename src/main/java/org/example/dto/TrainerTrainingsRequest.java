package org.example.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(description = "Request parameters for retrieving trainer trainings")
public class TrainerTrainingsRequest {

    @ApiModelProperty(value = "Trainer username", required = true, example = "Trainer.User1")
    private String trainerUsername;

    @ApiModelProperty(value = "Period start date (optional)", example = "2024-01-01")
    private Date periodFrom;

    @ApiModelProperty(value = "Period end date (optional)", example = "2024-01-31")
    private Date periodTo;

    @ApiModelProperty(value = "Trainee username filter (optional)", example = "John.Doe3")
    private String traineeUsername;

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }
}
