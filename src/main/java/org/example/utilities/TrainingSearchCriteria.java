package org.example.utilities;

import java.sql.Date;

public class TrainingSearchCriteria {
    private Date fromDate;
    private Date toDate;
    private String userLastName;
    private String trainingType;

    public String getUserLastName() {
        return userLastName;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public String getTrainingType() {
        return trainingType;
    }
}

