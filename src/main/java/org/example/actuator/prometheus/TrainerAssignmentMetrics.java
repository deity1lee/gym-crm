package org.example.actuator.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TrainerAssignmentMetrics {

    private final Timer assignmentTimer;

    public TrainerAssignmentMetrics(MeterRegistry registry) {
        this.assignmentTimer = Timer.builder("gym_trainer_assignment_seconds")
                .description("Time taken to assign trainers to a trainee")
                .register(registry);
    }

    public <T> T record(Supplier<T> supplier) {
        return assignmentTimer.record(supplier);
    }
}
