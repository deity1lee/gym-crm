package org.example.storage;


import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    private final Map<Long, Trainer> trainerMap = new HashMap<>();
    private final Map<Long, Training> trainingMap = new HashMap<>();
    private final Map<Long, Trainee> traineeMap = new HashMap<>();

    private final AtomicLong traineeMaxId = new AtomicLong(0);
    private final AtomicLong trainerMaxId = new AtomicLong(0);
    private final AtomicLong trainingMaxId = new AtomicLong(0);

    public Map<Long, Trainer> getTrainerMap() {
        return trainerMap;
    }
    public Map<Long, Training> getTrainingMap() {
        return trainingMap;
    }
    public Map<Long, Trainee> getTraineeMap() {
        return traineeMap;
    }

    public AtomicLong getTraineeMaxId() {
        return traineeMaxId;
    }

    public AtomicLong getTrainerMaxId() {
        return trainerMaxId;
    }

    public AtomicLong getTrainingMaxId() {
        return trainingMaxId;
    }

    public void setTraineeMaxId(long id) {
        traineeMaxId.set(id);
    }
    public void setTrainerMaxId(long id) {
        trainerMaxId.set(id);
    }
    public void setTrainingMaxId(long id) {
        trainingMaxId.set(id);
    }
}

