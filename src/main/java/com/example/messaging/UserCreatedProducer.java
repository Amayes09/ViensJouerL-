package com.example.messaging;

import com.example.domain.User;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.time.Instant;

// On enlève @Stateless qui ne sert à rien ici
public class UserCreatedProducer {

    // On utilise @Inject car on va configurer manuellement ces objets dans le Main
    @Inject
    private ConnectionFactory factory;

    @Inject
    private Queue queue;

    private final UserCreatedMessageTranslator translator = new UserCreatedMessageTranslator();

    public void sendUserCreatedEvent(User user) {

        //MESSAGE FILTRER
        if (user == null || user.getName() == null || user.getName().trim().length() < 3) {
            System.out.println("[JMS] Message ignore: nom utilisateur trop court.");
            return;
        }
        // On crée un contexte (une connexion) temporaire pour envoyer le message
        try (JMSContext context = factory.createContext()) {
            Instant timestamp = Instant.now();
            String payload = translator.toJson(user, timestamp, "user-service");

            // Envoi du message dans la queue
            context.createProducer().send(queue, payload);

            System.out.println("📤 [JMS] Message envoyé à Artemis : " + payload);
        } catch (JMSRuntimeException e) {
            System.err.println("❌ Erreur d'envoi JMS : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
