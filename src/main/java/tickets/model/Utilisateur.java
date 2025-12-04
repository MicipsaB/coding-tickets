package tickets.model;

public class Utilisateur {
private long id;
private String nom;
private String email;
private String motDePasse;
private Role role;


public Utilisateur() {}


public Utilisateur(long id, String nom, String email, String motDePasse, Role role) {
this.id = id;
this.nom = nom;
this.email = email;
this.motDePasse = motDePasse;
this.role = role;
}


public long getId() { return id; }
public void setId(long id) { this.id = id; }
public String getNom() { return nom; }
public void setNom(String nom) { this.nom = nom; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getMotDePasse() { return motDePasse; }
public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
public Role getRole() { return role; }
public void setRole(Role role) { this.role = role; }


public boolean isClient() { return Role.CLIENT == role; }
public boolean isOrganisateur() { return Role.ORGANISATEUR == role; }
}