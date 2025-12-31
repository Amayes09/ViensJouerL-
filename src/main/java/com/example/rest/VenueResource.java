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
    public Response createVenue(Venue venue) {
        Venue created = venueService.createVenue(venue);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public List<Venue> getAllVenues() {
        return venueService.findAll();
    }
}