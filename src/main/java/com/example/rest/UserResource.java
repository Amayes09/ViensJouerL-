package com.example.rest;

import com.example.domain.User;
import com.example.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;


/**
 * ✅ UserResource avec DTO UserResponse (sans password)
 * Toutes les réponses retournent UserResponse pour sécuriser la sérialisation
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    /**
     * POST /users : créer un utilisateur
     * ✅ Validation: 400 (entrée invalide), 409 (email dupliqué), 201 (succès)
     */
    @POST
    public Response createUser(User user) {
        try {
            User created = userService.register(user);
            return Response.status(Response.Status.CREATED)
                    .entity(UserResponse.fromUser(created))
                    .build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "VALIDATION_ERROR", "message", e.getMessage()))
                    .build();
        } catch (WebApplicationException e) {
            throw e;
        }
    }

    /**
     * GET /users/{id} : récupérer un utilisateur
     * ✅ Retourne UserResponse (sans password)
     */
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "USER_NOT_FOUND"))
                        .build();
            }
            return Response.ok(UserResponse.fromUser(user)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "INVALID_ID", "message", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /users : lister tous les utilisateurs
     * ✅ Retourne List<UserResponse>
     */
    @GET
    public Response getAllUsers() {
        List<User> users = userService.findAll();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return Response.ok(responses).build();
    }

    /**
     * PUT /users/{id} : mettre à jour un utilisateur
     * ✅ Validation 400, 404, 409
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            User updated = userService.update(id, user);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "USER_NOT_FOUND"))
                        .build();
            }
            return Response.ok(UserResponse.fromUser(updated)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "VALIDATION_ERROR", "message", e.getMessage()))
                    .build();
        } catch (WebApplicationException e) {
            throw e;
        }
    }

    /**
     * DELETE /users/{id} : supprimer un utilisateur
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            boolean deleted = userService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "USER_NOT_FOUND"))
                        .build();
            }
            return Response.noContent().build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "INVALID_ID", "message", e.getMessage()))
                    .build();
        }
    }
}
