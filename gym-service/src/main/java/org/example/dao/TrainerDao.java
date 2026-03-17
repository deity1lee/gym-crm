package org.example.dao;

import org.example.dto.TrainerTraineeCount;
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
public class TrainerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDao.class);

    @PersistenceContext
    private EntityManager em;

    public void create(Trainer trainer) {
        em.persist(trainer);
        String tx = MDC.get("transactionId");
        LOGGER.info("[{}] Trainee with id {} was created.", tx, trainer.getId());
    }

    public List<TrainerTraineeCount> findTrainerTraineeCounts() {
        return em.createQuery(
                "SELECT new org.example.dto.TrainerTraineeCount(t.username, size(t.trainees)) " +
                        "FROM Trainer t",
                TrainerTraineeCount.class
        ).getResultList();
    }


    public void updateProfile(Trainer trainer) {
        String tx = MDC.get("transactionId");
        em.merge(trainer);
        LOGGER.info("[{}] Updated trainer profile with id {}", tx, trainer.getId());
    }

    public List<Training> findByTrainerUsernameAndCriteria(TrainingSearchCriteria criteria) {
        StringBuilder jpql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainer.username = :trainerUsername"
        );

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(value -> jpql.append(" AND tr.date >= :fromDate"));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(value -> jpql.append(" AND tr.date <= :toDate"));

        Optional.ofNullable(criteria.getTraineeUsername())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> jpql.append(" AND tr.trainee.username = :traineeUsername"));

        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);
        query.setParameter("trainerUsername", criteria.getTrainerUsername());

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(v -> query.setParameter("fromDate", v));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(v -> query.setParameter("toDate", v));

        Optional.ofNullable(criteria.getTraineeUsername())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> query.setParameter("traineeUsername", value));
        return query.getResultList();
    }

    public Optional<Trainer> findByUsername(String username) {
        String txId = MDC.get("transactionId");

        try {
            List<Trainer> results = em.createQuery(
                            "select t from Trainer t where t.username = :username", Trainer.class)
                    .setParameter("username", username)
                    .getResultList();

            if (results.isEmpty()) {
                LOGGER.debug("[{}] Trainer '{}' not found", txId, username);
                return Optional.empty();
            }

            return Optional.of(results.get(0));

        } catch (Exception ex) {
            LOGGER.error("[{}] DAO error fetching trainer '{}'", txId, username, ex);
            throw ex;
        }
    }

    public long countActiveTrainersWithoutTrainees() {
        String jpql = """
            SELECT COUNT(tr)
            FROM Trainer tr
            LEFT JOIN tr.trainees tn
            WHERE tn IS NULL
              AND tr.isActive = true
        """;

        return em.createQuery(jpql, Long.class)
                .getSingleResult();
    }

    public long countByFullName(String firstName, String lastName) {

        LOGGER.info("Counting number of trainers with full name {} {}", firstName, lastName);
        long count = em.createQuery(
                        "SELECT COUNT(t) FROM Trainer t WHERE t.firstName = :firstName AND t.lastName = :lastName",
                        Long.class
                )
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getSingleResult();
        LOGGER.info("Number of trainers by first name '{}' and last name '{}': {}", firstName, lastName, count);

        return count;
    }

    public List<Trainer> findAllById(List<Long> trainerIds) {
        return em.createQuery(
                        "SELECT t FROM Trainer t WHERE t.id IN :ids", Trainer.class)
                .setParameter("ids", trainerIds)
                .getResultList();
    }

    public Optional<Trainer> findById(Long trainerId) {
        Trainer trainer = em.createQuery(
                        "SELECT t FROM Trainer t WHERE t.id = :id", Trainer.class)
                .setParameter("id", trainerId)
                .getSingleResult();
        return Optional.ofNullable(trainer);
    }
}
