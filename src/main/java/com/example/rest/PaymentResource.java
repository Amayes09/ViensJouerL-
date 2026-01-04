package com.example.rest;

import java.math.BigDecimal;
import java.util.List;

import com.example.domain.Payment;
import com.example.domain.Reservation;
import com.example.service.PaymentService;
import com.example.service.ReservationService;

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
import jakarta.ws.rs.core.Response;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentService paymentService;

    @Inject
    private ReservationService reservationService;

    @POST
    public Payment createPayment(
        @QueryParam("reservationId") Long reservationId,
        @QueryParam("amount") BigDecimal amount,
        @QueryParam("method") String method
    ) {
        Reservation reservation = reservationService.findReservation(reservationId);
        if (reservation != null) {
            return paymentService.createPayment(reservation, amount, method);
        }
        return null;
    }

    @GET
    @Path("/{id}")
    public Payment getPaymentById(@PathParam("id") Long id) {
        return paymentService.findPayment(id);
    }

    @GET
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GET
    @Path("/reservation/{reservationId}")
    public List<Payment> getPaymentsByReservation(@PathParam("reservationId") Long reservationId) {
        return paymentService.findPaymentsByReservation(reservationId);
    }

    @GET
    @Path("/method/{method}")
    public List<Payment> getPaymentsByMethod(@PathParam("method") String method) {
        return paymentService.findPaymentsByMethod(method);
    }

    @PUT
    @Path("/{id}")
    public Payment updatePayment(
        @PathParam("id") Long id,
        @QueryParam("amount") BigDecimal amount,
        @QueryParam("method") String method
    ) {
        return paymentService.updatePayment(id, amount, method);
    }

    @DELETE
    @Path("/{id}")
    public void deletePayment(@PathParam("id") Long id) {
        paymentService.deletePayment(id);
    }

    @POST
    @Path("/{id}/validate")
    public Response validatePayment(@PathParam("id") Long id) {
        boolean isValid = paymentService.validatePayment(id);
        if (isValid) {
            return Response.ok("Payment is valid").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Payment is invalid")
                .build();
        }
    }

    @GET
    @Path("/total")
    public BigDecimal getTotalPayments() {
        return paymentService.calculateTotalPayments();
    }
}
