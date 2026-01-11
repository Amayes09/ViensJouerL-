package com.example.service;

import com.example.domain.Notification;
import com.example.domain.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;

@Stateless
public class NotificationService {

    @PersistenceContext
    private EntityManager em;

    public Notification createNotification(Long userId, String message) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(Instant.now());

        em.persist(notification);
        return notification;
    }

    public Notification findById(Long id) {
        return em.find(Notification.class, id);
    }

    public List<Notification> findAll() {
        return em.createQuery("SELECT n FROM Notification n", Notification.class).getResultList();
    }
}
