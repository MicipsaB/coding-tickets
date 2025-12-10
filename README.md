# Projet **ticketing-app** — Application de gestion de billets

Application web Java (JSP / Servlets) permettant de :

- gérer des événements
- réserver des places
- gérer les comptes utilisateurs (clients / organisateurs)

L'application fonctionne avec **Docker**, **Tomcat 10.1** et **PostgreSQL**.

---

## Lancer l'application avec Docker

### 1. Prérequis

- Docker
- Docker Compose

Vérifier :

```bash
docker --version
docker-compose --version
```

### 2. Démarrage

À la racine du projet :

```bash
docker-compose up --build
```

Attendre que tout démarre, puis ouvrir :

http://localhost:8080/

### 3. Arrêter l'application

```bash
docker-compose down
```

Pour réinitialiser la base de données :

```bash
docker-compose down -v
docker-compose up --build
```

---

## URLs principales

| Fonction                                         | URL                     |
| ------------------------------------------------ | ----------------------- |
| Page de connexion                                | `/login`                |
| Liste des événements                             | `/events`               |
| Création / modification événement (organisateur) | `/events/create`        |
| Historique des réservations (client)             | `/reservations/history` |
| Création de réservation (POST)                   | `/reservations/create`  |
| Annulation de réservation (POST)                 | `/reservations/cancel`  |
| Déconnexion                                      | `/logout`               |

Exemple complet : http://localhost:8080/events

---

## Comptes de test

Ces comptes sont chargés automatiquement par le script SQL `init_ticketing.sql` :

### Organisateurs

| Nom    | Email           | Mot de passe |
| ------ | --------------- | ------------ |
| Antman | antman@mail.com | admin        |
| Joker  | joker@mail.com  | admin        |

Ils peuvent :

- créer des événements
- modifier leurs propres événements

### Clients

| Nom       | Email              | Mot de passe |
| --------- | ------------------ | ------------ |
| Batman    | batman@mail.com    | passe        |
| Spiderman | spiderman@mail.com | passe        |
| Queen     | queen@mail.com     | passe        |

Ils peuvent :

- consulter les événements
- réserver des places
- voir leurs réservations
- annuler leurs réservations

---

## Architecture technique

- Java 17+
- JSP / Servlets
- Apache Tomcat 10.1
- PostgreSQL 15
- JDBC (DAO personnalisés)
- Docker & Docker Compose

---

## Base de données

- **Nom** : `ticketing`
- **Utilisateur** : `postgres`
- **Mot de passe** : `password`
- **Script d'initialisation** : `init_ticketing.sql`

Contient tables : `utilisateur`, `evenement`, `reservation` et les enums `role` et `statut_reservation`.

---

## Développement

**Package principal** : `tickets.*`

Contient :

- `controller` → servlets
- `dao` → accès base de données
- `service` → logique métier
- `model` → entités

---

## Notes

- Pour un organisateur, seule la création/modification de ses propres événements est autorisée.
- Les clients peuvent réserver des événements et annuler leurs réservations.
- Tous les messages flash sont affichés dans les JSP via `sessionScope.msg`.
