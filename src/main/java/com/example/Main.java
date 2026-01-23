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
        System.out.println("--- Connexion BDD ---");
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        System.out.println("\"--- Connexion Artemis ---");
        ActiveMQConnectionFactory jmsFactory =
                new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
        Queue userQueue = new ActiveMQQueue("UserCreatedQueue");

        NotificationService notificationService = new NotificationService();
        notificationService.setEmf(emf);

        JmsUserCreatedConsumer consumer = new JmsUserCreatedConsumer(jmsFactory, userQueue, notificationService);
        consumer.start();

        final ResourceConfig rc = new ResourceConfig().packages("com.example");

        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(emf).to(EntityManagerFactory.class);

                bind(jmsFactory).to(ConnectionFactory.class);
                bind(userQueue).to(Queue.class);
                bindAsContract(UserCreatedProducer.class);

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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Arrêt");
            try { server.shutdownNow(); } catch (Exception ignored) {}
            try { consumer.stop(); } catch (Exception ignored) {}
            try { emf.close(); } catch (Exception ignored) {}
            try { jmsFactory.close(); } catch (Exception ignored) {}
        }));
        System.in.read();

        server.shutdownNow();
        consumer.stop();
        emf.close();
        jmsFactory.close();
    }
}