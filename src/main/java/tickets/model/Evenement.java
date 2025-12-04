package tickets.model;

import java.time.LocalDateTime;


public class Evenement {
private long id;
private String titre;
private String description;
private LocalDateTime date;
private String lieu;
private int nbPlacesTotales;
private int nbPlacesRestantes;
private double prixBase;


public Evenement() {}


public Evenement(long id, String titre, String description, LocalDateTime date, String lieu, int nbPlacesTotales, double prixBase) {
this.id = id;
this.titre = titre;
this.description = description;
this.date = date;
this.lieu = lieu;
this.nbPlacesTotales = nbPlacesTotales;
this.nbPlacesRestantes = nbPlacesTotales;
this.prixBase = prixBase;
}


// Getters/Setters
public long getId() { return id; }
public void setId(long id) { this.id = id; }
public String getTitre() { return titre; }
public void setTitre(String titre) { this.titre = titre; }
public String getDescription() { return description; }
public void setDescription(String description) { this.description = description; }
public LocalDateTime getDate() { return date; }
public void setDate(LocalDateTime date) { this.date = date; }
public String getLieu() { return lieu; }
public void setLieu(String lieu) { this.lieu = lieu; }
public int getNbPlacesTotales() { return nbPlacesTotales; }
public void setNbPlacesTotales(int nbPlacesTotales) { this.nbPlacesTotales = nbPlacesTotales; }
public int getNbPlacesRestantes() { return nbPlacesRestantes; }
public void setNbPlacesRestantes(int nbPlacesRestantes) { this.nbPlacesRestantes = nbPlacesRestantes; }
public double getPrixBase() { return prixBase; }
public void setPrixBase(double prixBase) { this.prixBase = prixBase; }


// Méthodes métier
public synchronized void reserverPlaces(int nb) {
if (nb <= 0) throw new IllegalArgumentException("Le nombre de places doit être positif");
if (nb > nbPlacesRestantes) {
throw new PlacesInsuffisantesException("Pas assez de places disponibles");
}
nbPlacesRestantes -= nb;
}


public synchronized void annulerPlaces(int nb) {
if (nb <= 0) throw new IllegalArgumentException("Le nombre de places doit être positif");
nbPlacesRestantes += nb;
if (nbPlacesRestantes > nbPlacesTotales) nbPlacesRestantes = nbPlacesTotales;
}
}