package org.example.dao;

import org.example.dto.TrainingTypeCountPerTrainer;
import org.example.entities.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Repository
public class TrainingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingDao.class);

    @PersistenceContext
    private EntityManager em;

    public void addTraining(Training training) {
        em.persist(training);
    }

    public List<TrainingTypeCountPerTrainer> findTrainingTypesPerTrainer() {
        return em.createQuery(
                """
                SELECT new org.example.dto.TrainingTypeCountPerTrainer(
                    tr.username,
                    COUNT(DISTINCT t.trainingType.id)
                )
                FROM Training t
                JOIN t.trainer tr
                GROUP BY tr.username
                """,
                TrainingTypeCountPerTrainer.class
        ).getResultList();
    }

    public void deleteTraining(Training training) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Trying to delete training with trainer {} and duration {}", txId, training.getTrainer(), training.getDuration());
        em.remove(training);
        LOGGER.info("[{}] Training was successfully deleted.", txId);
    }

    public Training findTrainingById(Long id) {
        String txId = MDC.get("transactionId");

        return em.createQuery(
                        "select t from Training t where t.id = :id",
                        Training.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.debug("[{}] Training id='{}' not found", txId, id);
                    return new IllegalArgumentException(
                            "Training with id " + id + " not found");
                });
    }
}
