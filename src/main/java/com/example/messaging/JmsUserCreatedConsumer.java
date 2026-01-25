package com.example.messaging;

import com.example.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class JmsUserCreatedConsumer {

    private final ActiveMQConnectionFactory connectionFactory;
    private final Queue queue;
    private final NotificationService notificationService;

    private final ObjectMapper mapper = new ObjectMapper();

    private Thread thread;
    private volatile boolean running = false;

    public JmsUserCreatedConsumer(
            ActiveMQConnectionFactory connectionFactory,
            Queue queue,
            NotificationService notificationService
    ) {
        this.connectionFactory = connectionFactory;
        this.queue = queue;
        this.notificationService = notificationService;
    }

    public synchronized void start() {
        if (running) return;
        running = true;

        thread = new Thread(() -> {
            try (JMSContext context = connectionFactory.createContext()) {
                JMSConsumer consumer = context.createConsumer(queue);
                System.out.println("[JMS] Consumer active on 'UserCreatedQueue'");

                while (running && !Thread.currentThread().isInterrupted()) {
                    String payload = consumer.receiveBody(String.class, 1000);
                    if (payload != null) {
                        handleUserCreated(payload);
                    }
                }
            } catch (Exception e) {
                System.out.println("[JMS] Consumer stopped");
                e.printStackTrace();
            }
        }, "jms-usercreated-consumer");

        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    private void handleUserCreated(String payload) {
        try {
            UserCreatedEvent event = mapper.readValue(payload, UserCreatedEvent.class);

            Long userId = event == null ? null : event.id;
            String email = event != null && event.email != null ? event.email : "unknown";

            if (userId == null) {
                System.out.println("[JMS] Invalid UserCreated message (missing id): " + payload);
                return;
            }

            System.out.println("[JMS] Received UserCreated id=" + userId + " email=" + email);

            notificationService.createNotification(userId, "Creation du compte utilisateur : " + email);

            System.out.println("[JMS] Notification created for userId=" + userId);
        } catch (Exception e) {
            System.out.println("[JMS] Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
