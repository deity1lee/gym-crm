package org.example.dao;

import jakarta.persistence.NoResultException;
import org.example.entities.TrainingType;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeDao {

    @PersistenceContext
    private EntityManager em;

    public Optional<TrainingType> findById(Long id) {
        String hql = "FROM TrainingType tt WHERE tt.id = :id";
        try {
            TrainingType result = em.createQuery(hql, TrainingType.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(result);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<TrainingType> findAll() {
        String hql = "FROM TrainingType";
        return em.createQuery(hql, TrainingType.class).getResultList();
    }
}
