package com.example.rest;

import com.example.domain.Reservation;
import com.example.domain.User;
import com.example.domain.Event;
import com.example.domain.Venue;
import com.example.service.ReservationService;
import com.example.service.UserService;
import com.example.service.EventService;
import com.example.service.VenueService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    private ReservationService reservationService;

    // On injecte les autres services pour retrouver les liens
    @Inject
    private UserService userService;
    @Inject
    private EventService eventService;
    @Inject
    private VenueService venueService;

    // DTO pour la requête
    public static class ReservationRequest {
        public Long userId;
        public Long eventId;
        public Long venueId;
        public Date reservationDate;
    }

    @POST
    public Response create(ReservationRequest request) {
        // 1. Récupération des entités liées
        User user = userService.findById(request.userId);
        Event event = eventService.findById(request.eventId);
        Venue venue = venueService.findById(request.venueId);

        if (user == null || event == null || venue == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User, Event ou Venue introuvable").build();
        }

        // 2. Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setVenue(venue);
        reservation.setReservationDate(request.reservationDate);

        // 3. Sauvegarde
        reservationService.create(reservation);

        return Response.status(Response.Status.CREATED).entity(reservation).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Reservation r = reservationService.findById(id);
        if (r == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(r).build();
    }

    @GET
    public List<Reservation> findAll() {
        return reservationService.findAll();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ReservationRequest request) {
        User user = userService.findById(request.userId);
        Event event = eventService.findById(request.eventId);
        Venue venue = venueService.findById(request.venueId);

        if (user == null || event == null || venue == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User, Event ou Venue introuvable").build();
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setVenue(venue);
        reservation.setReservationDate(request.reservationDate);

        Reservation updated = reservationService.update(id, reservation);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = reservationService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
