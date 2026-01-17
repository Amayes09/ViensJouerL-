package com.example.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String method; // Carte, PayPal
    private Boolean isConfirmed = false;

    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonIgnore
    private Reservation reservation;

    public void processPayment() {
        // Simuler la validation
        this.isConfirmed = true;
    }

    // Getters et Setters standard...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Boolean getIsConfirmed() { return isConfirmed; }
    public void setIsConfirmed(Boolean confirmed) { isConfirmed = confirmed; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}