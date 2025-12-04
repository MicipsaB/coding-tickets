package tickets.model;

public class Client extends Utilisateur {
public Client() { super(); }
public Client(long id, String nom, String email, String motDePasse) {
super(id, nom, email, motDePasse, Role.CLIENT);
}
}