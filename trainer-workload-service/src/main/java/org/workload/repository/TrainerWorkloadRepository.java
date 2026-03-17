package org.workload.repository;

import org.workload.entities.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository
        extends JpaRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}