A. Lancer l'application
Importer le projet dans Eclipse → Dynamic Web Project
Vérifier que les Servlets 4.0+ sont activées
Déployer sur un serveur :
Apache Tomcat 9 / 10 / 10.1 / 11 recommandé
Démarrer le serveur
Accéder à l’application via :

http://localhost:8080/coding/

B. URLs principales
URL Description
/login Page de connexion
/logout Déconnexion
/events Liste des événements
/events/create Création d’un événement (organisateurs uniquement)
/reservations/create Création d’une réservation (POST)
/reservations/history Historique des réservations du client
/reservations/cancel Annulation d’une réservation (POST)

C. Comptes de test
Clients
email : batman@mail.com
mot de passe : passe

email : spiderman@mail.com
mot de passe : passe

email : flash@mail.comflash@mail.com
mot de passe : passe

email : joker@mail.comjoker@mail.com
mot de passe : passe

Organisateurs
email : orga1@mail.com
mot de passe : admin

email : orga1@mail.com
mot de passe : admin

(Ces comptes sont créés dans le ServletContextListener au démarrage.)
