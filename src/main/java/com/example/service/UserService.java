package com.example.service;

import com.example.domain.User;
import com.example.messaging.UserCreatedProducer;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * ✅ UserService avec gestion d'erreurs robuste
 * - Valide les entrées (400)
 * - Gère email unique (409 Conflict)
 * - Envoie événement JMS après création
 */
public class UserService {

    @Inject
    private EntityManagerFactory emf;

    @Inject
    private UserCreatedProducer producer;

    /**
     * ✅ Créer utilisateur avec validations + gestion email unique
     * Throws: BadRequestException (400), WebApplicationException 409 (Conflict)
     */
    public User register(User user) {
        // Validation d'entrée (400)
        if (user == null) {
            throw new BadRequestException("User ne peut pas être null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new BadRequestException("Name est obligatoire");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email est obligatoire");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password est obligatoire");
        }
        if (!user.getEmail().contains("@")) {
            throw new BadRequestException("Email invalide");
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            // Envoi événement JMS UserCreated
            if (producer != null) {
                producer.sendUserCreatedEvent(user);
            }

            return user;

        } catch (PersistenceException e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            // ✅ Gestion email unique (409 Conflict)
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unique constraint") || msg.contains("duplicate key")) {
                throw new WebApplicationException(
                        Response.status(Response.Status.CONFLICT)
                                .entity("Email '" + user.getEmail() + "' déjà utilisé")
                                .build()
                );
            }

            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID invalide");
        }
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
        if (id == null || id <= 0) {
            throw new BadRequestException("ID invalide");
        }
        if (data == null) {
            throw new BadRequestException("User ne peut pas être null");
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User existing = em.find(User.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            if (data.getName() != null && !data.getName().trim().isEmpty()) {
                existing.setName(data.getName());
            }
            if (data.getEmail() != null && !data.getEmail().trim().isEmpty()) {
                existing.setEmail(data.getEmail());
            }
            if (data.getPassword() != null && !data.getPassword().trim().isEmpty()) {
                existing.setPassword(data.getPassword());
            }
            if (data.getIsAdmin() != null) {
                existing.setIsAdmin(data.getIsAdmin());
            }

            em.getTransaction().commit();
            return existing;
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("unique constraint")) {
                throw new WebApplicationException(
                        Response.status(Response.Status.CONFLICT)
                                .entity("Email déjà utilisé")
                                .build()
                );
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean delete(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID invalide");
        }
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
