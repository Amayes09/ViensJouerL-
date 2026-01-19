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
import java.util.Map;

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
        try {
            if (request == null || request.userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "userId obligatoire"))
                        .build();
            }
            if (request.message == null || request.message.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "message obligatoire"))
                        .build();
            }

            User user = userService.findById(request.userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "User introuvable"))
                        .build();
            }

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(request.message);
            notification.setCreatedAt(Instant.now());

            notificationService.create(notification);
            return Response.status(Response.Status.CREATED).entity(notification).build();
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
            Notification n = notificationService.findById(id);
            if (n == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(n).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<Notification> notifications = notificationService.findAll();
            return Response.ok(notifications).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, NotificationRequest request) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        try {
            User user = userService.findById(request.userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "User introuvable"))
                        .build();
            }

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(request.message);

            Notification updated = notificationService.update(id, notification);
            if (updated == null) return Response.status(Response.Status.NOT_FOUND).build();
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
            boolean deleted = notificationService.delete(id);
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
