package com.example.service;

import com.example.domain.Timeslot;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import com.example.domain.Venue;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import com.example.domain.Venue;

public class TimeslotService {

    @Inject
    private EntityManagerFactory emf;

    public Timeslot create(Timeslot timeslot) {
        EntityManager em = emf.createEntityManager();
        try {
            if (timeslot == null) throw new BadRequestException("Timeslot manquant");
            if (timeslot.getStart() == null || timeslot.getEndTime() == null)
                throw new BadRequestException("start et endTime sont obligatoires");
            if (timeslot.getVenue() == null || timeslot.getVenue().getId() == null)
                throw new BadRequestException("venue.id est obligatoire");

            em.getTransaction().begin();

            // Venue "managé" (très important)
            Long venueId = timeslot.getVenue().getId();
            Venue managedVenue = em.find(Venue.class, venueId);
            if (managedVenue == null) throw new NotFoundException("Venue introuvable: " + venueId);

            timeslot.setVenue(managedVenue);

            em.persist(timeslot);
            em.getTransaction().commit();
            return timeslot;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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

            if (data.getVenue() != null && data.getVenue().getId() != null) {
                Venue managedVenue = em.find(Venue.class, data.getVenue().getId());
                if (managedVenue == null) {
                    em.getTransaction().rollback();
                    return null; // ou NotFoundException
                }
                existing.setVenue(managedVenue);
            }

            em.getTransaction().commit();
            return existing;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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
