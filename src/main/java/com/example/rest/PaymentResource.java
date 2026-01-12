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
        Reservation reservation = reservationService.findById(request.reservationId);
        if (reservation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reservation introuvable").build();
        }

        Payment payment = new Payment();
        payment.setAmount(request.amount);
        payment.setMethod(request.method);
        payment.setReservation(reservation);
        payment.processPayment(); // valide le paiement

        paymentService.create(payment);

        return Response.status(Response.Status.CREATED).entity(payment).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Payment p = paymentService.findById(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(p).build();
    }

    @GET
    public List<Payment> findAll() {
        return paymentService.findAll();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, PaymentRequest request) {
        Reservation reservation = reservationService.findById(request.reservationId);
        if (reservation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reservation introuvable").build();
        }

        Payment payment = new Payment();
        payment.setAmount(request.amount);
        payment.setMethod(request.method);
        payment.setReservation(reservation);
        payment.processPayment();

        Payment updated = paymentService.update(id, payment);
        if (updated == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = paymentService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
