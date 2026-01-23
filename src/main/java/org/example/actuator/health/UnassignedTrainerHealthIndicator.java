package org.example.actuator.health;

import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnassignedTrainerHealthIndicator implements HealthIndicator {

    private final TrainerDao trainerDao;

    public UnassignedTrainerHealthIndicator(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Health health() {

        long count = trainerDao.countActiveTrainersWithoutTrainees();

        if (count > 0) {
            return Health.down()
                    .withDetail("problem", "There are trainers without trainees")
                    .withDetail("unassignedTrainersCount", count)
                    .build();
        }
        return Health.up()
                .withDetail("message", "All active trainers have at least one trainee")
                .build();
    }
}
