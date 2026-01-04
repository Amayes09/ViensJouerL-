package com.example.rest;

import java.util.List;

import com.example.domain.Venue;
import com.example.service.VenueService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {

    @Inject
    private VenueService venueService;

    @POST
    public Venue createVenue(Venue venue) {
        return venueService.createVenue(
            venue.getName(),
            venue.getAddress(),
            venue.getPostalCode(),
            venue.getCity()
        );
    }

    @GET
    @Path("/{id}")
    public Venue getVenueById(@PathParam("id") Long id) {
        return venueService.findVenue(id);
    }

    @GET
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    @PUT
    @Path("/{id}")
    public Venue updateVenue(@PathParam("id") Long id, Venue venue) {
        return venueService.updateVenue(
            id,
            venue.getName(),
            venue.getAddress(),
            venue.getPostalCode(),
            venue.getCity()
        );
    }

    @DELETE
    @Path("/{id}")
    public void deleteVenue(@PathParam("id") Long id) {
        venueService.deleteVenue(id);
    }

    @GET
    @Path("/city/{city}")
    public List<Venue> getVenuesByCity(@PathParam("city") String city) {
        return venueService.findVenuesByCity(city);
    }

    @GET
    @Path("/postalcode/{postalCode}")
    public List<Venue> getVenuesByPostalCode(@PathParam("postalCode") String postalCode) {
        return venueService.findVenuesByPostalCode(postalCode);
    }
}
