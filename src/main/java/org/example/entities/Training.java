package org.example.entities;

public class Training {
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType type;
    private String date;
    private Long duration;

    public Training(Long traineeId, Long trainerId, String name, TrainingType type, String date, Long duration) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.name = name;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Training{" +
                "traineeId=" + traineeId +
                ", trainerId=" + trainerId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", date='" + date + '\'' +
                ", duration=" + duration +
                '}';
    }
}
