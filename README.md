# ViensJouerL-

Projet Jakarta EE 8 réalisé en groupe, proposant une API REST pour la gestion d’événements, de réservations et d’utilisateurs, avec persistance JPA et communication JMS lors de la création d’un utilisateur.

## Prérequis
- Java
- Maven
- Docker

## Démarrage rapide
1. `mvn clean package` : compile le projet et génère l’artefact.
2. `docker-compose up -d` : démarre les services nécessaires en arrière‑plan.
3. Lancer `Main.java` : démarre l’application et initialise les composants.

## Description générale de l’API
L’API a pour objectif de gérer la réservation de salles d’esport : création de comptes, choix d’un lieu, sélection d’un créneau horaire et validation de la réservation. Le projet a été conçu comme une application Jakarta EE 8 complète, en équipe, afin de mettre en pratique les concepts vus en cours.

Le fonctionnement suit un schéma classique : les ressources JAX‑RS reçoivent les requêtes HTTP, délèguent la logique métier aux services (disponibilités, réservations, paiements), puis la persistance est assurée via JPA. Cette séparation facilite la compréhension des responsabilités et la maintenance du code.

La gestion des utilisateurs constitue un module central. Lorsqu’un utilisateur est créé, un événement JMS est publié, et un consommateur JMS (file de messages) réagit pour générer automatiquement une notification. Cela illustre un traitement asynchrone intégré au flux applicatif.

L’architecture met en œuvre plusieurs patterns (couche service, séparation des responsabilités, logique événementielle) afin de préserver la cohérence et la lisibilité de l’ensemble.

## Modèle de données – Tables des entités

### Table : User
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| name | String | Nom de l’utilisateur |
| email | String | Email unique |
| password | String | Mot de passe |
| isAdmin | Boolean | Indique un rôle administrateur |

### Table : Event
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| title | String | Titre de l’événement |
| description | String | Description textuelle |
| eventDate | Date | Date et heure |
| gameType | String | Type de jeu |
| capacity | Integer | Capacité maximale |

### Table : Venue
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| name | String | Nom du lieu |
| address | String | Adresse |
| capacity | Integer | Capacité maximale |
| isAvailable | Boolean | Disponibilité |

### Table : Timeslot
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| start | Date | Début du créneau |
| endTime | Date | Fin du créneau |
| isReserved | Boolean | Indique si réservé |
| venue_id | Long | Référence du lieu |

### Table : Reservation
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| user_id | Long | Référence utilisateur |
| event_id | Long | Référence événement |
| venue_id | Long | Référence lieu |
| timeslot_id | Long | Référence créneau |
| reservationDate | Date | Date de réservation |

### Table : Payment
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| amount | BigDecimal | Montant |
| method | String | Méthode de paiement |
| isConfirmed | Boolean | Paiement confirmé |
| reservation_id | Long | Référence réservation |

### Table : Notification
| Colonne | Type | Description |
|--------|------|-------------|
| id | Long | Identifiant technique |
| message | String | Contenu |
| createdAt | Instant | Date de création |
| user_id | Long | Référence utilisateur |

**Relations principales** :
- User → Reservation : OneToMany
- User → Notification : OneToMany
- Event → Reservation : OneToMany
- Venue → Reservation : OneToMany
- Venue → Timeslot : OneToMany
- Timeslot → Reservation : OneToMany
- Reservation → Payment : OneToOne
- Reservation → User/Event/Venue/Timeslot : ManyToOne
- Notification → User : ManyToOne

## URL de base

Le projet expose les endpoints sous `/api`.

- Mode standalone (Main.java / Grizzly):
  `http://localhost:8080/api`

---

## Endpoints disponibles

### Users (`/users`)
- `POST /users` - creer un utilisateur
  ```json
  {
    "name": "Jean Dupont",
    "email": "jean@example.com",
    "password": "secret",
    "isAdmin": false
  }
  ```
- `GET /users` - liste des utilisateurs
- `GET /users/{id}` - detail d'un utilisateur
- `PUT /users/{id}` - modifier un utilisateur (meme schema que POST)
- `DELETE /users/{id}` - supprimer un utilisateur

---

### Events (`/events`)
- `POST /events` - creer un evenement
  ```json
  {
    "title": "Concert 2026",
    "description": "Un super concert",
    "eventDate": "2026-06-12T20:00:00Z",
    "gameType": "concert",
    "capacity": 500
  }
  ```
- `GET /events` - liste des evenements
- `GET /events/{id}` - detail d'un evenement
- `PUT /events/{id}` - modifier un evenement (meme schema que POST)
- `DELETE /events/{id}` - supprimer un evenement

---

### Venues (`/venues`)
- `POST /venues` - creer une salle
  ```json
  {
    "name": "Zenith de Paris",
    "address": "211 avenue Jean Jaures",
    "capacity": 2000,
    "isAvailable": true
  }
  ```
- `GET /venues` - liste des salles
- `GET /venues/{id}` - detail d'une salle
- `PUT /venues/{id}` - modifier une salle (meme schema que POST)
- `DELETE /venues/{id}` - supprimer une salle

---

