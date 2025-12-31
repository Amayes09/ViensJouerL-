package com.example.rest;

import com.example.domain.Event;
import com.example.service.EventService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventService eventService;

    @POST
    public Response createEvent(Event event) {
        Event created = eventService.createEvent(event);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public List<Event> getAllEvents() {
        return eventService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getEvent(@PathParam("id") Long id) {
        Event e = eventService.findById(id);
        if (e != null) return Response.ok(e).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}