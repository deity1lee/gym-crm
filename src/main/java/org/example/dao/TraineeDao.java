package org.example.dao;

import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utilities.TrainingSearchCriteria;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDao.class);

    private final SessionFactory sessionFactory;

    public TraineeDao(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public void create(Trainee trainee) {
        sessionFactory.getCurrentSession().save(trainee);
        LOGGER.info("Created trainee with id {}", trainee.getId());
    }

    public void updateProfile(Trainee trainee) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Updated trainee profile with username {}", txId, trainee.getUsername());
        sessionFactory.getCurrentSession().merge(trainee);
    }

    public void deleteTrainee(String username) {
        String txId = MDC.get("transactionId");
        LOGGER.info("[{}] Trying to delete trainee profile with username '{}'", txId, username);
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] Deletion failed: trainee '{}' not found", txId, username);
                    return new IllegalArgumentException(String.format("Trainee not found: %s", username));
                });

        sessionFactory.getCurrentSession().delete(trainee);

        LOGGER.info("[{}] Deleted trainee and related trainings for username '{}'", txId, username);
    }

    public List<Training> findByTraineeUsernameAndCriteria(
            TrainingSearchCriteria criteria
    ) {
        StringBuilder hql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainee.username = :traineeUsername"
        );

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(v -> hql.append(" AND tr.date >= :fromDate"));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(v -> hql.append(" AND tr.date <= :toDate"));

        Optional.ofNullable(criteria.getTrainerUsername())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> hql.append(" AND tr.trainer.username = :trainerUsername"));

        Optional.ofNullable(criteria.getTrainingTypeId())
                .ifPresent(value -> hql.append(" AND tr.trainingType.id = :trainingTypeId"));

        Query<Training> query = sessionFactory.getCurrentSession().createQuery(hql.toString(), Training.class);

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
        String hql = """
        SELECT tr FROM Trainer tr
        WHERE tr.id NOT IN (
            SELECT t.id FROM Trainee tn
            JOIN tn.trainers t
            WHERE tn.username = :username
        )
            AND tr.isActive = true
        
        """;
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    public Optional<Trainee> findByUsername(String username) {
        String hql = "select t from Trainee t where t.username = :username";
        String txId = MDC.get("transactionId");
        LOGGER.debug("[{}] Fetching trainee with username: {}", txId, username);
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public long countByFullName(String firstName, String lastName) {
        return sessionFactory.getCurrentSession().createQuery(
                        "SELECT COUNT(t) FROM Trainee t WHERE t.firstName = :firstName AND t.lastName = :lastName",
                        Long.class
                )
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
    }
}
