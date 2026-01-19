# Design Patterns - Projet ViensJouerLà

## Vue d'ensemble

Ce document décrit les **3 design patterns** implémentés dans l'application Jakarta EE, conformément aux exigences du projet.

---

## 1. Pattern : **Event Message**

### Localisation
- Fichier : `src/main/java/com/example/messaging/UserCreatedProducer.java`
- Catégorie : **Messaging Patterns - Messages**

### Description
Le pattern **Event Message** encapsule un événement métier significatif dans un message asynchrone. Ici, chaque création d'utilisateur produit un message JMS contenant les informations pertinentes.

### Implémentation
```java
public void sendUserCreatedEvent(User user) {
    // Message contenant l'événement "UserCreated"
    if (user == null || user.getName() == null || user.getName().trim().length() < 3) {
        System.out.println("[JMS] Message ignore: nom utilisateur trop court.");
        return;
    }
    
    try (JMSContext context = factory.createContext()) {
        Instant timestamp = Instant.now();
        String payload = translator.toJson(user, timestamp, "user-service");
        
        // Envoi du message événement dans la queue
        context.createProducer().send(queue, payload);
        System.out.println("[JMS] Message envoyé à Artemis : " + payload);
    }
}
```

### Structure du message
```json
{
  "id": 1,
  "name": "Alice Dupont",
  "email": "alice@example.com",
  "timestamp": "2026-01-19T10:30:45.123Z",
  "source": "user-service"
}
```

### Avantages
- Découplage entre le producteur et les consommateurs
- Traçabilité de tous les événements métier
- Possibilité de multiples réactions à un même événement

---

## 2. Pattern : **Message Translator**

### Localisation
- Fichier : `src/main/java/com/example/messaging/UserCreatedMessageTranslator.java`
- Catégorie : **Transformation Patterns - Transformation & Enrichissement**

### Description
Le pattern **Message Translator** convertit un objet métier (entité `User`) en un format de message standardisé (JSON). Cela isole la logique de transformation et facilite les changements de format.

### Implémentation
```java
public class UserCreatedMessageTranslator {
    
    public String toJson(User user, Instant timestamp, String source) {
        String id = user.getId() == null ? "null" : String.valueOf(user.getId());
        String name = escapeJson(user.getName());
        String email = escapeJson(user.getEmail());
        String time = timestamp == null ? "" : timestamp.toString();
        String src = escapeJson(source);
        
        return "{"
            + "\"id\":" + id + ","
            + "\"name\":\"" + name + "\","
            + "\"email\":\"" + email + "\","
            + "\"timestamp\":\"" + time + "\","
            + "\"source\":\"" + src + "\""
            + "}";
    }
    
    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
```

### Flux de transformation
```
User (entité JPA)
    ↓
UserCreatedMessageTranslator.toJson()
    ↓
JSON String
    ↓
JMS Message
    ↓
Queue Artemis
```

### Avantages
- Séparation des responsabilités : métier ≠ sérialisation
- Facile à tester indépendamment
- Permet d'ajouter du contexte (timestamp, source) au message

---

## 3. Pattern : **Message Filter**

### Localisation
- Fichier : `src/main/java/com/example/messaging/UserCreatedProducer.java` (ligne 22-26)
- Catégorie : **Routing Patterns - Routage & Distribution**

### Description
Le pattern **Message Filter** contrôle quels messages sont envoyés et lesquels sont rejetés, basé sur des critères spécifiques. Ici, on valide la qualité du message avant envoi.

### Implémentation
```java
public void sendUserCreatedEvent(User user) {
    // MESSAGE FILTER : Valider les critères avant envoi
    if (user == null || user.getName() == null || user.getName().trim().length() < 3) {
        System.out.println("[JMS] Message ignore: nom utilisateur trop court.");
        return;  // Message filtré, non envoyé
    }
    
    // Message valide, continuer le traitement
    try (JMSContext context = factory.createContext()) {
        // ...envoi du message
    }
}
```

