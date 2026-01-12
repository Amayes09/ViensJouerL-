package com.example.service;

import com.example.domain.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class EventService {

    @Inject
    private EntityManagerFactory emf;

    public void create(Event event) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Event findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Event.class, id);
        } finally {
            em.close();
        }
    }

    public List<Event> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Event e", Event.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Event update(Long id, Event data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Event existing = em.find(Event.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setTitle(data.getTitle());
            existing.setDescription(data.getDescription());
            existing.setEventDate(data.getEventDate());
            existing.setGameType(data.getGameType());
            existing.setCapacity(data.getCapacity());
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
            Event existing = em.find(Event.class, id);
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
