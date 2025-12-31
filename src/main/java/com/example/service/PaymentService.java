package com.example.service;

import com.example.domain.Payment;
import com.example.domain.Reservation;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class PaymentService {

    @PersistenceContext
    private EntityManager em;

    public Payment processPayment(Long reservationId, BigDecimal amount, String method) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Réservation introuvable");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(method);
        
        // Simulation du traitement métier
        payment.processPayment(); 

        em.persist(payment);
        return payment;
    }
}