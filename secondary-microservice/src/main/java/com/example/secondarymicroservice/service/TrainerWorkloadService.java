package com.example.secondarymicroservice.service;

import com.example.secondarymicroservice.dto.ActionType;
import com.example.secondarymicroservice.dto.TrainerWorkloadRequest;
import com.example.secondarymicroservice.entities.TrainerMonth;
import com.example.secondarymicroservice.entities.TrainerWorkload;
import com.example.secondarymicroservice.entities.TrainerYear;
import com.example.secondarymicroservice.repository.TrainerWorkloadRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainerWorkloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadService.class);

    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadService(TrainerWorkloadRepository repository) {
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
}
