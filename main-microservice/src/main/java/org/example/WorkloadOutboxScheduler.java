package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.dao.OutboxDao;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.WorkloadOutbox;
import org.example.service.TrainerWorkloadClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkloadOutboxScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadOutboxScheduler.class);

    private final OutboxDao outboxDao;
    private final TrainerWorkloadClient workloadClient;
    private final ObjectMapper objectMapper;

    public WorkloadOutboxScheduler(OutboxDao outboxDao, TrainerWorkloadClient workloadClient, ObjectMapper objectMapper) {
        this.outboxDao = outboxDao;
        this.workloadClient = workloadClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        LOGGER.info("HEARTBEAT: Scheduler is checking for pending tasks...");
        List<WorkloadOutbox> pendingTasks = outboxDao.findByStatus("PENDING");

        for (WorkloadOutbox task : pendingTasks) {
            try {
                MDC.put("transactionId", task.getTransactionId());

                TrainerWorkloadRequest request = objectMapper.readValue(
                        task.getPayload(), TrainerWorkloadRequest.class);

                workloadClient.sendUpdate(request);

                task.setStatus("SENT");
                outboxDao.update(task);

                LOGGER.info("TRANSACTION COMPLETED - Workload updated for {}", request.getTrainerUsername());

            } catch (Exception e) {
                // If service is down OR Circuit Breaker is OPEN:
                // We log it and leave status as PENDING. It will retry in 5 seconds.
                LOGGER.warn("TRANSACTION PENDING - Remote service unavailable for TX: {}. Error: {}",
                        task.getTransactionId(), e.getMessage());
            } finally {
                MDC.remove("transactionId");
            }
        }
    }
}
