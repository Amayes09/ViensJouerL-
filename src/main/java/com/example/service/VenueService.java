package com.example.service;

import com.example.domain.Venue;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

@Stateless
public class VenueService {

    @Inject
    private EntityManagerFactory emf;

    public void create(Venue venue) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(venue);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Venue findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Venue.class, id);
        } finally {
            em.close();
        }
    }

    public List<Venue> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT v FROM Venue v", Venue.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Venue update(Long id, Venue data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Venue existing = em.find(Venue.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setName(data.getName());
            existing.setAddress(data.getAddress());
            existing.setCapacity(data.getCapacity());
            existing.setIsAvailable(data.getIsAvailable());
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
            Venue existing = em.find(Venue.class, id);
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
