package org.example.dao;

import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utilities.TrainingSearchCriteria;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.util.*;

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

    public void updatePassword(Long traineeId, String newPassword) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "UPDATE Trainee t SET t.password = :password WHERE t.id = :id"
        );
        query.setParameter("password", newPassword);
        query.setParameter("id", traineeId);
        query.executeUpdate();
    }

    public void updateProfile(Trainee trainee) {
        LOGGER.info("Updated trainee profile with id {}", trainee.getId());
    }

    public void activateTrainee(Trainee trainee) {
        if (!trainee.isActive()) {
            trainee.setActive(true);
            LOGGER.debug("Trainee profile with username is activated: {}", trainee.getUsername());
            return;
        }
        LOGGER.debug("Trainee profile with username is already activated: {}", trainee.getUsername());
    }

    public void deActivateTrainee(Trainee trainee) {
        if (!trainee.isActive()) {
            trainee.setActive(true);
            LOGGER.debug("Trainee profile with username is activated: {}", trainee.getUsername());
            return;
        }
        LOGGER.debug("Trainee profile with username is already activated: {}", trainee.getUsername());
    }

    public void deleteTrainee(Trainee trainee) {
        sessionFactory.getCurrentSession().delete(trainee);
        LOGGER.info("Deleted trainee and related trainings for username {}", trainee.getUsername());
    }

    public List<Training> findByTraineeUsernameAndCriteria(
            String username,
            TrainingSearchCriteria criteria
    ) {
        StringBuilder hql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainee.username = :username"
        );

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(value -> hql.append(" AND tr.date >= :fromDate"));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(value -> hql.append(" AND tr.date <= :toDate"));

        Optional.ofNullable(criteria.getUserLastName())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> hql.append(" AND tr.trainer.lastName = :userLastName"));

        Optional.ofNullable(criteria.getTrainingType())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> hql.append(" AND tr.trainingType.trainingTypeName = :trainingType"));

        Query<Training> query = sessionFactory.getCurrentSession().createQuery(hql.toString(), Training.class);
        query.setParameter("username", username);

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(value -> query.setParameter("fromDate", value));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(value -> query.setParameter("toDate", value));

        Optional.ofNullable(criteria.getUserLastName())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> query.setParameter("userLastName", value));

        Optional.ofNullable(criteria.getTrainingType())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> query.setParameter("trainingType", value));
        return query.getResultList();
    }

    public List<Trainer> findUnassignedTrainers(String traineeUsername) {

        LOGGER.debug("Fetching trainers that are not assigned on trainee by username: {}", traineeUsername);
        String hql = """
        SELECT tr FROM Trainer tr
        WHERE tr.id NOT IN (
            SELECT t.id FROM Trainee tn
            JOIN tn.trainers t
            WHERE tn.username = :username
        )
        """;
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    public Optional<Trainee> findByUsername(String username) {
        String hql = "select t from Trainee t where t.username = :username";
        LOGGER.debug("Fetching trainee with username: {}", username);
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public long countByFullName(String firstName, String lastName) {
        LOGGER.info("Counting number of trainees with full name {} {}", firstName, lastName);
        long count = sessionFactory.getCurrentSession().createQuery(
                        "SELECT COUNT(t) FROM Trainee t WHERE t.firstName = :firstName AND t.lastName = :lastName",
                        Long.class
                )
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
        LOGGER.info("Number of trainees by first name '{}' and last name '{}': {}", firstName, lastName, count);
        return count;
    }
}
