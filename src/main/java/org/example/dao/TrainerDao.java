package org.example.dao;

import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utilities.TrainingSearchCriteria;
import org.hibernate.Session;
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

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void create(Trainer trainer) {
        getSession().save(trainer);
        LOGGER.info("Created trainer with id {}", trainer.getId());
    }

    public void updatePassword(Long trainerId, String newPassword) {
        Query query = getSession().createQuery(
                "UPDATE Trainer t SET t.password = :password WHERE t.id = :id"
        );
        query.setParameter("password", newPassword);
        query.setParameter("id", trainerId);
        query.executeUpdate();
    }

    public void updateProfile(Trainer trainer) {
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

    public void deActivateTrainer(Trainer trainer) {
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

        if (criteria.getFromDate() != null) {
            hql.append(" AND tr.date >= :fromDate");
        }

        if (criteria.getToDate() != null) {
            hql.append(" AND tr.date <= :toDate");
        }

        if (criteria.getUserLastName() != null && !criteria.getUserLastName().isEmpty()) {
            hql.append(" AND tr.trainee.lastName = :userLastName");
        }

        Query<Training> query = getSession().createQuery(hql.toString(), Training.class);

        query.setParameter("username", username);

        if (criteria.getFromDate() != null) {
            query.setParameter("fromDate", criteria.getFromDate());
        }

        if (criteria.getToDate() != null) {
            query.setParameter("toDate", criteria.getToDate());
        }

        if (criteria.getUserLastName() != null && !criteria.getUserLastName().isEmpty()) {
            query.setParameter("userLastName", criteria.getUserLastName());
        }

        return query.getResultList();
    }

    public Optional<Trainer> findByUsername(String username) {
        String hql = "select t from Trainer t where t.username = :username";
        LOGGER.debug("Fetching trainer with username: {}", username);
        return getSession()
                .createQuery(hql, Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }


    public long countByFullName(String firstName, String lastName) {
        LOGGER.info("Counting number of trainers with full name {} {}", firstName, lastName);
        long count = getSession().createQuery(
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

