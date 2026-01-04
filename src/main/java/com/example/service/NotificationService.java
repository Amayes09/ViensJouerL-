package com.example.service;

import java.time.Instant;
import java.util.List;

import com.example.domain.Notification;
import com.example.domain.User;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class NotificationService {

    @PersistenceContext
    EntityManager em;

    public Notification createNotification(User user, String message) {
        Notification notification = new Notification(message, user);
        em.persist(notification);
        return notification;
    }

    public Notification findNotification(Long id) {
        return em.find(Notification.class, id);
    }

    public List<Notification> getAllNotifications() {
        Query q = em.createQuery("SELECT n FROM Notification n ORDER BY n.createdAt DESC");
        return q.getResultList();
    }

    public List<Notification> findNotificationsByUser(Long userId) {
        Query q = em.createQuery("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC");
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    public List<Notification> findRecentNotifications(Long userId, int days) {
        Query q = em.createQuery(
            "SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "AND n.createdAt >= :startDate ORDER BY n.createdAt DESC"
        );
        q.setParameter("userId", userId);
        Instant startDate = Instant.now().minusSeconds(days * 86400L);
        q.setParameter("startDate", startDate);
        return q.getResultList();
    }

    public Notification updateNotification(Long id, String message) {
        Notification notification = em.find(Notification.class, id);
        if (notification != null) {
            notification.setMessage(message);
            em.merge(notification);
        }
        return notification;
    }

    public void deleteNotification(Long id) {
        Notification notification = em.find(Notification.class, id);
        if (notification != null) {
            em.remove(notification);
        }
    }

    public void deleteOldNotifications(int days) {
        Instant cutoffDate = Instant.now().minusSeconds(days * 86400L);
        Query q = em.createQuery("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate");
        q.setParameter("cutoffDate", cutoffDate);
        q.executeUpdate();
    }
}
