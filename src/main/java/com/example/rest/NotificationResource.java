package com.example.rest;

import com.example.domain.Notification;
import com.example.domain.User;
import com.example.service.NotificationService;
import com.example.service.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserService userService;

    public static class NotificationRequest {
        public Long userId;
        public String message;
    }

    @POST
    public Response create(NotificationRequest request) {
        User user = userService.findById(request.userId);
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User introuvable").build();
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(request.message);
        notification.setCreatedAt(Instant.now());

        notificationService.create(notification);

        return Response.status(Response.Status.CREATED).entity(notification).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Notification n = notificationService.findById(id);
        if (n == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(n).build();
    }

    @GET
    public List<Notification> findAll() {
        return notificationService.findAll();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, NotificationRequest request) {
        User user = userService.findById(request.userId);
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User introuvable").build();
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(request.message);

        Notification updated = notificationService.update(id, notification);
        if (updated == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = notificationService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
