package com.example.messaging;

import com.example.domain.User;
import java.time.Instant;

public class UserCreatedMessageTranslator {

    public String toJson(User user, Instant timestamp, String source) {
        String id = user.getId() == null ? "null" : String.valueOf(user.getId());
        String name = escapeJson(user.getName());
        String email = escapeJson(user.getEmail());
        String time = timestamp == null ? "" : timestamp.toString();
        String src = escapeJson(source);

        return "{"
                + "\"id\":" + id + ","
                + "\"name\":\"" + name + "\","
                + "\"email\":\"" + email + "\","
                + "\"timestamp\":\"" + time + "\","
                + "\"source\":\"" + src + "\""
                + "}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
