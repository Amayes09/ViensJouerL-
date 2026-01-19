package com.example.rest;

import com.example.domain.Timeslot;
import com.example.service.TimeslotService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/timeslots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeslotResource {

    @Inject
    private TimeslotService timeslotService;

    @POST
    public Response create(Timeslot timeslot) {
        try {
            if (timeslot == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Timeslot ne peut pas être null"))
                        .build();
            }
            Timeslot created = timeslotService.create(timeslot);
            return Response.status(Response.Status.CREATED).entity(created).build();
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
            Timeslot timeslot = timeslotService.findById(id);
            if (timeslot == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(timeslot).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<Timeslot> timeslots = timeslotService.findAll();
            return Response.ok(timeslots).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Timeslot timeslot) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        if (timeslot == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Timeslot ne peut pas être null"))
                    .build();
        }
        try {
            Timeslot updated = timeslotService.update(id, timeslot);
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
            boolean deleted = timeslotService.delete(id);
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
