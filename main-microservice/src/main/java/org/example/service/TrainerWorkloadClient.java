package org.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.dto.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TrainerWorkloadClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainerWorkloadClient.class);

    private final RestTemplate restTemplate;

    public TrainerWorkloadClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "secondaryService", fallbackMethod = "fallback")
    public void sendUpdate(TrainerWorkloadRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "my-secret-key-123");

        String txId = MDC.get("transactionId");
        if (txId != null) {
            headers.set("X-Transaction-Id", txId);
        }
        HttpEntity<TrainerWorkloadRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForObject(
                "http://secondary-microservice/workload",
                entity,
                Void.class
        );
    }

    public void fallback(TrainerWorkloadRequest request, Throwable ex) {

        LOGGER.warn(
                "[{}] Secondary microservice unavailable, skipping update for trainer {}: {}",
                MDC.get("transactionId"),
                request.getTrainerUsername(),
                ex.getMessage()
        );

        throw new RuntimeException("Downstream unavailable, keeping in outbox.", ex);
    }
}
