package org.example.dao;

import org.example.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    @PersistenceContext
    private EntityManager em;

    public Optional<User> findByUsername(String username) {
        String txId = MDC.get("transactionId");

        try {
            List<User> results = em.createQuery(
                            "select t from User t where t.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultList();

            if (results.isEmpty()) {
                LOGGER.debug("[{}] User '{}' not found", txId, username);
                return Optional.empty();
            }

            return Optional.of(results.get(0));

        } catch (Exception ex) {
            LOGGER.error("[{}] DAO error fetching user '{}'", txId, username, ex);
            throw ex;
        }
    }

    public void updatePassword(Long id, String newPassword) {
        em.createQuery("UPDATE User u SET u.password = :password WHERE u.id = :id")
                .setParameter("password", newPassword)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void save(User user) {
        em.merge(user);
    }
}
