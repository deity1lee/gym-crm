package org.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.dto.TrainerWorkloadRequest;
import org.example.exception.DownstreamServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TrainerWorkloadClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadClient.class);

    private final RestTemplate restTemplate;

    @Value("${security.api-key}")
    private String apiKey;

    public TrainerWorkloadClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "trainerWorkloadService", fallbackMethod = "changeWorkingHoursFailure")
    public void changeWorkingHours(TrainerWorkloadRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        String txId = MDC.get("transactionId");
        if (txId != null) {
            headers.set("X-Transaction-Id", txId);
        }

        HttpEntity<TrainerWorkloadRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForObject(
                "http://trainer-workload-service/workload",
                entity,
                Void.class
        );
    }

    @CircuitBreaker(name = "trainerWorkloadService", fallbackMethod = "getTrainingHoursFailure")
    public Long getTrainerTrainingHours(String username, int month) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        String txId = MDC.get("transactionId");
        if (txId != null) {
            headers.set("X-Transaction-Id", txId);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = String.format("http://trainer-workload-service/workload/%s/trainingHours/%s",
                username,
                month
        );
        LOGGER.info("[{}] Notifying workload service", txId);
        return restTemplate.exchange(url,
                org.springframework.http.HttpMethod.GET,
                entity,
                Long.class
        ).getBody();
    }

    private Long getTrainingHoursFailure(String username, int month, Throwable ex) {
        LOGGER.warn("[{}] Trainer workload service GET failed for trainer {} month {}",
                MDC.get("transactionId"),
                username,
                month
        );
        throw new DownstreamServiceException("Trainer workload service unavailable", ex);
    }

    private void changeWorkingHoursFailure(TrainerWorkloadRequest request, Throwable ex) {
        LOGGER.warn("[{}] Trainer workload service call failed for trainer {}",
                MDC.get("transactionId"),
                request.getTrainerUsername()
        );
        throw new DownstreamServiceException("Trainer workload service unavailable", ex);
    }
}
