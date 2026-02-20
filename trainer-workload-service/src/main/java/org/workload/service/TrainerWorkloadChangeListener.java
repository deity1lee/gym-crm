package org.workload.service;

import org.workload.dto.TrainerWorkloadRequest;
import org.workload.entities.TrainerYear;
import org.workload.repository.TrainerWorkloadRepository;
import org.workload.entities.TrainerMonth;
import org.workload.entities.TrainerWorkload;
import org.workload.exception.WorkloadUpdateRequestAuthException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class TrainerWorkloadChangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadChangeListener.class);

    private final TrainerWorkloadRepository repository;
    private final ValidatorService validatorService;
    private final JmsTemplate jmsTemplate;

    @Value("${security.api-key}")
    private String apiKey;

    public TrainerWorkloadChangeListener(TrainerWorkloadRepository repository, ValidatorService validatorService, JmsTemplate jmsTemplate) {
        this.repository = repository;
        this.validatorService = validatorService;
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "trainer.workload.change.queue")
    public void process(@Payload TrainerWorkloadRequest request, @Header(name = "X-Transaction-Id") String txId,
                        @Header(name = "X-API-KEY") String requestKey) {

        MDC.put("transactionId", Optional.ofNullable(txId).orElse(UUID.randomUUID().toString()));

        if (requestKey == null || !requestKey.equals(apiKey)) {
            LOGGER.info("[{}] Request is not authenticated: {}", txId, request.getTrainerUsername());
            throw new WorkloadUpdateRequestAuthException("Invalid API key");
        }
        if (!validatorService.validateAddRequest(request)) {
            jmsTemplate.convertAndSend(
                    "invalid.trainer.workload",
                    request,
                    message -> {
                        message.setStringProperty("X-Transaction-Id", txId);
                        message.setStringProperty("X-API-KEY", requestKey);
                        return message;
                    }
            );
            LOGGER.info("[{}] Request was moved to specified queue", txId);
            return;
        }

        Optional<TrainerWorkload> trainerWorkloadOptional = repository.findByTrainerUsername(request.getTrainerUsername());

        switch (request.getActionType()) {
            case DELETE -> {
                if (trainerWorkloadOptional.isEmpty()) {
                    LOGGER.info("[{}] DELETE ignored: trainer {} not found", txId, request.getTrainerUsername());
                    return;
                }
                updateDuration(trainerWorkloadOptional.get(), request, -request.getTrainingDuration());
            }
            case ADD -> {
                TrainerWorkload workload = trainerWorkloadOptional.orElseGet(() -> createTrainerWorkload(request));
                updateDuration(workload, request, request.getTrainingDuration());
            }
        }
        LOGGER.info("[{}] Trainer={} workload was updated", txId, request.getTrainerUsername());
    }

    private void updateDuration(TrainerWorkload workload, TrainerWorkloadRequest request, Long delta) {
        String txId = MDC.get("transactionId");

        int yearValue = request.getTrainingDate().getYear();
        int monthValue = request.getTrainingDate().getMonthValue();

        TrainerYear year = findOrCreateYear(workload, yearValue);
        TrainerMonth month = findOrCreateMonth(year, monthValue);

        long newTotal = month.getTrainingDuration() + delta;

        if (newTotal <= 0) {
            handleRemoval(workload, year, month);
        } else {
            month.setTrainingDuration(newTotal);
            repository.save(workload);
        }
        LOGGER.info("[{}] Trainer {} workload was successfully changed", txId, request.getTrainerUsername());
    }

    private void handleRemoval(TrainerWorkload workload, TrainerYear year, TrainerMonth month) {
        year.getMonths().remove(month);
        if (year.getMonths().isEmpty()) {
            workload.getYears().remove(year);
        }
        if (workload.getYears().isEmpty()) {
            repository.delete(workload);
        } else {
            repository.save(workload);
        }
    }

    private TrainerYear findOrCreateYear(TrainerWorkload workload, int year) {
        return workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    TrainerYear newYear = new TrainerYear();
                    newYear.setYear(year);
                    workload.getYears().add(newYear);
                    return newYear;
                });
    }

    private TrainerMonth findOrCreateMonth(TrainerYear year, int month) {
        return year.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    TrainerMonth newMonth = new TrainerMonth();
                    newMonth.setMonth(month);
                    newMonth.setTrainingDuration(0L);
                    year.getMonths().add(newMonth);
                    return newMonth;
                });
    }

    public TrainerWorkload createTrainerWorkload(TrainerWorkloadRequest request) {
        TrainerWorkload t = new TrainerWorkload();
        t.setTrainerUsername(request.getTrainerUsername());
        t.setFirstName(request.getTrainerFirstName());
        t.setLastName(request.getTrainerLastName());
        t.setActive(request.isActive());
        return t;
    }
}
