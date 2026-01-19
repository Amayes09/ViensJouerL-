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
import java.util.Map;

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

    public static class ReservationRequest {
        public Long userId;
        public Long eventId;
        public Long venueId;
        public Date reservationDate;
    }

    @POST
    public Response create(ReservationRequest request) {
        try {
            if (request == null || request.userId == null || request.eventId == null || request.venueId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "userId, eventId et venueId obligatoires"))
                        .build();
            }

            User user = userService.findById(request.userId);
            Event event = eventService.findById(request.eventId);
            Venue venue = venueService.findById(request.venueId);

            if (user == null || event == null || venue == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "User, Event ou Venue introuvable"))
                        .build();
            }

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setEvent(event);
            reservation.setVenue(venue);
            reservation.setReservationDate(request.reservationDate);

            reservationService.create(reservation);
            return Response.status(Response.Status.CREATED).entity(reservation).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        try {
            Reservation r = reservationService.findById(id);
            if (r == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(r).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<Reservation> reservations = reservationService.findAll();
            return Response.ok(reservations).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ReservationRequest request) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        try {
            User user = userService.findById(request.userId);
            Event event = eventService.findById(request.eventId);
            Venue venue = venueService.findById(request.venueId);

            if (user == null || event == null || venue == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "User, Event ou Venue introuvable"))
                        .build();
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
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        try {
            boolean deleted = reservationService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
