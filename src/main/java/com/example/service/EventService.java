package com.example.service;

import com.example.domain.Event;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EventService {

    @PersistenceContext
    private EntityManager em;

    public Event createEvent(Event event) {
        // Validation métier possible ici (ex: date dans le futur)
        em.persist(event);
        return event;
    }

    public Event findById(Long id) {
        return em.find(Event.class, id);
    }

    public List<Event> findAll() {
        return em.createQuery("SELECT e FROM Event e", Event.class).getResultList();
    }
    
    public void deleteEvent(Long id) {
        Event e = findById(id);
        if (e != null) {
            em.remove(e);
        }
    }
}