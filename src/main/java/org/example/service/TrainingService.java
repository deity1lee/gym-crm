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

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao TrainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao TrainingDao) {
        this.TrainingDao = TrainingDao;
    }

    public void createTraining(Training Training) {
        TrainingDao.save(Training);
        logger.info("Created training with name: {}", Training.getName());
    }

    public Optional<Training> getTrainingById(String name) {
        logger.debug("Fetching training with name: {}", name);
        return TrainingDao.findByName(name);
    }

    public List<Training> getAllTrainings() {
        logger.debug("Fetching all trainings");
        return TrainingDao.findAll();
    }
}


