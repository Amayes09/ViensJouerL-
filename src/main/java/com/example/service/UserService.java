package com.example.service;

import java.util.List;

import com.example.domain.User;
import com.example.messaging.UserCreatedProducer;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

@Stateless
public class UserService {

    @Inject
    private EntityManagerFactory emf;

    @Inject
    private UserCreatedProducer producer;

    public User register(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            if (producer != null)
                producer.sendUserCreatedEvent(user);

            return user;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
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

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void setProducer(UserCreatedProducer producer) {
        this.producer = producer;
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
