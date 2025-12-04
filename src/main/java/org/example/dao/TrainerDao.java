package org.example.dao;

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
public class TrainerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDao.class);
    private final SessionFactory sessionFactory;

    public TrainerDao(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public void create(Trainer trainer) {
        sessionFactory.getCurrentSession().save(trainer);
        LOGGER.info("Created trainer with id {}", trainer.getId());
    }

    public void updatePassword(Long trainerId, String newPassword) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "UPDATE Trainer t SET t.password = :password WHERE t.id = :id"
        );
        query.setParameter("password", newPassword);
        query.setParameter("id", trainerId);
        query.executeUpdate();
    }

    public void updateProfile(Trainer trainer) {
        sessionFactory.getCurrentSession().merge(trainer);
        LOGGER.info("Updated trainer profile with id {}", trainer.getId());
    }

    public void activateTrainer(Trainer trainer) {
        if (!trainer.isActive()) {
            trainer.setActive(true);
            LOGGER.debug("Trainer profile with username is activated: {}", trainer.getUsername());
            return;
        }
        LOGGER.debug("Trainer profile with username is already activated: {}", trainer.getUsername());
    }

    public void deactivateTrainer(Trainer trainer) {
        if (!trainer.isActive()) {
            trainer.setActive(true);
            LOGGER.debug("Trainer profile with username is activated: {}", trainer.getUsername());
            return;
        }
        LOGGER.debug("Trainer profile with username is already activated: {}", trainer.getUsername());
    }

    public List<Training> findByTrainerUsernameAndCriteria(
            String username,
            TrainingSearchCriteria criteria
    ) {
        StringBuilder hql = new StringBuilder(
                "SELECT tr FROM Training tr WHERE tr.trainer.username = :username"
        );
        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(value -> hql.append(" AND tr.date >= :fromDate"));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(value -> hql.append(" AND tr.date <= :toDate"));

        Optional.ofNullable(criteria.getUserLastName())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> hql.append(" AND tr.trainee.lastName = :userLastName"));

        Query<Training> query = sessionFactory.getCurrentSession().createQuery(hql.toString(), Training.class);        query.setParameter("username", username);

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(value -> query.setParameter("fromDate", value));

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(value -> query.setParameter("toDate", value));

        Optional.ofNullable(criteria.getUserLastName())
                .filter(s -> !s.isEmpty())
                .ifPresent(value -> query.setParameter("userLastName", value));
        return query.getResultList();
    }

    public Optional<Trainer> findByUsername(String username) {
        String hql = "select t from Trainer t where t.username = :username";
        LOGGER.debug("Fetching trainer with username: {}", username);
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public long countByFullName(String firstName, String lastName) {
        LOGGER.info("Counting number of trainers with full name {} {}", firstName, lastName);
        long count = sessionFactory.getCurrentSession().createQuery(
                        "SELECT COUNT(t) FROM Trainer t WHERE t.firstName = :firstName AND t.lastName = :lastName",
                        Long.class
                )
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
        LOGGER.info("Number of trainers by first name '{}' and last name '{}': {}", firstName, lastName, count);
        return count;
    }
}
