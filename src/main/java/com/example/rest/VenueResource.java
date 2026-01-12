package com.example.rest;

import com.example.domain.Venue;
import com.example.service.VenueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {

    @Inject
    private VenueService venueService;

    @POST
    public Response create(Venue venue) {
        venueService.create(venue);
        return Response.status(Response.Status.CREATED).entity(venue).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Venue venue = venueService.findById(id);
        if (venue == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(venue).build();
    }

    @GET
    public List<Venue> findAll() {
        return venueService.findAll();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Venue venue) {
        Venue updated = venueService.update(id, venue);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = venueService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
