package org.workload.service;

import org.springframework.transaction.annotation.Transactional;
import org.workload.repository.TrainerWorkloadRepository;
import org.workload.entities.TrainerMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class WorkloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadService.class);

    private final TrainerWorkloadRepository repository;

    public WorkloadService(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    public Long getWorkingHours(String username, int year, int month) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Getting working hours for trainer {} year {} month {}", txId, username, year, month);

        return repository.findByTrainerUsername(username)
                .map(trainer -> trainer.getYears().stream()
                        .filter(y -> y.getYear() == year)
                        .flatMap(y -> y.getMonths().stream())
                        .filter(m -> m.getMonth() == month)
                        .map(TrainerMonth::getTrainingDuration)
                        .findFirst()
                        .orElse(0L)
                )
                .orElse(0L);
    }
}
