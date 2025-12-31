package com.example.service;

import com.example.domain.Venue;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class VenueService {

    @PersistenceContext
    private EntityManager em;

    public Venue createVenue(Venue venue) {
        em.persist(venue);
        return venue;
    }

    public List<Venue> findAll() {
        return em.createQuery("SELECT v FROM Venue v", Venue.class).getResultList();
    }
    
    public Venue findById(Long id) {
        return em.find(Venue.class, id);
    }
}