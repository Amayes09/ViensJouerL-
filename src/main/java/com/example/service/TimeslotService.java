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
}