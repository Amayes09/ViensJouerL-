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

## Équipe
Projet réalisé par : Alexandre DU, Nazim Ouamer Ali, Mehdi Atmane et Amayes Goulmane.
