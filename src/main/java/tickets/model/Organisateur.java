package tickets.model;

public class Organisateur extends Utilisateur {
public Organisateur() { super(); }
public Organisateur(long id, String nom, String email, String motDePasse) {
super(id, nom, email, motDePasse, Role.ORGANISATEUR);
}
}