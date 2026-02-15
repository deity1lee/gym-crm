package com.example.service;

import com.example.dto.ActionType;
import com.example.dto.TrainerWorkloadRequest;
import com.example.entities.TrainerYear;
import com.example.repository.TrainerWorkloadRepository;
import com.example.entities.TrainerMonth;
import com.example.entities.TrainerWorkload;
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

    public WorkloadService(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void process(TrainerWorkloadRequest request) {

        String txId = MDC.get("transactionId");

        LOGGER.info("[{}] Processing trainer workload request", txId);

        Optional<TrainerWorkload> trainerOpt = repository.findByTrainerUsername(request.getTrainerUsername());

        if (trainerOpt.isEmpty() && request.getActionType() == ActionType.DELETE) {
            LOGGER.info("[{}] DELETE ignored, trainer not found: {}", txId, request.getTrainerUsername());
            return;
        }

        TrainerWorkload trainer = trainerOpt.orElseGet(() -> createTrainer(request));

        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonth().getValue();

        TrainerYear trainerYear = trainer.getYears()
                .stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    TrainerYear y = new TrainerYear();
                    y.setYear(year);
                    trainer.getYears().add(y);
                    return y;
                });

        TrainerMonth trainerMonth = trainerYear.getMonths()
                .stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    TrainerMonth m = new TrainerMonth();
                    m.setMonth(month);
                    m.setTotalDuration(0L);
                    trainerYear.getMonths().add(m);
                    return m;
                });

        Long delta = request.getActionType() == ActionType.ADD
                ? request.getTrainingDuration()
                : -request.getTrainingDuration();

        long newTotal = trainerMonth.getTotalDuration() + delta;

        if (newTotal <= 0) {
            trainerYear.getMonths().remove(trainerMonth);

            if (trainerYear.getMonths().isEmpty()) {
                trainer.getYears().remove(trainerYear);

                if (trainer.getYears().isEmpty()) {
                    repository.delete(trainer);
                }
            }
        } else {
            trainerMonth.setTotalDuration(newTotal);
            repository.save(trainer);
        }
    }

    public TrainerWorkload createTrainer(TrainerWorkloadRequest request) {
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
                        .map(TrainerMonth::getTotalDuration)
                        .findFirst()
                        .orElse(0L)
                )
                .orElse(0L);
    }
}
