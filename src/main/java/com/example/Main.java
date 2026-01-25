package com.example;

import com.example.messaging.JmsUserCreatedConsumer;
import com.example.messaging.UserCreatedProducer;
import com.example.service.*;
import com.example.util.DataSeeder;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.lang.reflect.Field;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/";

    public static void main(String[] args) throws Exception {
        System.out.println("=================================");
        System.out.println("DÉMARRAGE DE L'APPLICATION");
        System.out.println("=================================");

        // 1. Initialisation de l'infrastructure (DB + Messaging)
        System.out.println("--- 1. Connexion Base de Données (PostgreSQL) ---");
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("starterPU");

        System.out.println("--- 2. Connexion Messaging (ActiveMQ Artemis) ---");
        ActiveMQConnectionFactory jmsFactory = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
        Queue userQueue = new ActiveMQQueue("UserCreatedQueue");

        // 2. Préparation des Services pour le mode Standalone
        // NotificationService est instancié tôt car utilisé par le Consumer ET le
        // Seeder
        NotificationService notificationService = new NotificationService();
        notificationService.setEmf(emf); // Setter public disponible

        // 3. Démarrage du Consommateur JMS 
        System.out.println("--- 3. Démarrage du Consommateur JMS ---");
        JmsUserCreatedConsumer consumer = new JmsUserCreatedConsumer(jmsFactory, userQueue, notificationService);
        consumer.start();

        // 4. DATA SEEDING
        System.out.println("--- 4. Exécution du DataSeeder ---");
        try {
            // a) Instanciation des services nécessaires
            UserService userService = new UserService();
            VenueService venueService = new VenueService();
            EventService eventService = new EventService();
            TimeslotService timeslotService = new TimeslotService();
            ReservationService reservationService = new ReservationService();
            PaymentService paymentService = new PaymentService();
            UserCreatedProducer producer = new UserCreatedProducer();

            // b) Injection des dépendances techniques
            // Nécessaire car @Inject ne fonctionne pas hors du conteneur Jersey
            injectDependency(userService, "emf", emf);
            injectDependency(venueService, "emf", emf);
            injectDependency(eventService, "emf", emf);
            injectDependency(timeslotService, "emf", emf);
            injectDependency(reservationService, "emf", emf);
            injectDependency(paymentService, "emf", emf);

            // c) Injection spécifique pour le Messaging (Producer)
            injectDependency(producer, "factory", jmsFactory);
            injectDependency(producer, "queue", userQueue);
            userService.setProducer(producer);

            // d) Lancement du Seeder
            DataSeeder seeder = new DataSeeder(
                    userService,
                    venueService,
                    eventService,
                    timeslotService,
                    reservationService,
                    paymentService,
                    notificationService);
            seeder.seed();

        } catch (Exception e) {
            System.err.println("Le DataSeeding a rencontré une erreur (mais le serveur va continuer).");
            System.err.println("Détail : " + e.getMessage());
            e.printStackTrace();
        }

        // 5. Configuration et Démarrage du Serveur REST
        System.out.println("--- 5. Démarrage du Serveur HTTP (Grizzly) ---");
        final ResourceConfig rc = new ResourceConfig().packages("com.example");

        // Enregistrement des dépendances pour l'injection (@Inject) dans les classes
        // REST
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(emf).to(EntityManagerFactory.class);
                bind(jmsFactory).to(ConnectionFactory.class);
                bind(userQueue).to(Queue.class);

                // Services & Components
                bindAsContract(UserCreatedProducer.class);
                bindAsContract(UserService.class);
                bindAsContract(EventService.class);
                bindAsContract(VenueService.class);
                bindAsContract(ReservationService.class);
                bindAsContract(PaymentService.class);
                bindAsContract(TimeslotService.class);

                // Instance partagée
                bind(notificationService).to(NotificationService.class);
            }
        });

        final var server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        System.out.println("=================================");
        System.out.println("APPLICATION PRÊTE SUR : " + BASE_URI);
        System.out.println("=================================");

        // Hook d'arrêt
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Arrêt en cours");
            try {
                server.shutdownNow();
            } catch (Exception ignored) {
            }
            try {
                consumer.stop();
            } catch (Exception ignored) {
            }
            try {
                emf.close();
            } catch (Exception ignored) {
            }
            try {
                jmsFactory.close();
            } catch (Exception ignored) {
            }
            System.out.println("Bye !");
        }));

        // Bloque le thread principal jusqu'à l'appui sur Entrée
        System.in.read();

        // Nettoyage final
        server.shutdownNow();
        consumer.stop();
        emf.close();
        jmsFactory.close();
    }

    /**
     * Utilitaire pour injecter des dépendances privées (@Inject) dans des objets
     * instanciés manuellement avec 'new', via la réflexion Java.
     */
    private static void injectDependency(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            System.err.println("Champ '" + fieldName + "' introuvable dans " + target.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            System.err
                    .println("Accès refusé au champ '" + fieldName + "' dans " + target.getClass().getSimpleName());
        } catch (Exception e) {
            System.err
                    .println("Erreur d'injection dans " + target.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}