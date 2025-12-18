package org.example.actuator.health;

import org.example.dao.TrainingDao;
import org.example.dto.TrainingTypeCountPerTrainer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TrainerTrainingTypeOveruseHealthIndicator implements HealthIndicator {

    private static final int MAX_TYPES = 2;

    private final TrainingDao trainingDao;

    public TrainerTrainingTypeOveruseHealthIndicator(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Health health() {

        List<TrainingTypeCountPerTrainer> rows =
                trainingDao.findTrainingTypesPerTrainer();

        for (TrainingTypeCountPerTrainer row : rows) {

            if (row.getDistinctTrainingTypes() > MAX_TYPES) {
                return Health.down()
                        .withDetail("trainer", row.getTrainerUsername())
                        .withDetail("distinctTrainingTypes", row.getDistinctTrainingTypes())
                        .withDetail("limit", MAX_TYPES)
                        .withDetail("reason", "Trainer is teaching too many training types")
                        .build();
            }
        }
        return Health.up()
                .withDetail("message", "All trainers teach within allowed training types")
                .build();
    }
}
