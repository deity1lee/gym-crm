package org.example.policy;

import org.example.dao.TraineeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local")
@Service
public class LocalTraineeDeletionPolicy implements TraineeDeletionPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTraineeDeletionPolicy.class);

    private final TraineeDao traineeDao;

    LocalTraineeDeletionPolicy(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void deleteTrainee(String username) {
        traineeDao.deleteTrainee(username);
        LOGGER.info("LOCAL: Trainer {}, deleted", username);
    }
}
