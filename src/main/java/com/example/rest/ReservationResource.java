package com.example.rest;

import java.util.List;

import com.example.domain.Event;
import com.example.domain.Reservation;
import com.example.domain.User;
import com.example.domain.Venue;
import com.example.service.EventService;
import com.example.service.ReservationService;
import com.example.service.UserService;
import com.example.service.VenueService;

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

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    private ReservationService reservationService;

    @Inject
    private UserService userService;

    @Inject
    private EventService eventService;

    @Inject
    private VenueService venueService;

    @POST
    public Reservation createReservation(
        @QueryParam("userId") Long userId,
        @QueryParam("eventId") Long eventId,
        @QueryParam("venueId") Long venueId
    ) {
        User user = userService.findUser(userId);
        Event event = eventService.findEvent(eventId);
        Venue venue = venueService.findVenue(venueId);

        if (user != null && event != null && venue != null) {
            return reservationService.createReservation(user, event, venue);
        }
        return null;
    }

    @GET
    @Path("/{id}")
    public Reservation getReservationById(@PathParam("id") Long id) {
        return reservationService.findReservation(id);
    }

    @GET
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GET
    @Path("/user/{userId}")
    public List<Reservation> getReservationsByUser(@PathParam("userId") Long userId) {
        return reservationService.findReservationsByUser(userId);
    }

    @GET
    @Path("/event/{eventId}")
    public List<Reservation> getReservationsByEvent(@PathParam("eventId") Long eventId) {
        return reservationService.findReservationsByEvent(eventId);
    }

    @GET
    @Path("/venue/{venueId}")
    public List<Reservation> getReservationsByVenue(@PathParam("venueId") Long venueId) {
        return reservationService.findReservationsByVenue(venueId);
    }

    @PUT
    @Path("/{id}")
    public Reservation updateReservation(
        @PathParam("id") Long id,
        @QueryParam("userId") Long userId,
        @QueryParam("eventId") Long eventId,
        @QueryParam("venueId") Long venueId
    ) {
        User user = userService.findUser(userId);
        Event event = eventService.findEvent(eventId);
        Venue venue = venueService.findVenue(venueId);

        if (user != null && event != null && venue != null) {
            return reservationService.updateReservation(id, user, event, venue);
        }
        return null;
    }

    @DELETE
    @Path("/{id}")
    public void deleteReservation(@PathParam("id") Long id) {
        reservationService.deleteReservation(id);
    }

    @DELETE
    @Path("/{id}/cancel")
    public void cancelReservation(@PathParam("id") Long id) {
        reservationService.cancelReservation(id);
    }
}
