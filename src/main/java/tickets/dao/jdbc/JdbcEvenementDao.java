package tickets.dao.jdbc;

import tickets.dao.*;
import tickets.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcEvenementDao implements EvenementDao {

    private final Connection connection;

    public JdbcEvenementDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Evenement> findAll() throws DaoException {
        String sql = "SELECT * FROM evenement";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            List<Evenement> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DaoException("Erreur findAll Evenement", e);
        }
    }

    @Override
    public Evenement findById(long id) throws DaoException {
        String sql = "SELECT * FROM evenement WHERE id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findById Evenement", e);
        }
    }

    @Override
    public void create(Evenement e) throws DaoException {
        String sql = "INSERT INTO evenement(titre, description, lieu, date_evenement, nb_places_totales, nb_places_restantes, prix_base, organisateur_id) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, e.getTitre());
            st.setString(2, e.getDescription());
            st.setString(3, e.getLieu());
            st.setTimestamp(4, Timestamp.valueOf(e.getDateEvenement()));
            st.setInt(5, e.getNbPlacesTotales());
            st.setInt(6, e.getNbPlacesRestantes());
            st.setDouble(7, e.getPrixBase());
            st.setLong(8, e.getOrganisateur().getId());
            st.executeUpdate();

            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getLong(1));
            }

        } catch (SQLException ex) {
            throw new DaoException("Erreur create Evenement", ex);
        }
    }

    @Override
    public void update(Evenement e) throws DaoException {
        String sql = "UPDATE evenement SET titre=?, description=?, lieu=?, date_evenement=?, nb_places_totales=?, nb_places_restantes=?, prix_base=? WHERE id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, e.getTitre());
            st.setString(2, e.getDescription());
            st.setString(3, e.getLieu());
            st.setTimestamp(4, Timestamp.valueOf(e.getDateEvenement()));
            st.setInt(5, e.getNbPlacesTotales());
            st.setInt(6, e.getNbPlacesRestantes());
            st.setDouble(7, e.getPrixBase());
            st.setLong(8, e.getId());
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Erreur update Evenement", ex);
        }
    }

    @Override
    public List<Evenement> findByOrganisateur(Organisateur org) throws DaoException {
        String sql = "SELECT * FROM evenement WHERE organisateur_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, org.getId());
            try (ResultSet rs = st.executeQuery()) {
                List<Evenement> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findByOrganisateur Evenement", e);
        }
    }

    // ---------- Mapping ---------- //
    private Evenement mapRow(ResultSet rs) throws SQLException {
        Evenement e = new Evenement();
        e.setId(rs.getLong("id"));
        e.setTitre(rs.getString("titre"));
        e.setDescription(rs.getString("description"));
        e.setLieu(rs.getString("lieu"));
        e.setDateEvenement(rs.getTimestamp("date_evenement").toLocalDateTime());
        e.setNbPlacesTotales(rs.getInt("nb_places_totales"));
        e.setNbPlacesRestantes(rs.getInt("nb_places_restantes"));
        e.setPrixBase(rs.getDouble("prix_base"));

        long orgId = rs.getLong("organisateur_id");
        if (orgId > 0) {
            Organisateur org = new Organisateur();
            org.setId(orgId);
            e.setOrganisateur(org);
        }

        return e;
    }
}
