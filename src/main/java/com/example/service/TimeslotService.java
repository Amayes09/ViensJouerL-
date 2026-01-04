package com.example.service;

import java.util.Date;
import java.util.List;

import com.example.domain.Timeslot;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class TimeslotService {

    @PersistenceContext
    EntityManager em;

    public Timeslot createTimeslot(Date start, Date end) {
        Timeslot timeslot = new Timeslot(start, end);
        em.persist(timeslot);
        return timeslot;
    }

    public Timeslot findTimeslot(Long id) {
        return em.find(Timeslot.class, id);
    }

    public List<Timeslot> getAllTimeslots() {
        Query q = em.createQuery("SELECT t FROM Timeslot t");
        return q.getResultList();
    }

    public List<Timeslot> getAvailableTimeslots() {
        Query q = em.createQuery("SELECT t FROM Timeslot t WHERE t.isReserved = false");
        return q.getResultList();
    }

    public List<Timeslot> getReservedTimeslots() {
        Query q = em.createQuery("SELECT t FROM Timeslot t WHERE t.isReserved = true");
        return q.getResultList();
    }

    public Timeslot updateTimeslot(Long id, Date start, Date end) {
        Timeslot timeslot = em.find(Timeslot.class, id);
        if (timeslot != null) {
            timeslot.setStart(start);
            timeslot.setEnd(end);
            em.merge(timeslot);
        }
        return timeslot;
    }

    public void deleteTimeslot(Long id) {
        Timeslot timeslot = em.find(Timeslot.class, id);
        if (timeslot != null) {
            em.remove(timeslot);
        }
    }

    public void reserveTimeslot(Long id) {
        Timeslot timeslot = em.find(Timeslot.class, id);
        if (timeslot != null && !timeslot.isReserved()) {
            timeslot.setReserved(true);
            em.merge(timeslot);
        }
    }

    public void releaseTimeslot(Long id) {
        Timeslot timeslot = em.find(Timeslot.class, id);
        if (timeslot != null) {
            timeslot.setReserved(false);
            em.merge(timeslot);
        }
    }
}
