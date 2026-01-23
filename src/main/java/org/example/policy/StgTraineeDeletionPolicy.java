package org.example.policy;

import org.example.dao.TraineeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("stg")
@Service
public class StgTraineeDeletionPolicy implements TraineeDeletionPolicy{
    private static final Logger LOGGER = LoggerFactory.getLogger(StgTraineeDeletionPolicy.class);

    private final TraineeDao traineeDao;

    StgTraineeDeletionPolicy(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void deleteTrainee(String username) {
        traineeDao.deleteTrainee(username);
        LOGGER.info("STG: Trainer {}, deleted", username);
    }
}
