-- ===========================================
-- Script SQL init pour Docker (PostgreSQL)
-- ===========================================

-- ===========================================
-- ENUMs
-- ===========================================

DROP TYPE IF EXISTS role CASCADE;
CREATE TYPE role AS ENUM ('CLIENT', 'ORGANISATEUR');

DROP TYPE IF EXISTS statut_reservation CASCADE;
CREATE TYPE statut_reservation AS ENUM ('CONFIRMEE', 'ANNULEE');

-- ===========================================
-- Table des utilisateurs
-- ===========================================

DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS evenement CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;

CREATE TABLE utilisateur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role role NOT NULL,
    date_creation TIMESTAMP DEFAULT NOW()
);

-- ===========================================
-- Table des événements
-- ===========================================

CREATE TABLE evenement (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    description TEXT,
    lieu VARCHAR(100),
    date_evenement TIMESTAMP NOT NULL,
    nb_places_totales INT NOT NULL CHECK (nb_places_totales > 0),
    nb_places_restantes INT NOT NULL CHECK (nb_places_restantes >= 0),
    prix_base NUMERIC(10, 2) NOT NULL CHECK (prix_base >= 0),
    organisateur_id INT NOT NULL REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- ===========================================
-- Table des réservations
-- ===========================================

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    date_reservation TIMESTAMP DEFAULT NOW(),
    nb_places INT NOT NULL CHECK (nb_places > 0),
    montant_total NUMERIC(10, 2) NOT NULL CHECK (montant_total >= 0),
    statut statut_reservation DEFAULT 'CONFIRMEE',
    client_id INT NOT NULL REFERENCES utilisateur(id) ON DELETE CASCADE,
    evenement_id INT NOT NULL REFERENCES evenement(id) ON DELETE CASCADE
);

-- ===========================================
-- Insertion d'utilisateurs exemples
-- ===========================================

INSERT INTO utilisateur (nom, email, mot_de_passe, role) VALUES
('Batman', 'batman@mail.com', '556972429c265aab19b92b814d2ffe9b2b4dc7e99176aae6e211ad425a37ec15', 'CLIENT'),
('Spiderman', 'spiderman@mail.com', '556972429c265aab19b92b814d2ffe9b2b4dc7e99176aae6e211ad425a37ec15', 'CLIENT'),
('queen', 'queen@mail.com', '556972429c265aab19b92b814d2ffe9b2b4dc7e99176aae6e211ad425a37ec15', 'CLIENT'),
('Antman', 'antman@mail.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ORGANISATEUR'),
('joker', 'joker@mail.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ORGANISATEUR');

-- ===========================================
-- Insertion d'événements exemples
-- ===========================================

INSERT INTO evenement (titre, description, lieu, date_evenement, nb_places_totales, nb_places_restantes, prix_base, organisateur_id) VALUES
('Concert Blues', 'Concert de blues US', 'Arena', '2026-01-15 20:00:00', 500, 500, 50.00, 4),
('Conférence politique', 'Conférence sur la politique occidentale', 'Europe1', '2026-02-10 09:00:00', 200, 200, 150.00, 5);

-- ===========================================
-- Insertion de réservations exemples
-- ===========================================

INSERT INTO reservation (nb_places, montant_total, client_id, evenement_id) VALUES
(2, 100.00, 1, 1),
(1, 50.00, 2, 1),
(1, 150.00, 1, 2);
