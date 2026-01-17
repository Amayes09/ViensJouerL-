package com.example;

import com.example.messaging.JmsUserCreatedConsumer;
import com.example.messaging.UserCreatedProducer;
import com.example.service.*;
import com.example.service.NotificationService;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/";

    public static void main(String[] args) throws Exception {
        // 1) BDD
        System.out.println("⏳ Connexion BDD...");
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        // 2) JMS Artemis
        System.out.println("⏳ Connexion Artemis...");
        ActiveMQConnectionFactory jmsFactory =
                new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
        Queue userQueue = new ActiveMQQueue("UserCreatedQueue");

        // 3) Démarrage du consommateur JMS (INDISCUTABLE)
        // 3) Démarrage du consommateur JMS avec traitement libre (Consumer -> JPA)
        NotificationService notificationService = new NotificationService();
        notificationService.setEmf(emf); // ✅ indispensable en standalone

        JmsUserCreatedConsumer consumer = new JmsUserCreatedConsumer(jmsFactory, userQueue, notificationService);
        consumer.start();


        // 4) Jersey / Grizzly
        final ResourceConfig rc = new ResourceConfig().packages("com.example");

        // Injection HK2 pour tes Services + Producer (comme avant)
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                // Base de données
                bind(emf).to(EntityManagerFactory.class);

                // JMS (Artemis)
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
                bind(notificationService).to(NotificationService.class);
            }
        });

        final var server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        System.out.println("🚀 Serveur lancé sur " + BASE_URI);

        // Arrêt propre même si tu fais Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Arrêt propre...");
            try { server.shutdownNow(); } catch (Exception ignored) {}
            try { consumer.stop(); } catch (Exception ignored) {}
            try { emf.close(); } catch (Exception ignored) {}
            try { jmsFactory.close(); } catch (Exception ignored) {}
        }));

        // Bloque jusqu'à entrée clavier
        System.in.read();

        // Arrêt propre si tu appuies sur Entrée
        server.shutdownNow();
        consumer.stop();
        emf.close();
        jmsFactory.close();
    }
}
