package com.example.rest;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

    @Override
    public Response toResponse(PersistenceException ex) {

        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        String rootMsg = root.getMessage() != null ? root.getMessage().toLowerCase() : "";
        String exMsg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        boolean duplicate =
                rootMsg.contains("duplicate key") ||
                        rootMsg.contains("already exists") ||
                        rootMsg.contains("unique constraint") ||
                        exMsg.contains("duplicate key") ||
                        exMsg.contains("already exists") ||
                        exMsg.contains("unique constraint");

        boolean mentionsEmail = rootMsg.contains("email") || exMsg.contains("email");

        if (duplicate && mentionsEmail) {
            return Response.status(Response.Status.CONFLICT) // 409
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of(
                            "error", "EMAIL_ALREADY_EXISTS",
                            "message", "Un utilisateur avec cet email existe déjà."
                    ))
                    .build();
        }

        // Sinon: erreur DB générique
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "error", "PERSISTENCE_ERROR",
                        "message", "Erreur lors de l'accès à la base de données."
                ))
                .build();
    }
}
