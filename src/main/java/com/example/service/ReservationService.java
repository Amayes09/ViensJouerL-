package com.example.service;

import com.example.domain.Event;
import com.example.domain.Reservation;
import com.example.domain.Timeslot;
import com.example.domain.User;
import com.example.domain.Venue;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import java.util.Date;
import java.util.List;

@Stateless
public class ReservationService {

    @Inject
    private EntityManagerFactory emf;

    public Reservation createWithChecks(
            Long userId,
            Long eventId,
            Long venueId,
            Long timeslotId,
            Date reservationDate
    ) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            User user = requireUser(em, userId);
            Event event = requireEvent(em, eventId);
            Venue venue = requireVenue(em, venueId);
            Timeslot timeslot = requireTimeslot(em, timeslotId);

            validateReservationInputs(event, venue, timeslot, reservationDate, null, em);

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setEvent(event);
            reservation.setVenue(venue);
            reservation.setTimeslot(timeslot);
            reservation.setReservationDate(reservationDate);

            em.persist(reservation);
            em.flush();
            updateTimeslotReservedFlag(em, timeslot);
            em.getTransaction().commit();
            return reservation;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void create(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Reservation findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    public List<Reservation> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Reservation update(Long id, Reservation data) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reservation existing = em.find(Reservation.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setUser(data.getUser());
            existing.setEvent(data.getEvent());
            existing.setVenue(data.getVenue());
            existing.setReservationDate(data.getReservationDate());
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

    public Reservation updateWithChecks(
            Long reservationId,
            Long userId,
            Long eventId,
            Long venueId,
            Long timeslotId,
            Date reservationDate
    ) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reservation existing = em.find(Reservation.class, reservationId);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            User user = requireUser(em, userId);
            Event event = requireEvent(em, eventId);
            Venue venue = requireVenue(em, venueId);
            Timeslot timeslot = requireTimeslot(em, timeslotId);

            validateReservationInputs(event, venue, timeslot, reservationDate, reservationId, em);

            Timeslot oldTimeslot = existing.getTimeslot();
            existing.setUser(user);
            existing.setEvent(event);
            existing.setVenue(venue);
            existing.setTimeslot(timeslot);
            existing.setReservationDate(reservationDate);

            em.flush();
            updateTimeslotReservedFlag(em, timeslot);
            if (oldTimeslot != null && oldTimeslot.getId() != null
                    && !oldTimeslot.getId().equals(timeslot.getId())) {
                Timeslot managedOld = em.find(Timeslot.class, oldTimeslot.getId());
                if (managedOld != null) {
                    updateTimeslotReservedFlag(em, managedOld);
                }
            }

            em.getTransaction().commit();
            return existing;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reservation existing = em.find(Reservation.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return false;
            }
            Timeslot timeslot = existing.getTimeslot();
            em.remove(existing);
            em.flush();
            updateTimeslotReservedFlag(em, timeslot);
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

    private User requireUser(EntityManager em, Long userId) {
        if (userId == null) {
            throw new BadRequestException("userId obligatoire");
        }
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new NotFoundException("User introuvable");
        }
        return user;
    }

    private Event requireEvent(EntityManager em, Long eventId) {
        if (eventId == null) {
            throw new BadRequestException("eventId obligatoire");
        }
        Event event = em.find(Event.class, eventId);
        if (event == null) {
            throw new NotFoundException("Event introuvable");
        }
        return event;
    }

    private Venue requireVenue(EntityManager em, Long venueId) {
        if (venueId == null) {
            throw new BadRequestException("venueId obligatoire");
        }
        Venue venue = em.find(Venue.class, venueId);
        if (venue == null) {
            throw new NotFoundException("Venue introuvable");
        }
        return venue;
    }

    private Timeslot requireTimeslot(EntityManager em, Long timeslotId) {
        if (timeslotId == null) {
            throw new BadRequestException("timeslotId obligatoire");
        }
        Timeslot timeslot = em.find(Timeslot.class, timeslotId);
        if (timeslot == null) {
            throw new NotFoundException("Timeslot introuvable");
        }
        return timeslot;
    }

    private void validateReservationInputs(
            Event event,
            Venue venue,
            Timeslot timeslot,
            Date reservationDate,
            Long excludeReservationId,
            EntityManager em
    ) {
        if (reservationDate == null) {
            throw new BadRequestException("reservationDate obligatoire");
        }
        if (venue.getIsAvailable() != null && !venue.getIsAvailable()) {
            throw new WebApplicationException("Venue indisponible", 409);
        }
        if (timeslot.isReserved()) {
            throw new WebApplicationException("Timeslot deja reserve", 409);
        }
        if (timeslot.getVenue() == null
                || timeslot.getVenue().getId() == null
                || !timeslot.getVenue().getId().equals(venue.getId())) {
            throw new BadRequestException("Timeslot ne correspond pas a la venue");
        }
        Date start = timeslot.getStart();
        Date end = timeslot.getEndTime();
        if (start == null || end == null || !start.before(end)) {
            throw new BadRequestException("Timeslot invalide");
        }

        long overlapCount = countOverlaps(em, venue.getId(), start, end, excludeReservationId);
        if (overlapCount > 0) {
            throw new WebApplicationException("Conflit horaire", 409);
        }

        long eventCount = countReservationsForEvent(em, event.getId(), excludeReservationId);
        if (event.getCapacity() != null && eventCount >= event.getCapacity()) {
            throw new WebApplicationException("Capacite event atteinte", 409);
        }

        long venueSlotCount = countReservationsForVenueTimeslot(em, venue.getId(),
                timeslot.getId(), excludeReservationId);
        if (venue.getCapacity() != null && venueSlotCount >= venue.getCapacity()) {
            throw new WebApplicationException("Capacite venue atteinte", 409);
        }
    }

    private long countOverlaps(EntityManager em, Long venueId, Date start, Date end, Long excludeId) {
        String jpql = "SELECT COUNT(r) FROM Reservation r "
                + "WHERE r.venue.id = :venueId "
                + "AND r.timeslot.start < :end "
                + "AND r.timeslot.endTime > :start";
        if (excludeId != null) {
            jpql += " AND r.id <> :excludeId";
        }
        var query = em.createQuery(jpql, Long.class)
                .setParameter("venueId", venueId)
                .setParameter("start", start)
                .setParameter("end", end);
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }
        return query.getSingleResult();
    }

