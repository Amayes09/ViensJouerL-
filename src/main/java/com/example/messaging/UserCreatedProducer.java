package com.example.messaging;

import com.example.domain.User;
import jakarta.inject.Inject;
import jakarta.jms.*;

// On enlève @Stateless qui ne sert à rien ici
public class UserCreatedProducer {

    // On utilise @Inject car on va configurer manuellement ces objets dans le Main
    @Inject
    private ConnectionFactory factory;

    @Inject
    private Queue queue;

    public void sendUserCreatedEvent(User user) {
        // On crée un contexte (une connexion) temporaire pour envoyer le message
        try (JMSContext context = factory.createContext()) {
            String payload = "UserCreated:" + user.getId() + ":" + user.getName();

            // Envoi du message dans la queue
            context.createProducer().send(queue, payload);

            System.out.println("📤 [JMS] Message envoyé à Artemis : " + payload);
        } catch (JMSRuntimeException e) {
            System.err.println("❌ Erreur d'envoi JMS : " + e.getMessage());
            e.printStackTrace();
        }
    }
}