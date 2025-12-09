package tickets.dao;


import tickets.model.*;
import java.util.List;

public interface UtilisateurDao {

    Utilisateur findById(long id) throws DaoException;

    Utilisateur findByEmail(String email) throws DaoException;

    List<Utilisateur> findAll() throws DaoException;

    void create(Utilisateur u) throws DaoException;

    void update(Utilisateur u) throws DaoException;

    void delete(long id) throws DaoException;
}
