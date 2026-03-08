package org.workload.service;

import org.workload.dto.TrainerWorkloadRequest;
import org.workload.entities.TrainerYear;
import org.workload.repository.TrainerWorkloadRepository;
import org.workload.entities.TrainerMonth;
import org.workload.entities.TrainerWorkload;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WorkloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadService.class);

    private final TrainerWorkloadRepository repository;
    private final ValidatorService validatorService;

    public WorkloadService(TrainerWorkloadRepository repository, ValidatorService validatorService) {
        this.repository = repository;
        this.validatorService = validatorService;
    }

    @Transactional
    public void process(TrainerWorkloadRequest request) {

        String txId = MDC.get("transactionId");

        LOGGER.info("[{}] Processing trainer workload request", txId);

        validatorService.validateTrainerWorkloadRequest(request);

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
    }

    private void updateDuration(TrainerWorkload workload, TrainerWorkloadRequest request, Long delta) {
        String txId = MDC.get("transactionId");

        int yearValue = request.getTrainingDate().getYear();
        int monthValue = request.getTrainingDate().getMonthValue();

        TrainerYear year = findOrCreateYear(workload, yearValue);
        TrainerMonth month = findOrCreateMonth(year, monthValue);

        long newTotal = month.getTrainingDuration() + delta;

        if (newTotal == 0) {
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

    public Long getWorkingHours(String username, int month) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Getting working hours for trainer {} month {}", txId, username, month);

        return repository.findByTrainerUsername(username)
                .map(trainer -> trainer.getYears().stream()
                        .flatMap(year -> year.getMonths().stream())
                        .filter(m -> m.getMonth() == month)
                        .map(TrainerMonth::getTrainingDuration)
                        .findFirst()
                        .orElse(0L)
                )
                .orElse(0L);
    }
}
