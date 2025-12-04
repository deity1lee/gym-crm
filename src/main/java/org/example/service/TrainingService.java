package org.example.service;

import org.example.dao.TrainingDao;
import org.example.entities.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class TrainingService {

    private static final Logger LOGGER  = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public void createTraining(Training training) {
        if (trainingDao.findById(training.getId()).isPresent()) {
            LOGGER.warn("Training with id {} already exists. Creation skipped.", training.getId());
            return;
        }
        trainingDao.save(training);
        LOGGER.info("Created training with name: {}", training.getName());
    }

    public Optional<Training> getTrainingById(Long id) {
        LOGGER.debug("Fetching training with id: {}", id);
        return trainingDao.findById(id);
    }

    public List<Training> getAllTrainings() {
        LOGGER.debug("Fetching all trainings");
        return trainingDao.findAll();
    }
}


