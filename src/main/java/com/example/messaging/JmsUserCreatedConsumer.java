package com.example.messaging;

import com.example.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
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
                System.out.println("👂 Consumer JMS actif sur 'UserCreatedQueue'");

                while (running && !Thread.currentThread().isInterrupted()) {
                    String payload = consumer.receiveBody(String.class, 1000);
                    if (payload != null) {
                        handleUserCreated(payload);
                    }
                }
            } catch (Exception e) {
                System.out.println("🛑 Arrêt du consumer JMS");
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

    /** Traitement libre: parse JSON + persist Notification */
    private void handleUserCreated(String payload) {
        try {
            JsonNode json = mapper.readTree(payload);

            // Ton producer envoie déjà {"id":...,"name":...,"email":...,"timestamp":...}
            Long userId = json.hasNonNull("id") ? json.get("id").asLong() : null;
            String email = json.hasNonNull("email") ? json.get("email").asText() : "unknown";

            if (userId == null) {
                System.out.println("⚠️ Message UserCreated invalide (id manquant): " + payload);
                return;
            }

            System.out.println("📥 [REÇU] UserCreated id=" + userId + " email=" + email);

            // ✅ Persist en base (Consumer -> JPA)
            notificationService.createNotification(userId, "Création du compte utilisateur : " + email);

            System.out.println("📩 Notification créée pour userId=" + userId);

        } catch (Exception e) {
            System.out.println("❌ Erreur traitement message JMS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
