package com.example.rest;

import com.example.domain.Reservation;
import com.example.service.ReservationService;

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

    // DTO pour la requête
    public static class ReservationRequest {
        public Long userId;
        public Long eventId;
        public Long venueId;
        public Long timeslotId;
        public Date reservationDate;
    }

    @POST
    public Response create(ReservationRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Requete invalide").build();
        }
        Reservation reservation = reservationService.createWithChecks(
                request.userId,
                request.eventId,
                request.venueId,
                request.timeslotId,
                request.reservationDate
        );
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
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Requete invalide").build();
        }
        Reservation updated = reservationService.updateWithChecks(
                id,
                request.userId,
                request.eventId,
                request.venueId,
                request.timeslotId,
                request.reservationDate
        );
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
