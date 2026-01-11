package com.example.rest;

import com.example.domain.Payment;
import com.example.service.PaymentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentService paymentService;

    // DTO interne pour la requête de paiement
    public static class PaymentRequest {
        public Long reservationId;
        public BigDecimal amount;
        public String method;
    }

    @POST
    public Response makePayment(PaymentRequest req) {
        try {
            Payment p = paymentService.processPayment(req.reservationId, req.amount, req.method);
            return Response.ok(p).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<Payment> getAllPayments() {
        return paymentService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getPayment(@PathParam("id") Long id) {
        Payment p = paymentService.findById(id);
        if (p != null)
            return Response.ok(p).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}