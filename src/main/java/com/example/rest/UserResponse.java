package com.example.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ✅ DTO UserResponse (Data Transfer Object)
 * Sérialisé en réponse REST, sans exposer le password
 * Pattern: DTO pour sécuriser la sérialisation
 */
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Boolean isAdmin;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, Boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Boolean getIsAdmin() { return isAdmin; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setIsAdmin(Boolean admin) { isAdmin = admin; }

    /**
     * Conversion depuis entité User
     */
    public static UserResponse fromUser(com.example.domain.User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsAdmin()
        );
    }
}
