package org.example.dao;

import org.example.entities.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public Optional<TrainingType> findById(Long trainingTypeId) {
        String hql = "FROM TrainingType tt WHERE tt.id = :id";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, TrainingType.class)
                .setParameter("id", trainingTypeId)
                .uniqueResultOptional();
    }

    public List<TrainingType> findAll() {
        String hql = "FROM TrainingType";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, TrainingType.class)
                .list();
    }
}
