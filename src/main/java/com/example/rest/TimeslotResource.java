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
    public Response createTimeslot(Timeslot timeslot) {
        Timeslot created = timeslotService.createTimeslot(timeslot);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public List<Timeslot> getAllTimeslots() {
        return timeslotService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getTimeslot(@PathParam("id") Long id) {
        Timeslot t = timeslotService.findById(id);
        if (t != null)
            return Response.ok(t).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}