package org.example.dto;

import org.example.entities.Training;

import java.time.LocalDate;

public class TrainingSnapshot {
    public Long trainerId;
    public Long trainingTypeId;
    public String name;
    public LocalDate date;
    public Long duration;

    public static TrainingSnapshot from(Training t) {
        TrainingSnapshot s = new TrainingSnapshot();

        s.trainerId = t.getTrainer().getId();
        s.trainingTypeId = t.getTrainingType().getId();
        s.name = t.getName();
        s.date = t.getDate();
        s.duration = t.getDuration();

        return s;
    }
}
