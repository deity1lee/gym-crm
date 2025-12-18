package org.example.actuator.health;

import org.example.dao.TrainerDao;
import org.example.dto.TrainerTraineeCount;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

//A trainer cannot have more than 5 trainees assigned.

@Component
public class TrainerOverloadHealthIndicator implements HealthIndicator {

    private final TrainerDao trainerDao;
    private static final int MAX_TRAINEES = 5;

    public TrainerOverloadHealthIndicator(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Health health() {

        List<TrainerTraineeCount> results = trainerDao.findTrainerTraineeCounts();

        for (TrainerTraineeCount row : results) {

            if (row.getTraineeCount() > MAX_TRAINEES) {
                return Health.down()
                        .withDetail("trainer", row.getUsername())
                        .withDetail("traineeCount", row.getTraineeCount())
                        .withDetail("limit", MAX_TRAINEES)
                        .build();
            }
        }
        return Health.up().build();
    }
}
