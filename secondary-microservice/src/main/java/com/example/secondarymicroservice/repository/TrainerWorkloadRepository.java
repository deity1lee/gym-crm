package com.example.secondarymicroservice.repository;

import com.example.secondarymicroservice.entities.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TrainerWorkloadRepository
        extends JpaRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}