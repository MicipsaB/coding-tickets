package tickets.service;

import tickets.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TicketService {
    private final List<Utilisateur> utilisateurs = Collections.synchronizedList(new ArrayList<>());
    private final List<Evenement> evenements = Collections.synchronizedList(new ArrayList<>());
    private final List<Reservation> reservations = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong idGen = new AtomicLong(1);

    // Authentication
    public Utilisateur authentifier(String email, String motDePasse) {
        synchronized(utilisateurs) {
            return utilisateurs.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getMotDePasse().equals(motDePasse))
                .findFirst().orElse(null);
        }
    }

    // Evenements
    public List<Evenement> listerEvenements() {
        synchronized(evenements) {
            return new ArrayList<>(evenements);
        }
    }

    public Evenement trouverEvenementParId(long id) {
        synchronized(evenements) {
            return evenements.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        }
    }

    // Reservation
    public Reservation reserver(Client client, long idEvenement, int nbPlaces) {
        Evenement ev = trouverEvenementParId(idEvenement);
        if (ev == null) throw new IllegalArgumentException("Evenement introuvable");
        synchronized(ev) {
            ev.reserverPlaces(nbPlaces);
        }
        long id = idGen.getAndIncrement();
        double montant = nbPlaces * ev.getPrixBase();
        Reservation r = new Reservation(id, LocalDateTime.now(), nbPlaces, montant, client, ev);
        reservations.add(r);
        return r;
    }

    public List<Reservation> listerReservationsClient(Client client) {
        synchronized(reservations) {
            return reservations.stream()
                .filter(r -> r.getClient().getId() == client.getId())
                .collect(Collectors.toList());
        }
    }

    public void annulerReservation(long idReservation, Client client) {
        Reservation r;
        synchronized(reservations) {
            r = reservations.stream().filter(x -> x.getId() == idReservation).findFirst().orElse(null);
        }
        if (r == null) throw new IllegalArgumentException("Réservation introuvable");
        if (r.getClient().getId() != client.getId()) throw new SecurityException("Accès refusé");
        r.annuler();
    }

    // Organisateur: creer evenement
    public Evenement creerEvenement(Organisateur org, String titre, String description, LocalDateTime date, String lieu, int nbPlacesTotales, double prixBase) {
        long id = idGen.getAndIncrement();
        Evenement e = new Evenement(id, titre, description, date, lieu, nbPlacesTotales, prixBase);
        evenements.add(e);
        return e;
    }

    // Utilisateurs: helper pour l'init
    public Utilisateur creerUtilisateur(Utilisateur u) {
        u.setId(idGen.getAndIncrement());
        utilisateurs.add(u);
        return u;
    }

    // Getters (pour debug / affichage)
    public List<Utilisateur> getUtilisateurs() { return new ArrayList<>(utilisateurs); }
    public List<Evenement> getEvenements() { return new ArrayList<>(evenements); }
    public List<Reservation> getReservations() { return new ArrayList<>(reservations); }
}