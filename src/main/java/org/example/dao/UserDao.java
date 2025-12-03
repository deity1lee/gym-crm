package org.example.dao;

import org.example.entities.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Optional<User> findByUsername(String username) {
        String hql = "FROM User u WHERE u.username = :username";
        return sessionFactory.getCurrentSession().createQuery(hql, User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public void updatePassword(Long id, String newPassword) {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "UPDATE User u SET u.password = :password WHERE u.id = :id"
        );
        query.setParameter("password", newPassword);
        query.setParameter("id", id);
        query.executeUpdate();
    }
}

