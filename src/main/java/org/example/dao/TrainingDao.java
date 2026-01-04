package org.example.dao;

import org.example.entities.Training;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingDao {
    private final SessionFactory sessionFactory;

    public TrainingDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addTraining(Training training) {
        sessionFactory.getCurrentSession().save(training);
    }
}
