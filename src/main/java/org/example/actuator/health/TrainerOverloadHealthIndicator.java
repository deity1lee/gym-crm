package org.example.actuator.health;

import org.example.dao.TrainerDao;
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

        List<Object[]> results = trainerDao.findTrainerTraineeCounts();

        for (Object[] row : results) {
            String username = (String) row[0];
            int traineeCount = ((Integer) row[1]);

            if (traineeCount > MAX_TRAINEES) {
                return Health.down()
                        .withDetail("trainer", username)
                        .withDetail("traineeCount", traineeCount)
                        .withDetail("limit", MAX_TRAINEES)
                        .build();
            }
        }
        return Health.up().build();
    }
}