### Timeslots (`/timeslots`)
- `POST /timeslots` - creer un creneau
  ```json
  {
    "start": "2026-01-15T19:00:00Z",
    "endTime": "2026-01-15T21:00:00Z",
    "venue": { "id": 1 }
  }
  ```
- `GET /timeslots` - liste des creneaux
- `GET /timeslots/{id}` - detail d'un creneau
- `PUT /timeslots/{id}` - modifier un creneau (meme schema que POST)
- `DELETE /timeslots/{id}` - supprimer un creneau

---

### Reservations (`/reservations`)
- `POST /reservations` - creer une reservation
  ```json
  {
    "userId": 1,
    "eventId": 1,
    "venueId": 1,
    "timeslotId": 1,
    "reservationDate": "2026-01-15T19:10:00Z"
  }
  ```
- `GET /reservations` - liste des reservations
- `GET /reservations/{id}` - detail d'une reservation
- `PUT /reservations/{id}` - modifier une reservation (meme schema que POST)
- `DELETE /reservations/{id}` - supprimer une reservation

---

### Payments (`/payments`)
- `POST /payments` - creer un paiement
  ```json
  {
    "reservationId": 1,
    "amount": 75.50,
    "method": "carte"
  }
  ```
- `GET /payments` - liste des paiements
- `GET /payments/{id}` - detail d'un paiement
- `PUT /payments/{id}` - modifier un paiement (meme schema que POST)
- `DELETE /payments/{id}` - supprimer un paiement

---

### Notifications (`/notifications`)
- `POST /notifications` - creer une notification
  ```json
  {
    "userId": 1,
    "message": "Votre reservation est confirmee"
  }
  ```
- `GET /notifications` - liste des notifications
- `GET /notifications/{id}` - detail d'une notification
- `PUT /notifications/{id}` - modifier une notification (meme schema que POST)
- `DELETE /notifications/{id}` - supprimer une notification

---

## Exemples avec Postman

Base URL :
```
http://localhost:8080/api
```

### Creer un utilisateur
- Method: `POST`
- URL: `{{baseUrl}}/users`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "name": "Alice Martin",
  "email": "alice@example.com",
  "password": "secret"
}
```

### Creer un evenement
- Method: `POST`
- URL: `{{baseUrl}}/events`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "title": "Festival de musique",
  "description": "Le plus grand festival de l\"annee",
  "eventDate": "2026-06-12T20:00:00Z",
  "gameType": "festival",
  "capacity": 1000
}
```

### Creer une salle
- Method: `POST`
- URL: `{{baseUrl}}/venues`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "name": "Zenith de Paris",
  "address": "211 avenue Jean Jaures",
  "capacity": 2000,
  "isAvailable": true
}
```

### Creer un creneau
- Method: `POST`
- URL: `{{baseUrl}}/timeslots`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "start": "2026-01-15T19:00:00Z",
  "endTime": "2026-01-15T21:00:00Z",
  "venue": { "id": 1 }
}
```

### Creer une reservation
- Method: `POST`
- URL: `{{baseUrl}}/reservations`
- Headers: `Content-Type: application/json`
- Body (raw / JSON):
```json
{
  "userId": 1,
  "eventId": 1,
  "venueId": 1,
  "timeslotId": 1,
  "reservationDate": "2026-01-15T19:10:00Z"
}
```

---

## Scenario complet: creer une reservation

Ce scenario suppose que la base est vide. Remplace les ids par ceux retournes par chaque creation.

1) Creer un utilisateur
- POST `{{baseUrl}}/users`
```json
{
  "name": "Alice Martin",
  "email": "alice@example.com",
  "password": "secret"
}
```

2) Creer une salle
- POST `{{baseUrl}}/venues`
```json
{
  "name": "Zenith de Paris",
  "address": "211 avenue Jean Jaures",
  "capacity": 2000,
  "isAvailable": true
}
```

3) Creer un evenement
- POST `{{baseUrl}}/events`
```json
{
  "title": "Festival de musique",
  "description": "Le plus grand festival de l\"annee",
  "eventDate": "2026-06-12T20:00:00Z",
  "gameType": "festival",
  "capacity": 1000
}
```

4) Creer un creneau lie a la salle
- POST `{{baseUrl}}/timeslots`
```json
{
  "start": "2026-01-15T19:00:00Z",
  "endTime": "2026-01-15T21:00:00Z",
  "venue": { "id": 1 }
}
```

5) Creer la reservation
- POST `{{baseUrl}}/reservations`
```json
{
  "userId": 1,
  "eventId": 1,
  "venueId": 1,
  "timeslotId": 1,
  "reservationDate": "2026-01-15T19:10:00Z"
}
```

6) (Optionnel) Creer un paiement
- POST `{{baseUrl}}/payments`
```json
{
  "reservationId": 1,
  "amount": 75.50,
  "method": "carte"
}
```

---

## Notes
- Les champs `user`, `event`, `venue`, `timeslot` sont ignores dans les reponses JSON des reservations (annotations `@JsonIgnore`).
- Les erreurs usuelles: `400` si la requete est invalide, `404` si l'entite n'existe pas.

## Équipe
Projet réalisé par : Alexandre DU, Nazim Ouamer Ali, Mehdi Atmane et Amayes Goulmane.
