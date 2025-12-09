package tickets.dao.jdbc;

import tickets.dao.*;
import tickets.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUtilisateurDao implements UtilisateurDao {

    private final Connection connection;

    public JdbcUtilisateurDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Utilisateur findById(long id) throws DaoException {
        String sql = "SELECT * FROM utilisateur WHERE id=?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new DaoException("Erreur findById utilisateur", e);
        }
    }

    @Override
    public Utilisateur findByEmail(String email) throws DaoException {
        String sql = "SELECT * FROM utilisateur WHERE email=?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, email);

            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (SQLException e) {
            throw new DaoException("Erreur findByEmail utilisateur", e);
        }
    }

    @Override
    public List<Utilisateur> findAll() throws DaoException {
        String sql = "SELECT * FROM utilisateur";

        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            List<Utilisateur> list = new ArrayList<>();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DaoException("Erreur findAll utilisateur", e);
        }
    }

    @Override
    public void create(Utilisateur u) throws DaoException {
        String sql = "INSERT INTO utilisateur(nom, email, mot_de_passe, role) VALUES(?,?,?,?)";

        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, u.getNom());
            st.setString(2, u.getEmail());
            st.setString(3, u.getMotDePasse());

            if (u instanceof Client) st.setString(4, "CLIENT");
            else if (u instanceof Organisateur) st.setString(4, "ORGANISATEUR");
            else throw new DaoException("Type utilisateur inconnu");

            st.executeUpdate();

            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new DaoException("Erreur create utilisateur", e);
        }
    }

    @Override
    public void update(Utilisateur u) throws DaoException {
        String sql = "UPDATE utilisateur SET nom=?, email=?, mot_de_passe=?, role=? WHERE id=?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, u.getNom());
            st.setString(2, u.getEmail());
            st.setString(3, u.getMotDePasse());

            if (u instanceof Client) st.setString(4, "CLIENT");
            else if (u instanceof Organisateur) st.setString(4, "ORGANISATEUR");
            else throw new DaoException("Type utilisateur inconnu");

            st.setLong(5, u.getId());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("Erreur update utilisateur", e);
        }
    }

    @Override
    public void delete(long id) throws DaoException {
        String sql = "DELETE FROM utilisateur WHERE id=?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erreur delete utilisateur", e);
        }
    }

    // ---------- MAPPING ---------- //

    private Utilisateur mapRow(ResultSet rs) throws SQLException {

        String role = rs.getString("role");

        if ("CLIENT".equalsIgnoreCase(role)) {
            Client client = new Client();
            client.setId(rs.getLong("id"));
            client.setNom(rs.getString("nom"));
            client.setEmail(rs.getString("email"));
            client.setMotDePasse(rs.getString("mot_de_passe"));
            client.setRole(Role.CLIENT);
            return client;
        }

        if ("ORGANISATEUR".equalsIgnoreCase(role)) {
            Organisateur orga = new Organisateur();
            orga.setId(rs.getLong("id"));
            orga.setNom(rs.getString("nom"));
            orga.setEmail(rs.getString("email"));
            orga.setMotDePasse(rs.getString("mot_de_passe"));
            orga.setRole(Role.ORGANISATEUR);
            return orga;
        }

        throw new SQLException("Rôle utilisateur inconnu : " + role);
    }
}
