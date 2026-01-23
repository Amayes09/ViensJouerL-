package com.example.service;

import com.example.domain.Notification;
import com.example.domain.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

@Stateless
public class NotificationService {

    @Inject
    private EntityManagerFactory emf;

    public void create(Notification notification) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Notification findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Notification.class, id);
        } finally {
            em.close();
        }
    }

    public List<Notification> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT n FROM Notification n", Notification.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Notification update(Long id, Notification data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Notification existing = em.find(Notification.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setMessage(data.getMessage());
            existing.setUser(data.getUser());
            em.getTransaction().commit();
            return existing;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Notification existing = em.find(Notification.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(existing);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void createNotification(Long userId, String message) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            User managedUser = em.find(User.class, userId);
            if (managedUser == null) {
                em.getTransaction().rollback();
                System.out.println("⚠️ Notification non créée : userId=" + userId + " introuvable");
                return;
            }
            System.out.println("✅ utilisateur rechargé en base → email=" + managedUser.getEmail());

            Notification notification = new Notification(message, managedUser);
            em.persist(notification);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

}
