package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.entities.TrainingType;

import java.time.LocalDate;

public class TrainingInfoResponse {
    private String trainingName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    private TrainingType trainingType;
    private Long trainingDuration;
    private String trainerFullName;

    public TrainingInfoResponse(String trainingName, LocalDate trainingDate,
                                TrainingType trainingType, Long trainingDuration,
                                String trainerFullName) {
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingType = trainingType;
        this.trainingDuration = trainingDuration;
        this.trainerFullName = trainerFullName;
    }

    public TrainingInfoResponse() {
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

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public Long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public String getTrainerFullName() {
        return trainerFullName;
    }

    public void setTrainerFullName(String trainerFullName) {
        this.trainerFullName = trainerFullName;
    }
}

