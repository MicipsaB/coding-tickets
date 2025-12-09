package tickets.dao.jdbc;

import tickets.dao.*;
import tickets.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private final Connection connection;

    public JdbcReservationDao(Connection connection) {
        this.connection = connection;
    }

    // ====================================================
    // ==================== FIND BY ID =====================
    // ====================================================
    @Override
    public Reservation findById(long id) throws DaoException {

        String sql = """
            SELECT r.*, 
                   u.nom AS client_nom, u.email AS client_email, u.mot_de_passe AS client_pwd,
                   e.titre, e.description, e.lieu, e.date_evenement, e.nb_places_totales,
                   e.nb_places_restantes, e.prix_base, e.organisateur_id
            FROM reservation r
            JOIN utilisateur u ON r.client_id = u.id
            JOIN evenement e ON r.evenement_id = e.id
            WHERE r.id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new DaoException("Erreur findById (Reservation)", e);
        }
    }

    // ====================================================
    // ================= FIND BY CLIENT ===================
    // ====================================================
    @Override
    public List<Reservation> findByClient(Client client) throws DaoException {

        String sql = """
            SELECT r.*, 
                   e.titre, e.description, e.lieu, e.date_evenement,
                   e.nb_places_totales, e.nb_places_restantes,
                   e.prix_base, e.organisateur_id
            FROM reservation r
            JOIN evenement e ON r.evenement_id = e.id
            WHERE r.client_id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setLong(1, client.getId());
            List<Reservation> list = new ArrayList<>();

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowForClient(rs, client));
                }
            }

            return list;

        } catch (SQLException e) {
            throw new DaoException("Erreur findByClient", e);
        }
    }

    // ====================================================
    // ===================== CREATE ========================
    // ====================================================
    @Override
    public void create(Reservation r) throws DaoException {

        String sql = """
            INSERT INTO reservation(date_reservation, nb_places, montant_total, client_id, evenement_id, statut)
            VALUES (?, ?, ?, ?, ?, ?::statut_reservation)
        """;

        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setTimestamp(1, Timestamp.valueOf(r.getDateReservation()));
            st.setInt(2, r.getNbPlaces());
            st.setDouble(3, r.getMontantTotal());
            st.setLong(4, r.getClient().getId());
            st.setLong(5, r.getEvenement().getId());
            st.setString(6, r.getStatut().name());

            st.executeUpdate();

            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new DaoException("Erreur create (Reservation)", e);
        }
    }

    // ====================================================
    // ===================== UPDATE ========================
    // ====================================================
    @Override
    public void update(Reservation r) throws DaoException {

        String sql = """
            UPDATE reservation 
            SET date_reservation = ?,
                nb_places = ?,
                montant_total = ?,
                statut = ?::statut_reservation,
                client_id = ?,
                evenement_id = ?
            WHERE id = ?
        """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setTimestamp(1, Timestamp.valueOf(r.getDateReservation()));
            st.setInt(2, r.getNbPlaces());
            st.setDouble(3, r.getMontantTotal());
            st.setString(4, r.getStatut().name());
            st.setLong(5, r.getClient().getId());
            st.setLong(6, r.getEvenement().getId());
            st.setLong(7, r.getId());

            int rows = st.executeUpdate();
            if (rows == 0) {
                throw new DaoException("Aucune ligne mise à jour pour la réservation id=" + r.getId());
            }

        } catch (SQLException e) {
            throw new DaoException("Erreur update (Reservation)", e);
        }
    }

    // ====================================================
    // ===================== MAPPING =======================
    // ====================================================
    private Reservation mapRow(ResultSet rs) throws SQLException {

        Client client = new Client(
                rs.getLong("client_id"),
                rs.getString("client_nom"),
                rs.getString("client_email"),
                rs.getString("client_pwd")
        );

        Evenement ev = mapEvent(rs);

        Reservation r = new Reservation(
                rs.getLong("id"),
                rs.getTimestamp("date_reservation").toLocalDateTime(),
                rs.getInt("nb_places"),
                rs.getDouble("montant_total"),
                client,
                ev
        );

        // ✅ Chargement du statut depuis la base
        String statut = rs.getString("statut");
        if (statut != null) {
            r.setStatut(StatutReservation.valueOf(statut));
        } else {
            r.setStatut(StatutReservation.CONFIRMEE);
        }

        return r;
    }

    private Reservation mapRowForClient(ResultSet rs, Client client) throws SQLException {

        Evenement ev = mapEvent(rs);

        Reservation r = new Reservation(
                rs.getLong("id"),
                rs.getTimestamp("date_reservation").toLocalDateTime(),
                rs.getInt("nb_places"),
                rs.getDouble("montant_total"),
                client,
                ev
        );

        // ✅ Chargement du statut
        String statut = rs.getString("statut");
        if (statut != null) {
            r.setStatut(StatutReservation.valueOf(statut));
        } else {
            r.setStatut(StatutReservation.CONFIRMEE);
        }

        return r;
    }

    private Evenement mapEvent(ResultSet rs) throws SQLException {

        return new Evenement(
                rs.getLong("evenement_id"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getString("lieu"),
                rs.getTimestamp("date_evenement").toLocalDateTime(),
                rs.getInt("nb_places_totales"),
                rs.getInt("nb_places_restantes"),
                rs.getDouble("prix_base")
        );
    }
}
