package org.example.actuator.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class LoginMetrics {

    private final Counter loginCounter;

    public LoginMetrics(MeterRegistry registry) {
        this.loginCounter = Counter.builder("gym_successful_logins_total")
                .description("Total number of successful logins")
                .register(registry);
    }

    public void incrementLogin() {
        loginCounter.increment();
    }
}
