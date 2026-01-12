package com.example.service;

import com.example.domain.Timeslot;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class TimeslotService {

    @Inject
    private EntityManagerFactory emf;

    public void create(Timeslot timeslot) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(timeslot);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Timeslot findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Timeslot.class, id);
        } finally {
            em.close();
        }
    }

    public List<Timeslot> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Timeslot t", Timeslot.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Timeslot update(Long id, Timeslot data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Timeslot existing = em.find(Timeslot.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setStart(data.getStart());
            existing.setEndTime(data.getEndTime());
            existing.setReserved(data.isReserved());
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
            Timeslot existing = em.find(Timeslot.class, id);
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
