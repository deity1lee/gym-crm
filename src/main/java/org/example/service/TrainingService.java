package org.example.service;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.dao.TrainingDao;
import org.example.dao.TrainingTypeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class TrainingService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingDao trainingDao;

    public TrainingService(TraineeDao traineeDao,
                           TrainerDao trainerDao,
                           TrainingTypeDao trainingTypeDao,
                           TrainingDao trainingDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.trainingDao = trainingDao;
    }

    public void addTraining(String traineeUsername,
                            String trainerUsername,
                            String trainingTypeName,
                            Date trainingDate,
                            Long duration,
                            String trainingName) {

        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainee not found: %s", traineeUsername)));
        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Trainer not found: %s", trainerUsername)));
        TrainingType type = trainingTypeDao.findByName(trainingTypeName)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Training type not found: %s", trainingTypeName)));

        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        Training training = new Training(
                null,
                trainee,
                trainer,
                trainingName,
                type,
                trainingDate,
                duration
        );
        trainingDao.addTraining(training);
    }
}
