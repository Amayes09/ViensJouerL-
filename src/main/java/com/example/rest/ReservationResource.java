package com.example.rest;

import com.example.domain.Reservation;
import com.example.service.ReservationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    private ReservationService reservationService;

    // DTO simple pour la requête de création
    public static class ReservationRequest {
        public Long userId;
        public Long venueId;
        public Date date;
    }

    @POST
    public Response createReservation(ReservationRequest request) {
        try {
            Reservation res = reservationService.createReservation(
                request.userId, 
                request.venueId, 
                request.date
            );
            return Response.status(Response.Status.CREATED).entity(res).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(e.getMessage()).build();
        }
    }

    @GET
    public Response getAllReservations() {
        return Response.ok(reservationService.findAll()).build();
    }
}