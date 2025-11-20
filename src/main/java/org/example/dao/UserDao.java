package org.example.dao;

import org.example.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Optional<User> findByUsername(String username) {
        String hql = "FROM User u WHERE u.username = :username";
        return getSession().createQuery(hql, User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }
}

