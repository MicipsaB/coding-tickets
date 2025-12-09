package tickets.dao;

import tickets.model.*;
import java.util.List;

public interface EvenementDao {

    List<Evenement> findAll() throws DaoException;

    Evenement findById(long id) throws DaoException;

    void create(Evenement e) throws DaoException;

    void update(Evenement e) throws DaoException;

    List<Evenement> findByOrganisateur(Organisateur org) throws DaoException;
}
