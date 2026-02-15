package org.example.dao;

import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utilities.TrainingSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.*;

@Repository
public class TraineeDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDao.class);

    @PersistenceContext
    private EntityManager em;

    public void create(Trainee trainee) {
        em.persist(trainee);
        String tx = MDC.get("transactionId");
        LOGGER.info("[{}] Trainee with id {} was created.", tx, trainee.getId());
    }

    public void updateProfile(Trainee trainee) {
        String txId = MDC.get("transactionId");
        em.merge(trainee);
        LOGGER.info("[{}] Updated trainee profile with username {}", txId, trainee.getUsername());
    }

    public void deleteTrainee(Trainee trainee) {
        String txId = MDC.get("transactionId");

        LOGGER.info("[{}] Trying to delete trainee profile with username '{}'", txId, trainee.getUsername());
        em.remove(trainee);
        LOGGER.info("[{}] Deleted trainee", txId);
    }

    public List<Training> findByTraineeUsernameAndCriteria(
            TrainingSearchCriteria criteria
    ) {
        StringBuilder jpql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainee.username = :traineeUsername"
        );

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(value -> jpql.append(" AND tr.date >= :fromDate"));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(value -> jpql.append(" AND tr.date <= :toDate"));

        Optional.ofNullable(criteria.getTrainerUsername())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> jpql.append(" AND tr.trainer.username = :trainerUsername"));

        Optional.ofNullable(criteria.getTrainingTypeId())
                .ifPresent(value -> jpql.append(" AND tr.trainingType.id = :trainingTypeId"));

        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);
        query.setParameter("traineeUsername", criteria.getTraineeUsername());

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(v -> query.setParameter("fromDate", v));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(v -> query.setParameter("toDate", v));

        Optional.ofNullable(criteria.getTrainerUsername())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> query.setParameter("trainerUsername", value));

        Optional.ofNullable(criteria.getTrainingTypeId())
                .ifPresent(value -> query.setParameter("trainingTypeId", value));
        return query.getResultList();
    }

    public List<Trainer> findUnassignedTrainers(String traineeUsername) {
        String txId = MDC.get("transactionId");
        LOGGER.debug("[{}] Fetching trainers that are not assigned on trainee by username: {}", txId, traineeUsername);
        String jpql = """
        SELECT tr FROM Trainer tr
        WHERE tr.id NOT IN (
            SELECT t.id FROM Trainee tn
            JOIN tn.trainers t
            WHERE tn.username = :username
        )
            AND tr.isActive = true
        
        """;
        return em.createQuery(jpql, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    public Optional<Trainee> findByUsername(String username) {
        String txId = MDC.get("transactionId");

        List<Trainee> results = em.createQuery(
                        "select t from Trainee t where t.username = :username",
                        Trainee.class)
                .setParameter("username", username)
                .getResultList();

        if (results.isEmpty()) {
            LOGGER.debug("[{}] Trainee '{}' not found", txId, username);
            return Optional.empty();
        }

        return Optional.of(results.get(0));
    }

    public long countByFullName(String firstName, String lastName) {
        return em.createQuery(
                        "SELECT COUNT(t) FROM Trainee t WHERE t.firstName = :firstName AND t.lastName = :lastName",
                        Long.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getSingleResult();
    }

    public long countActiveTrainees() {
        return em.createQuery(
                "SELECT COUNT(t) FROM Trainee t WHERE t.isActive = true", Long.class
        ).getSingleResult();
    }
}
