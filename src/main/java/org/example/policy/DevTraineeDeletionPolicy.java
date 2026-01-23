package org.example.policy;

import org.example.dao.TraineeDao;
import org.example.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class DevTraineeDeletionPolicy implements TraineeDeletionPolicy{

    private static final Logger LOGGER = LoggerFactory.getLogger(DevTraineeDeletionPolicy.class);

    private final TraineeDao traineeDao;

    DevTraineeDeletionPolicy(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void deleteTrainee(String username) {
        traineeDao.deleteTrainee(username);
        LOGGER.info("DEV: Trainer {}, deleted", username);
    }
}