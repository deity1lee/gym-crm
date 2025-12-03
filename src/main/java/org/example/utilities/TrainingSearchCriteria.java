package org.example.utilities;

import java.sql.Date;

public class TrainingSearchCriteria {
    private Date fromDate;
    private Date toDate;
    private String trainerUsername;
    private String traineeUsername;
    private Long trainingTypeId;

    public TrainingSearchCriteria(Date fromDate, Date toDate, String trainerUsername, String traineeUsername, Long trainingTypeId) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.trainerUsername = trainerUsername;
        this.traineeUsername = traineeUsername;
        this.trainingTypeId = trainingTypeId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

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

    public void setTrainingType(Long trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }
}
