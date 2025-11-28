package com.example.domain;

import com.example.domain.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private Instant createdAt;

    // !!!!! jointure ici absente sur le schéma
    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.example.domain.User user;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public com.example.domain.User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
