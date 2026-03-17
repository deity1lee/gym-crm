package org.example.actuator.health;

import org.example.dao.TrainerDao;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class UnassignedTrainerHealthIndicator implements HealthIndicator {

    private final TrainerDao trainerDao;

    public UnassignedTrainerHealthIndicator(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Health health() {

        long count = trainerDao.countActiveTrainersWithoutTrainees();

        if (count > 20) {
            return Health.down()
                    .withDetail("problem", "There more than 20 trainers without trainees")
                    .withDetail("unassignedTrainersCount", count)
                    .build();
        }
        return Health.up()
                .withDetail("message", "No more than 20 active trainers do not have trainee")
                .build();
    }
}
