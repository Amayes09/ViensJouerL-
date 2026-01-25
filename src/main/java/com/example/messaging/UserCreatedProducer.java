package com.example.messaging;

import com.example.domain.User;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.time.Instant;

public class UserCreatedProducer {

    @Inject
    private ConnectionFactory factory;

    @Inject
    private Queue queue;

    private final UserCreatedMessageTranslator translator = new UserCreatedMessageTranslator();

    public void sendUserCreatedEvent(User user) {

        if (user == null || user.getId() == null || isBlank(user.getEmail())) {
            System.out.println("[JMS] Message ignored: invalid user payload.");
            return;
        }

        try (JMSContext context = factory.createContext()) {
            Instant timestamp = Instant.now();
            String payload = translator.toJson(user, timestamp, "user-service");
            context.createProducer().send(queue, payload);
            System.out.println("[JMS] Message sent to Artemis: " + payload);
        } catch (JMSRuntimeException e) {
            System.err.println("[JMS] Send error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
