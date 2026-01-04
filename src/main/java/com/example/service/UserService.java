package com.example.service;

import java.util.List;

import com.example.domain.User;
import com.example.messaging.UserCreatedProducer;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class UserService {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserCreatedProducer producer;

    public User createUser(String name, String email) {
        User u = new User(name, email);
        em.persist(u);
        producer.sendUserCreatedEvent(u);
        return u;
    }

    public User findUser(Long id) {
        return em.find(User.class, id);
    }

    public List<User> getAllUsers() {
        Query q = em.createQuery("SELECT u FROM User u");
        return q.getResultList();
    }

    public User updateUser(Long id, String name, String email) {
        User user = em.find(User.class, id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            em.merge(user);
        }
        return user;
    }

    public void deleteUser(Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }

    public User findUserByEmail(String email) {
        try {
            Query q = em.createQuery("SELECT u FROM User u WHERE u.email = :email");
            q.setParameter("email", email);
            return (User) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
