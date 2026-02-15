package com.example.repository;

import com.example.entities.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository
        extends JpaRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}