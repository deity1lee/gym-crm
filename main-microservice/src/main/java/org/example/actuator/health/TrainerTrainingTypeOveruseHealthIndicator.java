package org.example.actuator.health;

import org.example.dao.TrainingDao;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainerTrainingTypeOveruseHealthIndicator implements HealthIndicator {

    private static final int MAX_TYPES = 2;

    private final TrainingDao trainingDao;

    public TrainerTrainingTypeOveruseHealthIndicator(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Health health() {

        return trainingDao.findTrainingTypesPerTrainer()
                .stream()
                .filter(row -> row.getDistinctTrainingTypes() > MAX_TYPES)
                .findFirst()
                .map(row -> Health.down()
                        .withDetail("trainer", row.getTrainerUsername())
                        .withDetail("distinctTrainingTypes", row.getDistinctTrainingTypes())
                        .withDetail("limit", MAX_TYPES)
                        .withDetail("reason", "Trainer is teaching too many training types")
                        .build()
                )
                .orElseGet(() -> Health.up()
                        .withDetail("message", "All trainers teach within allowed training types")
                        .build()
                );
    }
}
