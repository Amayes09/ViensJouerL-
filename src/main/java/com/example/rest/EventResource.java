package com.example.rest;

import java.util.List;

import com.example.domain.Event;
import com.example.service.EventService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventService eventService;

    @POST
    public Event createEvent(Event event) {
        return eventService.createEvent(event.getTitle(), event.getDescription());
    }

    @GET
    @Path("/{id}")
    public Event getEventById(@PathParam("id") Long id) {
        return eventService.findEvent(id);
    }

    @GET
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PUT
    @Path("/{id}")
    public Event updateEvent(@PathParam("id") Long id, Event event) {
        return eventService.updateEvent(id, event.getTitle(), event.getDescription());
    }

    @DELETE
    @Path("/{id}")
    public void deleteEvent(@PathParam("id") Long id) {
        eventService.deleteEvent(id);
    }

    @GET
    @Path("/search")
    public List<Event> searchEventsByTitle(@QueryParam("title") String title) {
        return eventService.findEventsByTitle(title);
    }
}
