package org.example.dto;

public class TrainerTraineeCount {

    private final String username;
    private final long traineeCount;

    public TrainerTraineeCount(String username, long traineeCount) {
        this.username = username;
        this.traineeCount = traineeCount;
    }

    public String getUsername() {
        return username;
    }

    public long getTraineeCount() {
        return traineeCount;
    }
}
