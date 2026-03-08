package org.workload.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TrainerMonth {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "calendar_month")
    private int month;
    private Long trainingDuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}
