package com.example.rest;

import com.example.domain.Payment;
import com.example.domain.Reservation;
import com.example.service.PaymentService;
import com.example.service.ReservationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentService paymentService;

    @Inject
    private ReservationService reservationService;

    public static class PaymentRequest {
        public Long reservationId;
        public BigDecimal amount;
        public String method;
    }

    @POST
    public Response create(PaymentRequest request) {
        try {
            if (request == null || request.reservationId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "reservationId obligatoire"))
                        .build();
            }
            if (request.amount == null || request.amount.signum() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "amount doit être > 0"))
                        .build();
            }
            if (request.method == null || request.method.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "method obligatoire"))
                        .build();
            }

            Reservation reservation = reservationService.findById(request.reservationId);
            if (reservation == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Reservation introuvable"))
                        .build();
            }

            Payment payment = new Payment();
            payment.setAmount(request.amount);
            payment.setMethod(request.method);
            payment.setReservation(reservation);
            payment.processPayment();

            paymentService.create(payment);
            return Response.status(Response.Status.CREATED).entity(payment).build();
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
            Payment p = paymentService.findById(id);
            if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
            return Response.ok(p).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<Payment> payments = paymentService.findAll();
            return Response.ok(payments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, PaymentRequest request) {
        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "ID invalide"))
                    .build();
        }
        try {
            Reservation reservation = reservationService.findById(request.reservationId);
            if (reservation == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Reservation introuvable"))
                        .build();
            }

            Payment payment = new Payment();
            payment.setAmount(request.amount);
            payment.setMethod(request.method);
            payment.setReservation(reservation);
            payment.processPayment();

            Payment updated = paymentService.update(id, payment);
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
            boolean deleted = paymentService.delete(id);
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
