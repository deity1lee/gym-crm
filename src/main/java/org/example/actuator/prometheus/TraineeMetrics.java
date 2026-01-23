package org.example.actuator.prometheus;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.example.dao.TraineeDao;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {

    public TraineeMetrics(MeterRegistry registry, TraineeDao traineeDao) {
        Gauge.builder("gym_active_trainees", traineeDao, dao ->
                        dao.countActiveTrainees()
                )
                .description("Number of currently active trainees")
                .register(registry);
    }
}
