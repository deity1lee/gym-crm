package org.example.utilities;

import javax.xml.crypto.Data;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.time.LocalDate;

public class TrainingSearchCriteria {
    private Date fromDate;
    private Date toDate;

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    private String userLastName;
    private String trainingType;

    public Object getFromDate() {
        return fromDate;
    }

    public Object getToDate() {
        return toDate;
    }

    public CharSequence getTrainingType() {
        return trainingType;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }
}

