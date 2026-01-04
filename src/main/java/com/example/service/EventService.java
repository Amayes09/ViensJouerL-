package com.example.service;

import java.util.List;

import com.example.domain.Event;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class EventService {

    @PersistenceContext
    EntityManager em;

    public Event createEvent(String title, String description) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        em.persist(event);
        return event;
    }

    public Event findEvent(Long id) {
        return em.find(Event.class, id);
    }

    public List<Event> getAllEvents() {
        Query q = em.createQuery("SELECT e FROM Event e");
        return q.getResultList();
    }

    public Event updateEvent(Long id, String title, String description) {
        Event event = em.find(Event.class, id);
        if (event != null) {
            event.setTitle(title);
            event.setDescription(description);
            em.merge(event);
        }
        return event;
    }

    public void deleteEvent(Long id) {
        Event event = em.find(Event.class, id);
        if (event != null) {
            em.remove(event);
        }
    }

    public List<Event> findEventsByTitle(String title) {
        Query q = em.createQuery("SELECT e FROM Event e WHERE e.title LIKE :title");
        q.setParameter("title", "%" + title + "%");
        return q.getResultList();
    }
}
