package com.example.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.inject.Inject;
import com.example.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ✅ Consumer JMS "officiel" (Message-Driven Bean)
 * Réagit aux messages "création utilisateur" et crée une Notification en base
 * Pattern: Message Translator + Content Enricher
 */
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/UserCreatedQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
        }
)
public class UserCreatedListener implements MessageListener {

    @Inject
    private NotificationService notificationService;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Traitement métier: Parse le message UserCreated et crée une Notification
     * Pattern: Content Enricher (recharge User via ID avant création notification)
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String payload = ((TextMessage) message).getText();
                System.out.println("📥 [MDB] Message reçu: " + payload);

                // Traitement libre: parse + enrichissement + persistance
                handleUserCreatedEvent(payload);
            }
        } catch (Exception e) {
            System.err.println("❌ [MDB] Erreur traitement message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ Traitement métier complet :
     * 1. Parse JSON (id, email, timestamp)
     * 2. Recharge User en base (Content Enricher)
     * 3. Crée Notification persistée (JPA)
     */
    private void handleUserCreatedEvent(String payload) {
        try {
            JsonNode json = mapper.readTree(payload);

            Long userId = json.hasNonNull("id") ? json.get("id").asLong() : null;
            String email = json.hasNonNull("email") ? json.get("email").asText() : "unknown";
            String timestamp = json.hasNonNull("timestamp") ? json.get("timestamp").asText() : "N/A";

            if (userId == null) {
                System.out.println("⚠️ [MDB] Message invalide (id manquant): " + payload);
                // Pattern: Invalid Message Channel (log dédié)
                logInvalidMessage(payload, "missing_user_id");
                return;
            }

            System.out.println("✅ [MDB] UserCreated parsé: id=" + userId + " email=" + email + " ts=" + timestamp);

            // Content Enricher: recharge User et crée Notification
            notificationService.createNotification(
                    userId,
                    "👤 Compte créé: " + email + " [" + timestamp + "]"
            );

            System.out.println("📩 [MDB] Notification créée avec succès pour userId=" + userId);

        } catch (Exception e) {
            System.out.println("❌ [MDB] Erreur parsing JSON: " + e.getMessage());
            logInvalidMessage(payload, "parse_error");
        }
    }

    /**
     * Pattern: Invalid Message Channel
     * Logs les messages invalides de manière structurée
     */
    private void logInvalidMessage(String payload, String reason) {
        System.out.println("📋 [INVALID_MESSAGE_CHANNEL] reason=" + reason + " payload=" + payload);
        // Pourrait être enrichi: persister en table audit, envoyer alerte, etc.
    }
}