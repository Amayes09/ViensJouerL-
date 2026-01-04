package com.example.rest;

import java.util.List;

import com.example.domain.User;
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

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    @POST
    public User createUser(User user) {
        return userService.createUser(user.getName(), user.getEmail());
    }

    @GET
    @Path("/{id}")
    public User getUserById(@PathParam("id") Long id) {
        return userService.findUser(id);
    }

    @GET
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PUT
    @Path("/{id}")
    public User updateUser(@PathParam("id") Long id, User user) {
        return userService.updateUser(id, user.getName(), user.getEmail());
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
    }

    @GET
    @Path("/email/{email}")
    public User getUserByEmail(@PathParam("email") String email) {
        return userService.findUserByEmail(email);
    }
}
