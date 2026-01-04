package com.example.rest;

import java.util.List;

import com.example.domain.Notification;
import com.example.domain.User;
import com.example.service.NotificationService;
import com.example.service.UserService;

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

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserService userService;

    @POST
    public Notification createNotification(
        @QueryParam("userId") Long userId,
        @QueryParam("message") String message
    ) {
        User user = userService.findUser(userId);
        if (user != null) {
            return notificationService.createNotification(user, message);
        }
        return null;
    }

    @GET
    @Path("/{id}")
    public Notification getNotificationById(@PathParam("id") Long id) {
        return notificationService.findNotification(id);
    }

    @GET
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GET
    @Path("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathParam("userId") Long userId) {
        return notificationService.findNotificationsByUser(userId);
    }

    @GET
    @Path("/user/{userId}/recent")
    public List<Notification> getRecentNotifications(
        @PathParam("userId") Long userId,
        @QueryParam("days") int days
    ) {
        if (days <= 0) {
            days = 7; // Par défaut 7 jours
        }
        return notificationService.findRecentNotifications(userId, days);
    }

    @PUT
    @Path("/{id}")
    public Notification updateNotification(
        @PathParam("id") Long id,
        @QueryParam("message") String message
    ) {
        return notificationService.updateNotification(id, message);
    }

    @DELETE
    @Path("/{id}")
    public void deleteNotification(@PathParam("id") Long id) {
        notificationService.deleteNotification(id);
    }

    @DELETE
    @Path("/cleanup")
    public void deleteOldNotifications(@QueryParam("days") int days) {
        if (days <= 0) {
            days = 30; // Par défaut 30 jours
        }
        notificationService.deleteOldNotifications(days);
    }
}
