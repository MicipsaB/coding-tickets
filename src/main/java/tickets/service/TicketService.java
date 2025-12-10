package tickets.service;

import tickets.dao.*;
import tickets.model.*;
import tickets.util.PasswordUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TicketService {

    private final EvenementDao evenementDao;
    private final ReservationDao reservationDao;
    private final UtilisateurDao utilisateurDao;

    public TicketService(EvenementDao evenementDao,
                         ReservationDao reservationDao,
                         UtilisateurDao utilisateurDao) {
        this.evenementDao = evenementDao;
        this.reservationDao = reservationDao;
        this.utilisateurDao = utilisateurDao;
    }

    // ============================================================
    // ===============     1. RESERVER          ===================
    // ============================================================

    public Reservation reserver(long clientId, long evenementId, int nbPlaces)
            throws ServiceException {

        try {
            // CORRECTION : Vérifier null AVANT le cast
            Utilisateur u = utilisateurDao.findById(clientId);
            if (u == null) {
                throw new ServiceException("Client introuvable.");
            }
            
            if (!(u instanceof Client)) {
                throw new ServiceException("Utilisateur non client");
            }
            Client client = (Client) u;

            // Vérification de l'événement
            Evenement ev = evenementDao.findById(evenementId);
            if (ev == null) {
                throw new ServiceException("Événement introuvable.");
            }

            // Validation du nombre de places
            if (nbPlaces <= 0) {
                throw new ServiceException("Nombre de places invalide.");
            }

            if (ev.getNbPlacesRestantes() < nbPlaces) {
                throw new ServiceException("Pas assez de places disponibles.");
            }

            // Calcul du prix
            double montant = nbPlaces * ev.getPrixBase();

            // Création de la réservation
            Reservation r = new Reservation(
                    0,
                    LocalDateTime.now(),
                    nbPlaces,
                    montant,
                    client,
                    ev
            );

            reservationDao.create(r);

            // Mise à jour des places restantes
            ev.setNbPlacesRestantes(ev.getNbPlacesRestantes() - nbPlaces);
            evenementDao.update(ev);

            return r;

        } catch (DaoException e) {
            throw new ServiceException("Erreur lors de la réservation", e);
        }
    }

    // ============================================================
    // ===============     2. ANNULER           ===================
    // ============================================================

    public void annulerReservation(long reservationId, Client client) throws ServiceException {
        try {
            Reservation r = reservationDao.findById(reservationId);
            if (r == null) {
                throw new ServiceException("Réservation introuvable.");
            }

            if (r.getClient().getId() != client.getId()) {
                throw new ServiceException("Vous ne pouvez annuler que vos propres réservations.");
            }

            if (r.getStatut() == StatutReservation.ANNULEE) {
                throw new ServiceException("Réservation déjà annulée.");
            }

            LocalDate dateEvent = r.getEvenement().getDateEvenement().toLocalDate();
            LocalDate aujourdHui = LocalDate.now();

            if (!aujourdHui.isBefore(dateEvent)) {
                throw new ServiceException("Annulation impossible : événement imminent.");
            }

            // Libérer les places
            Evenement ev = r.getEvenement();
            ev.setNbPlacesRestantes(ev.getNbPlacesRestantes() + r.getNbPlaces());
            evenementDao.update(ev);

            // Mise à jour du statut
            r.setStatut(StatutReservation.ANNULEE);
            reservationDao.update(r);

        } catch (DaoException e) {
            throw new ServiceException("Erreur annulation", e);
        }
    }

    // ============================================================
    // ======= 3. CRÉATION / MODIFICATION ÉVÉNEMENT ===============
    // ============================================================

    public void creerEvenement(Evenement ev, long organisateurId) throws ServiceException {
        try {
            Utilisateur u = utilisateurDao.findById(organisateurId);
            if (u == null) {
                throw new ServiceException("Organisateur introuvable.");
            }
            
            if (!(u instanceof Organisateur)) {
                throw new ServiceException("Utilisateur non organisateur");
            }
            Organisateur org = (Organisateur) u;

            ev.setOrganisateur(org);

            if (ev.getNbPlacesRestantes() > ev.getNbPlacesTotales()) {
                throw new ServiceException("Nb places restantes invalide.");
            }

            evenementDao.create(ev);

        } catch (DaoException e) {
            throw new ServiceException("Erreur création événement", e);
        }
    }

    public void modifierEvenement(Evenement ev, long organisateurId) throws ServiceException {
        try {
            Utilisateur u = utilisateurDao.findById(organisateurId);
            if (u == null) {
                throw new ServiceException("Organisateur introuvable.");
            }
            
            if (!(u instanceof Organisateur)) {
                throw new ServiceException("Utilisateur non organisateur");
            }

            if (ev.getNbPlacesRestantes() > ev.getNbPlacesTotales()) {
                throw new ServiceException("Nb places restantes > nb total.");
            }

            evenementDao.update(ev);

        } catch (DaoException e) {
            throw new ServiceException("Erreur modification événement", e);
        }
    }

    // ============================================================
    // ===============     4. LISTAGES         ====================
    // ============================================================

    public List<Evenement> listerEvenements() throws ServiceException {
        try {
            return evenementDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Erreur récupération événements", e);
        }
    }

    public List<Evenement> listerEvenementsOrganisateur(long orgId) throws ServiceException {
        try {
            Utilisateur u = utilisateurDao.findById(orgId);
            if (u == null) {
                throw new ServiceException("Organisateur introuvable.");
            }
            
            if (!(u instanceof Organisateur)) {
                throw new ServiceException("Utilisateur non organisateur");
            }
            Organisateur org = (Organisateur) u;

            return evenementDao.findByOrganisateur(org);

        } catch (DaoException e) {
            throw new ServiceException("Erreur liste événements organisateur", e);
        }
    }

    public List<Reservation> listerReservationsClient(long clientId) throws ServiceException {
        try {
            Utilisateur u = utilisateurDao.findById(clientId);
            if (u == null) {
                throw new ServiceException("Client introuvable.");
            }
            
            if (!(u instanceof Client)) {
                throw new ServiceException("Utilisateur non client");
            }
            Client client = (Client) u;

            return reservationDao.findByClient(client);

        } catch (DaoException e) {
            throw new ServiceException("Erreur liste réservations client", e);
        }
    }

    // ============================================================
    // ============   5. RÉCUPÉRATION SIMPLE   ====================
    // ============================================================

    public Evenement getEvenement(long id) throws ServiceException {
        try {
            return evenementDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Erreur getEvenement", e);
        }
    }

    // ============================================================
    // ============ 6. AUTHENTIFICATION ==========================
    // ============================================================

    public Utilisateur authentifier(String email, String password) throws ServiceException {
        try {
            Utilisateur u = utilisateurDao.findByEmail(email);
            if (u != null && PasswordUtil.verify(password, u.getMotDePasse())) {
                return u;
            }
            return null;
        } catch (DaoException e) {
            throw new ServiceException("Erreur authentification", e);
        }
    }

    public Client getClient(long id) throws ServiceException {
        try {
            Utilisateur u = utilisateurDao.findById(id);
            if (u == null) {
                return null;
            }
            if (!(u instanceof Client)) {
                throw new ServiceException("Utilisateur trouvé n'est pas un client");
            }
            return (Client) u;
        } catch (DaoException e) {
            throw new ServiceException("Erreur getClient", e);
        }
    }

    public Reservation getReservation(long id) throws ServiceException {
        try {
            return reservationDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException("Erreur getReservation", e);
        }
    }
}