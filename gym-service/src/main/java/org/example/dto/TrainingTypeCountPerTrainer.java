package org.example.dto;

public class TrainingTypeCountPerTrainer {

    private final String trainerUsername;
    private final long distinctTrainingTypes;

    public TrainingTypeCountPerTrainer(String trainerUsername, long distinctTrainingTypes) {
        this.trainerUsername = trainerUsername;
        this.distinctTrainingTypes = distinctTrainingTypes;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public long getDistinctTrainingTypes() {
        return distinctTrainingTypes;
    }
}
