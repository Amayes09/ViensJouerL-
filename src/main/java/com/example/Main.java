package com.example;

import com.example.service.*;
import com.example.messaging.UserCreatedProducer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.jms.*; // Import JMS
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory; // Import Artemis
import org.apache.activemq.artemis.jms.client.ActiveMQQueue; // Import Queue
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/";

    public static void main(String[] args) throws Exception {
        // 1. Démarrer la Base de Données (Hibernate)
        System.out.println("⏳ Connexion BDD...");
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        // 2. Configurer Artemis (JMS)
        System.out.println("⏳ Connexion Artemis...");
        // Attention : admin/admin correspond à votre docker-compose.yml
        ActiveMQConnectionFactory jmsFactory = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
        Queue userQueue = new ActiveMQQueue("UserCreatedQueue");

        // --- BONUS : Démarrer un écouteur pour voir les messages reçus ---
        Thread listenerThread = new Thread(() -> {
            try (JMSContext context = jmsFactory.createContext()) {
                JMSConsumer consumer = context.createConsumer(userQueue);
                System.out.println("👂 Écouteur JMS prêt sur 'UserCreatedQueue'");
                while (true) {
                    String message = consumer.receiveBody(String.class);
                    System.out.println("📥 [REÇU] Artemis a transmis : " + message);
                }
            } catch (Exception e) {
                System.out.println("Arrêt de l'écouteur JMS");
            }
        });
        listenerThread.start();
        // -------------------------------------------------------------

        final ResourceConfig rc = new ResourceConfig().packages("com.example");

        // 3. Injection de dépendances (Binder)
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                // Base de données
                bind(emf).to(EntityManagerFactory.class);

                // JMS (Artemis) : On donne la factory et la queue au Producer
                bind(jmsFactory).to(ConnectionFactory.class);
                bind(userQueue).to(Queue.class);
                bindAsContract(UserCreatedProducer.class);

                // Services
                bindAsContract(UserService.class);
                bindAsContract(EventService.class);
                bindAsContract(VenueService.class);
                bindAsContract(ReservationService.class);
                bindAsContract(PaymentService.class);
                bindAsContract(TimeslotService.class);
                bindAsContract(NotificationService.class);
            }
        });

        // 4. Lancer le serveur
        final var server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        System.out.println("✅ Serveur lancé sur " + BASE_URI);
        System.in.read();

        // Arrêt propre
        server.shutdownNow();
        emf.close();
        listenerThread.interrupt();
    }
}