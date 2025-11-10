package org.example.storage;


import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryStorage {

    private final Map<Long, Trainer> trainerMap = new HashMap<>();
    private final Map<Long, Training> trainingMap = new HashMap<>();
    private final Map<Long, Trainee> traineeMap = new HashMap<>();

    public Map<Long, Trainer> getTrainerMap() {
        return trainerMap;
    }
    public Map<Long, Training> getTrainingMap() {
        return trainingMap;
    }
    public Map<Long, Trainee> getTraineeMap() {
        return traineeMap;
    }
}

