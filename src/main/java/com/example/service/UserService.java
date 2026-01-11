package com.example.service;

import com.example.domain.User;
import com.example.messaging.UserCreatedProducer; // Assurez-vous d'avoir cette classe
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    // DECOMMENTER L'INJECTION
    @Inject
    private UserCreatedProducer producer;

    public User register(User user) {
        // Persistance
        em.persist(user);

        // DECOMMENTER L'ENVOI JMS
        // Cela envoie un message à Artemis quand un user est créé
        if (producer != null) {
            producer.sendUserCreatedEvent(user);
        }

        return user;
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }
}