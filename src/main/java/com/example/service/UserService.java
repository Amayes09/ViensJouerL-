package com.example.service;

import com.example.domain.User;
import com.example.messaging.UserCreatedProducer;
import jakarta.inject.Inject; // Attention : utiliser javax.inject ou jakarta.inject selon version
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

// On enlève @Stateless qui ne marche pas sans Payara
public class UserService {

    // On injecte la Factory au lieu de l'EntityManager direct
    @Inject
    private EntityManagerFactory emf;

    @Inject
    private UserCreatedProducer producer;

    public User register(User user) {
        // 1. Créer l'EntityManager
        EntityManager em = emf.createEntityManager();
        try {
            // 2. Commencer la transaction
            em.getTransaction().begin();

            // 3. Sauvegarder
            em.persist(user);

            // 4. Valider la transaction (Commit)
            em.getTransaction().commit();

            // Notification (Simulation)
            if (producer != null)
                producer.sendUserCreatedEvent(user);

            return user;
        } catch (Exception e) {
            // En cas d'erreur, annuler
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            // 5. Toujours fermer l'EntityManager
            em.close();
        }
    }

    public User findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public User update(Long id, User data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User existing = em.find(User.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setName(data.getName());
            existing.setEmail(data.getEmail());
            existing.setPassword(data.getPassword());
            existing.setIsAdmin(data.getIsAdmin());
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
            User existing = em.find(User.class, id);
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
}
