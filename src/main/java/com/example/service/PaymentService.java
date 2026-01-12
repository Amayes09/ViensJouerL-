package com.example.service;

import com.example.domain.Payment;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class PaymentService {

    @Inject
    private EntityManagerFactory emf;

    public void create(Payment payment) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Payment findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
        }
    }

    public List<Payment> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Payment update(Long id, Payment data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Payment existing = em.find(Payment.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setAmount(data.getAmount());
            existing.setMethod(data.getMethod());
            existing.setReservation(data.getReservation());
            existing.setIsConfirmed(data.getIsConfirmed());
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
            Payment existing = em.find(Payment.class, id);
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