### Critères de filtrage
| Critère | Condition | Résultat |
|---------|-----------|----------|
| `user == null` | Objet null | Rejeté |
| `user.getName() == null` | Nom absent | Rejeté |
| `name.length() < 3` | Nom trop court | Rejeté |
| Tous valides | - | Accepté |

### Avantages
- Prévient les données invalides d'être traitées
- Réduit les erreurs en aval
- Improve la qualité globale des données

---

## Bonus Pattern : **Event-Driven Consumer**

### Localisation
- Fichier : `src/main/java/com/example/messaging/JmsUserCreatedConsumer.java`
- Catégorie : **Endpoint Patterns - Intégration & Communication**

### Description
Le pattern **Event-Driven Consumer** écoute activement les événements et déclenche des actions automatiques en réaction. Ici, chaque message `UserCreated` reçu crée automatiquement une `Notification`.

### Implémentation
```java
public synchronized void start() {
    if (running) return;
    running = true;
    
    thread = new Thread(() -> {
        try (JMSContext context = connectionFactory.createContext()) {
            JMSConsumer consumer = context.createConsumer(queue);
            System.out.println("Consumer JMS actif sur 'UserCreatedQueue'");
            
            // Écoute continue
            while (running && !Thread.currentThread().isInterrupted()) {
                String payload = consumer.receiveBody(String.class, 1000);
                if (payload != null) {
                    handleUserCreated(payload);  // Réaction à l'événement
                }
            }
        }
    });
    thread.start();
}

private void handleUserCreated(String payload) {
    // Traitement de l'événement
    JsonNode json = mapper.readTree(payload);
    Long userId = json.get("id").asLong();
    String email = json.get("email").asText();
    
    // Action : créer une Notification
    notificationService.createNotification(userId, 
        "Création du compte utilisateur : " + email);
}
```

### Flux complet
```
UserService.register(user)
    ↓
UserCreatedProducer.sendUserCreatedEvent()
    ↓
Message Filter (validation)
    ↓
Message Translator (JSON)
    ↓
Queue Artemis (persistence)
    ↓
JmsUserCreatedConsumer.handleUserCreated()
    ↓
NotificationService.createNotification()
    ↓
Notification persisted en base
```

---

## Diagramme de composants

```
┌────────────────────────────────────────────────────────────────┐
│                     CLIENT (REST)                               │
│                  POST /users (body: User)                        │
└───────────────────────┬──────────────────────────────────────────┘
                        │
                        ▼
┌──────────────────────────────────────────────────────────────────┐
│                    UserResource (REST)                            │
│              POST /users → UserService.register()                 │
└───────────┬───────────────────────────────┬──────────────────────┘
            │                               │
            ▼                               ▼
  ┌─────────────────────┐       ┌──────────────────────────┐
  │  UserService        │       │  NotificationService     │
  │                     │       │                          │
  │ - Validation (400)  │       │ - create(notification)   │
  │ - Email unique (409)│       │ - findById()             │
  │ - persist()         │       │ - update()               │
  └──────────┬──────────┘       └──────────────────────────┘
             │                            ▲
             ▼                            │
  ┌──────────────────────────────────────┼─────────────────┐
  │         JPA / EntityManager          │                 │
  │                                      │                 │
  │  Persist User ────────────────────→ DB               │
  └──────────────────────────────────────┼─────────────────┘
                                         │
                    ┌────────────────────┘
                    │
                    ▼
        ┌─────────────────────────┐
        │  UserCreatedProducer    │
        │  [EVENT MESSAGE]        │
        │  - Filter validation    │
        │  - Translator (JSON)    │
        │  - send() to Queue      │
        └────────────┬────────────┘
                     │
                     ▼
        ┌─────────────────────────────────────┐
        │  Apache ActiveMQ Artemis            │
        │  UserCreatedQueue (persistent)      │
        │  ┌───────────────────────────────┐  │
        │  │ Event Message (JSON)          │  │
        │  │ id, name, email, timestamp    │  │
        │  └───────────────────────────────┘  │
        └─────────────┬───────────────────────┘
                      │
                      ▼
        ┌─────────────────────────────────┐
        │ JmsUserCreatedConsumer          │
        │ [EVENT-DRIVEN CONSUMER]         │
        │ - start()                       │
        │ - listen for messages           │
        │ - handleUserCreated()           │
        └────────────┬────────────────────┘
                     │
                     ▼
        ┌─────────────────────────────────┐
        │  NotificationService            │
        │  createNotification(userId, msg)│
        │  - Load User from DB            │
        │  - Create Notification          │
        │  - Persist                      │
        └────────────┬────────────────────┘
                     │
                     ▼
        ┌─────────────────────────────────┐
        │  Database (JPA)                 │
        │  Notification table             │
        │  (userId, message, createdAt)   │
        └─────────────────────────────────┘
```

