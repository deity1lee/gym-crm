package org.example.dao;

import org.example.dto.TrainerWorkloadRequest;
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
        em.remove(training);
        LOGGER.info("[{}] Training with id = {} was successfully deleted", txId, training.getId());
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

    public Training findTrainingByTrainerWorkloadRequest(TrainerWorkloadRequest request) {
        String txId = MDC.get("transactionId");

        return em.createQuery(
                        """
                        select t
                        from Training t
                        where t.trainer.username = :username
                          and t.trainer.firstName = :firstName
                          and t.trainer.lastName = :lastName
                          and t.trainer.isActive = :isActive
                          and t.date = :trainingDate
                          and t.duration = :trainingDuration
                        """,
                        Training.class
                )
                .setParameter("username", request.getTrainerUsername())
                .setParameter("firstName", request.getTrainerFirstName())
                .setParameter("lastName", request.getTrainerLastName())
                .setParameter("isActive", request.isActive())
                .setParameter("trainingDate", request.getTrainingDate())
                .setParameter("trainingDuration", request.getTrainingDuration())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.debug(
                            "[{}] Training not found for trainer '{} {}' on {}",
                            txId,
                            request.getTrainerFirstName(),
                            request.getTrainerLastName(),
                            request.getTrainingDate()
                    );
                    return new IllegalArgumentException("Training not found for given request");
                });
    }
}
