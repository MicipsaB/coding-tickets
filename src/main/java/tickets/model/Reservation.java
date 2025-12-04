package tickets.model;

import java.time.LocalDateTime;


public class Reservation {
private long id;
private LocalDateTime dateReservation;
private int nbPlaces;
private double montantTotal;
private StatutReservation statut;
private Client client;
private Evenement evenement;


public Reservation() {}


public Reservation(long id, LocalDateTime dateReservation, int nbPlaces, double montantTotal, Client client, Evenement evenement) {
this.id = id;
this.dateReservation = dateReservation;
this.nbPlaces = nbPlaces;
this.montantTotal = montantTotal;
this.statut = StatutReservation.CONFIRMEE;
this.client = client;
this.evenement = evenement;
}


// Getters/Setters
public long getId() { return id; }
public void setId(long id) { this.id = id; }
public LocalDateTime getDateReservation() { return dateReservation; }
public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
public int getNbPlaces() { return nbPlaces; }
public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; }
public double getMontantTotal() { return montantTotal; }
public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
public StatutReservation getStatut() { return statut; }
public void setStatut(StatutReservation statut) { this.statut = statut; }
public Client getClient() { return client; }
public void setClient(Client client) { this.client = client; }
public Evenement getEvenement() { return evenement; }
public void setEvenement(Evenement evenement) { this.evenement = evenement; }


// Méthode métier: annuler
public synchronized void annuler() {
if (statut == StatutReservation.ANNULEE) return;
// Règle simplifiée: annulation possible sauf si événement dans moins d'un jour
LocalDateTime now = LocalDateTime.now();
if (evenement.getDate().isBefore(now.plusDays(1))) {
throw new AnnulationTardiveException("Annulation trop tardive");
}
evenement.annulerPlaces(nbPlaces);
statut = StatutReservation.ANNULEE;
}
}