    private long countReservationsForEvent(
            EntityManager em,
            Long eventId,
            Long excludeId
    ) {
        String jpql = "SELECT COUNT(r) FROM Reservation r WHERE r.event.id = :eventId";
        if (excludeId != null) {
            jpql += " AND r.id <> :excludeId";
        }
        var query = em.createQuery(jpql, Long.class).setParameter("eventId", eventId);
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }
        return query.getSingleResult();
    }

    private long countReservationsForVenueTimeslot(
            EntityManager em,
            Long venueId,
            Long timeslotId,
            Long excludeId
    ) {
        String jpql = "SELECT COUNT(r) FROM Reservation r "
                + "WHERE r.venue.id = :venueId AND r.timeslot.id = :timeslotId";
        if (excludeId != null) {
            jpql += " AND r.id <> :excludeId";
        }
        var query = em.createQuery(jpql, Long.class)
                .setParameter("venueId", venueId)
                .setParameter("timeslotId", timeslotId);
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }
        return query.getSingleResult();
    }

    private void updateTimeslotReservedFlag(EntityManager em, Timeslot timeslot) {
        if (timeslot == null || timeslot.getId() == null) {
            return;
        }
        Venue venue = timeslot.getVenue();
        if (venue == null || venue.getId() == null || venue.getCapacity() == null) {
            return;
        }
        long count = countReservationsForVenueTimeslot(em, venue.getId(), timeslot.getId(), null);
        boolean full = count >= venue.getCapacity();
        timeslot.setReserved(full);
    }
}
