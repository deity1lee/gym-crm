package org.example.policy;

import org.example.dao.OutboxDao;
import org.example.service.TrainerWorkloadClient;
import org.example.dao.TraineeDao;
import org.example.dto.ActionType;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("stg")
@Service
public class StgTraineeDeletionPolicy implements TraineeDeletionPolicy{
    private static final Logger LOGGER = LoggerFactory.getLogger(StgTraineeDeletionPolicy.class);

    private final TraineeDao traineeDao;
    private final OutboxDao outboxDao;

    StgTraineeDeletionPolicy(TraineeDao traineeDao, OutboxDao outboxDao) {
        this.traineeDao = traineeDao;
        this.outboxDao = outboxDao;
    }

    @Override
    public void deleteTrainee(String username) {
        LOGGER.info("[{}] STG: Deleting trainer {}", MDC.get("transactionId"), username);
        List<Training> trainings = traineeDao.deleteTrainee(username);
        trainings.stream().map(training ->  new TrainerWorkloadRequest(training, ActionType.DELETE)).forEach(outboxDao::createPendingRequest);
    }
}
