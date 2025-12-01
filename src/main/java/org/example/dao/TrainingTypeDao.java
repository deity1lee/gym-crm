package org.example.dao;

import org.example.entities.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public class TrainingTypeDao {

    private final SessionFactory sessionFactory;

    public TrainingTypeDao(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Optional<TrainingType> findByName(String name) {
        String hql = "FROM TrainingType tt WHERE tt.trainingTypeName = :name";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
