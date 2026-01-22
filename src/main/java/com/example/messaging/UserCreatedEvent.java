package com.example.messaging;

public class UserCreatedEvent {
    public Long id;
    public String name;
    public String email;
    public String timestamp;
    public String source;

    public UserCreatedEvent() {
    }

    public UserCreatedEvent(Long id, String name, String email, String timestamp, String source) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.timestamp = timestamp;
        this.source = source;
    }
}
