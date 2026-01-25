package com.example.rest;

import com.example.domain.Timeslot;
import com.example.service.TimeslotService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/timeslots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeslotResource {

    @Inject
    private TimeslotService timeslotService;

    @POST
    public Response create(Timeslot timeslot) {
        Timeslot created = timeslotService.create(timeslot);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Timeslot timeslot = timeslotService.findById(id);
        if (timeslot == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(timeslot).build();
    }

    @GET
    public List<Timeslot> findAll() {
        return timeslotService.findAll();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Timeslot timeslot) {
        Timeslot updated = timeslotService.update(id, timeslot);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = timeslotService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
