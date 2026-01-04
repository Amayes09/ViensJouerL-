package com.example.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.domain.Payment;
import com.example.domain.Reservation;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Stateless
public class PaymentService {

    @PersistenceContext
    EntityManager em;

    public Payment createPayment(Reservation reservation, BigDecimal amount, String method) {
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(method);
        em.persist(payment);
        return payment;
    }

    public Payment findPayment(Long id) {
        return em.find(Payment.class, id);
    }

    public List<Payment> getAllPayments() {
        Query q = em.createQuery("SELECT p FROM Payment p");
        return q.getResultList();
    }

    public List<Payment> findPaymentsByReservation(Long reservationId) {
        Query q = em.createQuery("SELECT p FROM Payment p WHERE p.reservation.id = :reservationId");
        q.setParameter("reservationId", reservationId);
        return q.getResultList();
    }

    public List<Payment> findPaymentsByMethod(String method) {
        Query q = em.createQuery("SELECT p FROM Payment p WHERE p.method = :method");
        q.setParameter("method", method);
        return q.getResultList();
    }

    public Payment updatePayment(Long id, BigDecimal amount, String method) {
        Payment payment = em.find(Payment.class, id);
        if (payment != null) {
            payment.setAmount(amount);
            payment.setMethod(method);
            em.merge(payment);
        }
        return payment;
    }

    public void deletePayment(Long id) {
        Payment payment = em.find(Payment.class, id);
        if (payment != null) {
            em.remove(payment);
        }
    }

    public boolean validatePayment(Long paymentId) {
        Payment payment = em.find(Payment.class, paymentId);
        return payment != null && payment.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal calculateTotalPayments() {
        Query q = em.createQuery("SELECT SUM(p.amount) FROM Payment p");
        BigDecimal total = (BigDecimal) q.getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
}
