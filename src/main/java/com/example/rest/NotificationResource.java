package com.example.rest;

import com.example.domain.Notification;
import com.example.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    private NotificationService notificationService;

    // DTO interne pour la requête de notification
    public static class NotificationRequest {
        public Long userId;
        public String message;
    }

    @POST
    public Response createNotification(NotificationRequest request) {
        try {
            Notification n = notificationService.createNotification(request.userId, request.message);
            return Response.status(Response.Status.CREATED).entity(n).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<Notification> getAllNotifications() {
        return notificationService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getNotification(@PathParam("id") Long id) {
        Notification n = notificationService.findById(id);
        if (n != null)
            return Response.ok(n).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}