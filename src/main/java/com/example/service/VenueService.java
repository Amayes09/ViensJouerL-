package com.example.service;

import java.util.List;

import com.example.domain.Venue;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class VenueService {

    @PersistenceContext
    EntityManager em;

    public Venue createVenue(String name, String address, String postalCode, String city) {
        Venue venue = new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setPostalCode(postalCode);
        venue.setCity(city);
        em.persist(venue);
        return venue;
    }

    public Venue findVenue(Long id) {
        return em.find(Venue.class, id);
    }

    public List<Venue> getAllVenues() {
        Query q = em.createQuery("SELECT v FROM Venue v");
        return q.getResultList();
    }

    public Venue updateVenue(Long id, String name, String address, String postalCode, String city) {
        Venue venue = em.find(Venue.class, id);
        if (venue != null) {
            venue.setName(name);
            venue.setAddress(address);
            venue.setPostalCode(postalCode);
            venue.setCity(city);
            em.merge(venue);
        }
        return venue;
    }

    public void deleteVenue(Long id) {
        Venue venue = em.find(Venue.class, id);
        if (venue != null) {
            em.remove(venue);
        }
    }

    public List<Venue> findVenuesByCity(String city) {
        Query q = em.createQuery("SELECT v FROM Venue v WHERE v.city = :city");
        q.setParameter("city", city);
        return q.getResultList();
    }

    public List<Venue> findVenuesByPostalCode(String postalCode) {
        Query q = em.createQuery("SELECT v FROM Venue v WHERE v.postalCode = :postalCode");
        q.setParameter("postalCode", postalCode);
        return q.getResultList();
    }
}
