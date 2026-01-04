package com.example.rest;

import java.util.List;

import com.example.domain.Timeslot;
import com.example.service.TimeslotService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeslotResource {

    @Inject
    private TimeslotService timeslotService;

    @POST
    public Timeslot createTimeslot(Timeslot timeslot) {
        return timeslotService.createTimeslot(timeslot.getStart(), timeslot.getEnd());
    }

    @GET
    @Path("/{id}")
    public Timeslot getTimeslotById(@PathParam("id") Long id) {
        return timeslotService.findTimeslot(id);
    }

    @GET
    public List<Timeslot> getAllTimeslots() {
        return timeslotService.getAllTimeslots();
    }

    @GET
    @Path("/available")
    public List<Timeslot> getAvailableTimeslots() {
        return timeslotService.getAvailableTimeslots();
    }

    @GET
    @Path("/reserved")
    public List<Timeslot> getReservedTimeslots() {
        return timeslotService.getReservedTimeslots();
    }

    @PUT
    @Path("/{id}")
    public Timeslot updateTimeslot(@PathParam("id") Long id, Timeslot timeslot) {
        return timeslotService.updateTimeslot(id, timeslot.getStart(), timeslot.getEnd());
    }

    @DELETE
    @Path("/{id}")
    public void deleteTimeslot(@PathParam("id") Long id) {
        timeslotService.deleteTimeslot(id);
    }

    @POST
    @Path("/{id}/reserve")
    public void reserveTimeslot(@PathParam("id") Long id) {
        timeslotService.reserveTimeslot(id);
    }

    @POST
    @Path("/{id}/release")
    public void releaseTimeslot(@PathParam("id") Long id) {
        timeslotService.releaseTimeslot(id);
    }
}
