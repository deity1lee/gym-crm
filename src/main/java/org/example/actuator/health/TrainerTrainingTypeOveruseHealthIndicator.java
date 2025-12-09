package org.example.actuator.health;

import org.example.dao.TrainingDao;
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

        List<Object[]> rows = trainingDao.findTrainingTypesPerTrainer();

        for (Object[] row : rows) {
            String trainer = (String) row[0];
            long distinctTypes = (long) row[1];

            if (distinctTypes > MAX_TYPES) {
                return Health.down()
                        .withDetail("trainer", trainer)
                        .withDetail("distinctTrainingTypes", distinctTypes)
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
