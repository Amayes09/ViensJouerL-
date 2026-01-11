package com.example.service;

import com.example.domain.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    // @Inject
    // private UserCreatedProducer producer;

    public User register(User user) {
        // Ici, on pourrait hacher le mot de passe avant de sauvegarder
        em.persist(user);
        // Envoi de la notification JMS
        // producer.sendUserCreatedEvent(user);
        return user;
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }
}