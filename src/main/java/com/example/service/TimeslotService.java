package com.example.service;

import com.example.domain.Timeslot;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TimeslotService {

    @PersistenceContext
    private EntityManager em;

    public Timeslot createTimeslot(Timeslot timeslot) {
        em.persist(timeslot);
        return timeslot;
    }

    public Timeslot findById(Long id) {
        return em.find(Timeslot.class, id);
    }

    public List<Timeslot> findAll() {
        return em.createQuery("SELECT t FROM Timeslot t", Timeslot.class).getResultList();
    }
}