---

## Scénario d'utilisation complet

### Étape 1 : Création utilisateur (REST)
```bash
POST /users
Content-Type: application/json

{
  "name": "Bob Martin",
  "email": "bob@example.com",
  "password": "secure123"
}
```

### Étape 2 : Validation et persistance
- UserService valide les entrées
- Email unique en base
- User persisted en DB
- Response 201 CREATED

### Étape 3 : Event Message produit
```json
{
  "id": 5,
  "name": "Bob Martin",
  "email": "bob@example.com",
  "timestamp": "2026-01-19T10:30:45.123Z",
  "source": "user-service"
}
```

### Étape 4 : Message Filter
- Validation : nom.length() = 10 >= 3
- Message accepté

### Étape 5 : Message Translator
- Convertit User → JSON
- Ajoute timestamp et source

### Étape 6 : Envoi en Queue
- Message persisted dans Artemis
- Prêt pour consommation

### Étape 7 : Consumer écoute
- Consumer reçoit le message
- Parse JSON → extract userId & email

### Étape 8 : Action : créer Notification
```sql
INSERT INTO notifications (user_id, message, created_at)
VALUES (5, 'Création du compte utilisateur : bob@example.com', NOW())
```

### Étape 9 : Notification consultable
```bash
GET /notifications
[
  {
    "id": 1,
    "message": "Création du compte utilisateur : bob@example.com",
    "createdAt": "2026-01-19T10:30:45.123Z",
    "user": { "id": 5, "name": "Bob Martin", "email": "bob@example.com" }
  }
]
```

---

## Résumé des patterns

| Pattern | Type | Localisation | Bénéfice |
|---------|------|--------------|----------|
| **Event Message** | Messaging | UserCreatedProducer | Découplage, asynchrone |
| **Message Translator** | Transformation | UserCreatedMessageTranslator | Séparation des responsabilités |
| **Message Filter** | Routing | UserCreatedProducer | Qualité des données |
| **Event-Driven Consumer** | Endpoint | JmsUserCreatedConsumer | Réactivité, automatisation |

---

## 🏗️ Architecture générale

```
┌─────────────────────┐     ┌─────────────────────┐
│   REST Clients      │     │  Other Services     │
└──────────┬──────────┘     └──────────┬──────────┘
           │                           │
           └───────────┬───────────────┘
                       ▼
           ┌─────────────────────────┐
           │  REST API (JAX-RS)      │
           │  - UserResource         │
           │  - EventResource        │
           │  - ReservationResource  │
           │  - PaymentResource      │
           │  - NotificationResource │
           │  - VenueResource        │
           │  - TimeslotResource     │
           └───────────┬─────────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
    ┌────────┐  ┌──────────┐  ┌──────────────┐
    │Services│  │Messaging │  │JPA/Database │
    │        │  │  (JMS)   │  │              │
    │- User  │  │- Producer│  │ - User       │
    │- Event │  │- Consumer│  │ - Event      │
    │- ...   │  │- Translator
    │        │  │- Filter  │  │ - Notification
    └────────┘  └──────────┘  │ - ...
                               └──────────────┘
```

---

## Conclusion

Cette architecture démontre l'utilisation cohérente de **4 design patterns** (3 obligatoires + 1 bonus) pour créer une application résiliente, découplée et maintenable, conformément aux standards Jakarta EE 8 et aux bonnes pratiques d'intégration asynchrone.

