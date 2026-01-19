package com.example.rest;

import com.example.domain.Venue;
import com.example.service.VenueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {

    @Inject
    private VenueService venueService;

    @POST
    public Response create(Venue venue) {
        try {
            if (venue == null || venue.getName() == null || venue.getName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Venue name est obligatoire"))
                        .build();
            }
            venueService.create(venue);
            return Response.status(Response.Status.CREATED).entity(venue).build();
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
            Venue venue = venueService.findById(id);
            if (venue == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(venue).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<Venue> venues = venueService.findAll();
            return Response.ok(venues).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Venue venue) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        if (venue == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Venue ne peut pas être null"))
                    .build();
        }
        try {
            Venue updated = venueService.update(id, venue);
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
            boolean deleted = venueService.delete(id);
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
