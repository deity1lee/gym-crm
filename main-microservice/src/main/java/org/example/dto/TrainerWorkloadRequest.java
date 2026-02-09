package org.example.dto;

import org.example.entities.Training;

import java.time.LocalDate;

public class TrainerWorkloadRequest {

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean isActive;

    private LocalDate trainingDate;
    private Long trainingDuration;

    private org.example.dto.ActionType actionType;

    public TrainerWorkloadRequest(
            String trainerUsername,
            String trainerFirstName,
            String trainerLastName,
            boolean isActive,
            LocalDate trainingDate,
            Long trainingDuration,
            org.example.dto.ActionType actionType
    ) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = actionType;
    }

    public TrainerWorkloadRequest(
            Training training,
            org.example.dto.ActionType actionType
    ) {
        this.trainerUsername = training.getTrainer().getUsername();
        this.trainerFirstName = training.getTrainer().getFirstName();
        this.trainerLastName = training.getTrainer().getLastName();
        this.isActive = training.getTrainer().isActive();
        this.trainingDate = training.getDate();
        this.trainingDuration = training.getDuration();
        this.actionType = actionType;
    }

    public TrainerWorkloadRequest(){}

    public enum ActionType {
        ADD, DELETE
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public org.example.dto.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(org.example.dto.ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "TrainerWorkloadRequest{" +
                "trainerUsername='" + trainerUsername + '\'' +
                ", trainerFirstName='" + trainerFirstName + '\'' +
                ", trainerLastName='" + trainerLastName + '\'' +
                ", isActive=" + isActive +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                ", actionType=" + actionType +
                '}';
    }
}
