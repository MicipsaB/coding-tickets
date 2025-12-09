package tickets.model;

import java.time.LocalDateTime;

public class Evenement {

    private long id;
    private String titre;
    private String description;
    private String lieu;
    private LocalDateTime dateEvenement;
    private int nbPlacesTotales;
    private int nbPlacesRestantes;
    private double prixBase;
    private Organisateur organisateur;

    // --- Constructeur complet (utilisé par JDBC) ---
    public Evenement(long id, String titre, String description, String lieu,
                     LocalDateTime dateEvenement, int nbPlacesTotales,
                     int nbPlacesRestantes, double prixBase, Organisateur organisateur) {

        this.id = id;
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.dateEvenement = dateEvenement;
        this.nbPlacesTotales = nbPlacesTotales;
        this.nbPlacesRestantes = nbPlacesRestantes;
        this.prixBase = prixBase;
        this.organisateur = organisateur;
    }

    // --- Constructeur SANS organisateur (utilisé côté DAO lors de jointure minimale) ---
    public Evenement(long id, String titre, String description, String lieu,
                     LocalDateTime dateEvenement, int nbPlacesTotales,
                     int nbPlacesRestantes, double prixBase) {

        this(id, titre, description, lieu, dateEvenement,
             nbPlacesTotales, nbPlacesRestantes, prixBase, null);
    }

    // --- Constructeur vide (obligatoire pour JSP/Servlets) ---
    public Evenement() {}

    // --- Getters / Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public LocalDateTime getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(LocalDateTime dateEvenement) { this.dateEvenement = dateEvenement; }

    public int getNbPlacesTotales() { return nbPlacesTotales; }
    public void setNbPlacesTotales(int nbPlacesTotales) { this.nbPlacesTotales = nbPlacesTotales; }

    public int getNbPlacesRestantes() { return nbPlacesRestantes; }
    public void setNbPlacesRestantes(int nbPlacesRestantes) { this.nbPlacesRestantes = nbPlacesRestantes; }

    public double getPrixBase() { return prixBase; }
    public void setPrixBase(double prixBase) { this.prixBase = prixBase; }

    public Organisateur getOrganisateur() { return organisateur; }
    public void setOrganisateur(Organisateur organisateur) { this.organisateur = organisateur; }
}
