package com.example.util;

import com.example.domain.*;
import com.example.service.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class DataSeeder {

    private final UserService userService;
    private final VenueService venueService;
    private final EventService eventService;
    private final TimeslotService timeslotService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public DataSeeder(UserService userService, VenueService venueService,
            EventService eventService, TimeslotService timeslotService,
            ReservationService reservationService, PaymentService paymentService,
            NotificationService notificationService) {
        this.userService = userService;
        this.venueService = venueService;
        this.eventService = eventService;
        this.timeslotService = timeslotService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    public void seed() {
        System.out.println("🌱 Initialisation des données de test...");

        // --- 1. UTILISATEURS ---
        User admin = new User("Admin", "admin@viensjouer.com", "admin123");
        admin.setIsAdmin(true);
        userService.register(admin);

        User player = new User("Jean Joueur", "jean@gmail.com", "password");
        // Cela déclenchera l'événement JMS "UserCreated" -> Notification automatique
        userService.register(player);

        // --- 2. LIEUX (VENUES) ---
        Venue arena = new Venue();
        arena.setName("Gaming Arena Paris");
        arena.setAddress("12 Rue de la Paix, 75000 Paris");
        arena.setCapacity(50);
        arena.setIsAvailable(true);
        venueService.create(arena);

        // --- 3. ÉVÉNEMENTS (EVENTS) ---
        Event tournoi = new Event();
        tournoi.setTitle("Tournoi League of Legends");
        tournoi.setDescription("Grand tournoi annuel 5v5");
        tournoi.setGameType("MOBA");
        tournoi.setCapacity(50);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7); // Dans 7 jours
        tournoi.setEventDate(cal.getTime());
        eventService.create(tournoi);

        // --- 4. CRÉNEAUX (TIMESLOTS) ---
        Timeslot slot1 = new Timeslot();
        slot1.setVenue(arena);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 14); // Demain 14h
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date start = cal.getTime();
        slot1.setStart(start);

        cal.add(Calendar.HOUR_OF_DAY, 2); // Fin 16h
        Date end = cal.getTime();
        slot1.setEndTime(end);

        timeslotService.create(slot1);

        // --- 5. RÉSERVATION (RESERVATION) ---
        // Jean Joueur réserve le slot1 pour le tournoi à l'Arena
        try {
            Reservation resa = reservationService.createWithChecks(
                    player.getId(),
                    tournoi.getId(),
                    arena.getId(),
                    slot1.getId(),
                    new Date() // Date de réservation = maintenant
            );
            System.out.println("✅ Réservation créée avec l'ID : " + resa.getId());

            // --- 6. PAIEMENT (PAYMENT) ---
            Payment payment = new Payment();
            payment.setAmount(new BigDecimal("25.00")); // Prix fictif
            payment.setMethod("CB");
            payment.setReservation(resa);
            payment.processPayment(); // Confirme le paiement

            paymentService.create(payment);
            System.out.println("✅ Paiement validé pour la réservation.");

        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de la création de la réservation : " + e.getMessage());
        }

        // --- 7. NOTIFICATION MANUELLE (NOTIFICATION) ---
        // En plus de la notification JMS automatique, on en ajoute une manuelle
        Notification manualNotif = new Notification();
        manualNotif.setUser(player);
        manualNotif.setMessage("Rappel : N'oubliez pas votre manette pour le tournoi !");
        notificationService.create(manualNotif);

        System.out.println("✅ Données initialisées avec succès !");
    }
}
