package tickets.dao;


import tickets.model.*;
import java.util.List;

public interface ReservationDao {
    Reservation findById(long id) throws DaoException;
    List<Reservation> findByClient(Client client) throws DaoException;
    void create(Reservation reservation) throws DaoException;
    void update(Reservation reservation) throws DaoException;
}
