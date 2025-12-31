package com.example.service;

import com.example.domain.Reservation;
import com.example.domain.User;
import com.example.domain.Venue;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Stateless
public class ReservationService {

    @PersistenceContext
    private EntityManager em;

    public Reservation createReservation(Long userId, Long venueId, Date date) throws Exception {
        User user = em.find(User.class, userId);
        Venue venue = em.find(Venue.class, venueId);

        if (user == null || venue == null) {
            throw new IllegalArgumentException("Utilisateur ou Salle introuvable");
        }

        if (!venue.getIsAvailable()) {
            throw new IllegalStateException("La salle n'est pas disponible");
        }

        // Vérification simplifiée de chevauchement (à étoffer selon besoins)
        // Check if a reservation exists for this venue at this time...
        
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setVenue(venue);
        reservation.setReservationDate(date);
        
        em.persist(reservation);
        return reservation;
    }

    public List<Reservation> findAll() {
        return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
    }
}