package org.example.actuator.health;

import org.example.dao.TraineeDao;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnassignedTrainerHealthIndicator implements HealthIndicator {

    private final TraineeDao traineeDao;

    public UnassignedTrainerHealthIndicator(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Health health() {

        List<Trainee> trainees = traineeDao.findAll();

        for (Trainee trainee : trainees) {
            List<Trainer> availableTrainers =
                    traineeDao.findUnassignedTrainers(trainee.getUsername());

            if (availableTrainers.isEmpty()) {

                return Health.down()
                        .withDetail("trainee", trainee.getUsername())
                        .withDetail("problem", "No trainers available for this trainee")
                        .build();
            }
        }

        return Health.up().withDetail("message", "All trainees have available trainers").build();
    }
}
