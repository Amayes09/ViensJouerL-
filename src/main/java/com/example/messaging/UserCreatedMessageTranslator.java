package com.example.messaging;

import com.example.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;

public class UserCreatedMessageTranslator {

    private final ObjectMapper mapper = new ObjectMapper();

    public String toJson(User user, Instant timestamp, String source) {
        String ts = timestamp == null ? null : timestamp.toString();
        UserCreatedEvent event = new UserCreatedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                ts,
                source
        );
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize UserCreatedEvent", e);
        }
    }
}
