package org.example.dao;

import org.example.entities.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class TrainingTypeDao {

    private final SessionFactory sessionFactory;

    public TrainingTypeDao(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Optional<TrainingType> findByName(String name) {
        String hql = "FROM TrainingType tt WHERE tt.trainingTypeName = :name";
        return getSession()
                .createQuery(hql, TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
