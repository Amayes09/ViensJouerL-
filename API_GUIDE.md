# 📱 API REST - Guide d'utilisation

## 🚀 Démarrage rapide

### Prérequis
- Java 21+
- Maven 3.8+
- PostgreSQL 15
- ActiveMQ Artemis (optionnel pour la messagerie)

### Démarrer les services
```bash
docker-compose up -d
```

### Compiler et déployer
```bash
mvn clean install
```

### URL de base
```
http://localhost:8080/jakartaee-starter/api
```

---

## 📚 Endpoints disponibles

### Users (`/api/users`)
- `POST /users` - Créer un utilisateur
  ```json
  {
    "name": "Jean Dupont",
    "email": "jean@example.com"
  }
  ```

- `GET /users` - Récupérer tous les utilisateurs
- `GET /users/{id}` - Récupérer un utilisateur par ID
- `GET /users/email/{email}` - Chercher par email
- `PUT /users/{id}` - Modifier un utilisateur
- `DELETE /users/{id}` - Supprimer un utilisateur

---

### Events (`/api/events`)
- `POST /events` - Créer un événement
  ```json
  {
    "title": "Concert 2026",
    "description": "Un super concert!"
  }
  ```

- `GET /events` - Récupérer tous les événements
- `GET /events/{id}` - Récupérer un événement
- `GET /events/search?title=Concert` - Chercher par titre
- `PUT /events/{id}` - Modifier un événement
- `DELETE /events/{id}` - Supprimer un événement

---

### Venues (`/api/venues`)
- `POST /venues` - Créer une salle
  ```json
  {
    "name": "Zénith de Paris",
    "address": "211 avenue Jean Jaurès",
    "postalCode": "75019",
    "city": "Paris"
  }
  ```

- `GET /venues` - Récupérer toutes les salles
- `GET /venues/{id}` - Récupérer une salle
- `GET /venues/city/{city}` - Salles d'une ville
- `GET /venues/postalcode/{postalCode}` - Salles d'un code postal
- `PUT /venues/{id}` - Modifier une salle
- `DELETE /venues/{id}` - Supprimer une salle

---

### Reservations (`/api/reservations`)
- `POST /reservations?userId=1&eventId=1&venueId=1` - Créer une réservation
- `GET /reservations` - Toutes les réservations
- `GET /reservations/{id}` - Une réservation
- `GET /reservations/user/{userId}` - Réservations d'un utilisateur
- `GET /reservations/event/{eventId}` - Réservations d'un événement
- `GET /reservations/venue/{venueId}` - Réservations d'une salle
- `PUT /reservations/{id}?userId=1&eventId=1&venueId=1` - Modifier
- `DELETE /reservations/{id}` - Supprimer
- `DELETE /reservations/{id}/cancel` - Annuler

---

### Payments (`/api/payments`)
- `POST /payments?reservationId=1&amount=50.00&method=carte` - Créer un paiement
- `GET /payments` - Tous les paiements
- `GET /payments/{id}` - Un paiement
- `GET /payments/reservation/{reservationId}` - Paiements d'une réservation
- `GET /payments/method/{method}` - Paiements par méthode (carte, PayPal, etc.)
- `PUT /payments/{id}?amount=50.00&method=carte` - Modifier
- `DELETE /payments/{id}` - Supprimer
- `POST /payments/{id}/validate` - Valider un paiement
- `GET /payments/total` - Total des paiements

---

### Timeslots (`/api/timeslots`)
- `POST /timeslots` - Créer un créneau
  ```json
  {
    "start": "2026-01-15T19:00:00",
    "end": "2026-01-15T21:00:00"
  }
  ```

- `GET /timeslots` - Tous les créneaux
- `GET /timeslots/{id}` - Un créneau
- `GET /timeslots/available` - Créneaux disponibles
- `GET /timeslots/reserved` - Créneaux réservés
- `POST /timeslots/{id}/reserve` - Réserver un créneau
- `POST /timeslots/{id}/release` - Libérer un créneau
- `PUT /timeslots/{id}` - Modifier un créneau
- `DELETE /timeslots/{id}` - Supprimer un créneau

---

### Notifications (`/api/notifications`)
- `POST /notifications?userId=1&message=Votre%20réservation%20est%20confirmée` - Créer une notification
- `GET /notifications` - Toutes les notifications
- `GET /notifications/{id}` - Une notification
- `GET /notifications/user/{userId}` - Notifications d'un utilisateur
- `GET /notifications/user/{userId}/recent?days=7` - Notifications récentes
- `PUT /notifications/{id}?message=Nouveau%20message` - Modifier
- `DELETE /notifications/{id}` - Supprimer
- `DELETE /notifications/cleanup?days=30` - Supprimer les anciennes (30j par défaut)

---

## 🧪 Exemples avec curl

### Créer un utilisateur
```bash
curl -X POST http://localhost:8080/jakartaee-starter/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Martin",
    "email": "alice@example.com"
  }'
```

### Récupérer tous les utilisateurs
```bash
curl http://localhost:8080/jakartaee-starter/api/users
```

### Créer un événement
```bash
curl -X POST http://localhost:8080/jakartaee-starter/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Festival de musique",
    "description": "Le plus grand festival de l'\''année"
  }'
```

### Créer une réservation
```bash
curl -X POST "http://localhost:8080/jakartaee-starter/api/reservations?userId=1&eventId=1&venueId=1" \
  -H "Content-Type: application/json"
```

### Créer un paiement
```bash
curl -X POST "http://localhost:8080/jakartaee-starter/api/payments?reservationId=1&amount=75.50&method=carte" \
  -H "Content-Type: application/json"
```

### Valider un paiement
```bash
curl -X POST http://localhost:8080/jakartaee-starter/api/payments/1/validate
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     REST Resources                          │
│  (UserResource, EventResource, VenueResource, etc.)         │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │ Inject
                        ↓
┌─────────────────────────────────────────────────────────────┐
│                     Services                                 │
│  (UserService, EventService, VenueService, etc.)            │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │ EntityManager
                        ↓
┌─────────────────────────────────────────────────────────────┐
│                  JPA / Hibernate                             │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────────┐
│                   PostgreSQL                                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 Configuration requise

### Datasource (JNDI)
Ton serveur d'application doit avoir une datasource nommée `jdbc/starter` pointant vers PostgreSQL:

```xml
<!-- Exemple pour Payara/Glassfish -->
<jdbc-connection-pool>
  <name>starterPool</name>
  <datasource-classname>org.postgresql.ds.PGSimpleDataSource</datasource-classname>
  <property name="serverName" value="localhost"/>
  <property name="portNumber" value="5432"/>
  <property name="databaseName" value="starterdb"/>
  <property name="user" value="admin"/>
  <property name="password" value="admin"/>
</jdbc-connection-pool>

<jdbc-resource jndi-name="jdbc/starter" pool-name="starterPool"/>
```

---

## 🎯 Points clés

✅ **Tous les endpoints implémentés**
✅ **Architecture CRUD complète**
✅ **Validation des données**
✅ **Gestion des erreurs**
✅ **Transactions JTA**
✅ **Messagerie JMS prête** (UserCreatedListener/Producer)

---

## 🐛 Troubleshooting

- **Tables non créées** → Vérifier que `jakarta.persistence.schema-generation.database.action=create` est dans persistence.xml
- **Connexion BDD échouée** → Vérifier la datasource JNDI et PostgreSQL
- **Endpoints non accessibles** → Vérifier que RestApplication étend Application et a @ApplicationPath

---

**Bon développement!** 🚀
