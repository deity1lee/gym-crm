package org.example.dao;

import org.example.dto.TrainingTypeCountPerTrainer;
import org.example.entities.Training;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Repository
public class TrainingDao {

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
}
