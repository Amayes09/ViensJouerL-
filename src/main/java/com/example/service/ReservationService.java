package com.example.service;

import java.util.List;

import com.example.domain.Event;
import com.example.domain.Reservation;
import com.example.domain.User;
import com.example.domain.Venue;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class ReservationService {

    @PersistenceContext
    EntityManager em;

    public Reservation createReservation(User user, Event event, Venue venue) {
        Reservation reservation = new Reservation(user, event, venue);
        em.persist(reservation);
        return reservation;
    }

    public Reservation findReservation(Long id) {
        return em.find(Reservation.class, id);
    }

    public List<Reservation> getAllReservations() {
        Query q = em.createQuery("SELECT r FROM Reservation r");
        return q.getResultList();
    }

    public List<Reservation> findReservationsByUser(Long userId) {
        Query q = em.createQuery("SELECT r FROM Reservation r WHERE r.user.id = :userId");
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    public List<Reservation> findReservationsByEvent(Long eventId) {
        Query q = em.createQuery("SELECT r FROM Reservation r WHERE r.event.id = :eventId");
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    public List<Reservation> findReservationsByVenue(Long venueId) {
        Query q = em.createQuery("SELECT r FROM Reservation r WHERE r.venue.id = :venueId");
        q.setParameter("venueId", venueId);
        return q.getResultList();
    }

    public Reservation updateReservation(Long id, User user, Event event, Venue venue) {
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation != null) {
            reservation.setUser(user);
            reservation.setEvent(event);
            reservation.setVenue(venue);
            em.merge(reservation);
        }
        return reservation;
    }

    public void deleteReservation(Long id) {
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation != null) {
            em.remove(reservation);
        }
    }

    public void cancelReservation(Long id) {
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation != null) {
            em.remove(reservation);
        }
    }
}
