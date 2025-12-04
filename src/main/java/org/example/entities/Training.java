package org.example.entities;

public class Training {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private String date;
    private Long duration;

    public Training(Long id, Long traineeId, Long trainerId, String name, TrainingType type, String date, Long duration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
