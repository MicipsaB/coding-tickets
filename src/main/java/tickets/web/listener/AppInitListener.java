package tickets.web.listener;

import tickets.service.TicketService;
import tickets.dao.jdbc.*;
import tickets.dao.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");

            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // Fallback si tu lances hors Docker
            if (url == null) {
                url = "jdbc:postgresql://localhost:5432/ticketing";
                user = "postgres";
                password = "password";
            }

            System.out.println("🔄 Configuration DB:");
            System.out.println("   URL: " + url);
            System.out.println("   User: " + user);

            // Retry logic robuste
            Connection conn = connectWithRetry(url, user, password, 20, 3000);

            UtilisateurDao utilisateurDao = new JdbcUtilisateurDao(conn);
            EvenementDao evenementDao = new JdbcEvenementDao(conn);
            ReservationDao reservationDao = new JdbcReservationDao(conn);

            TicketService ticketService =
                    new TicketService(evenementDao, reservationDao, utilisateurDao);

            ServletContext context = sce.getServletContext();
            context.setAttribute("ticketService", ticketService);

            System.out.println("✅ Application initialisée avec succès");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver PostgreSQL non trouvé");
            e.printStackTrace();
            throw new RuntimeException("Driver PostgreSQL manquant", e);
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation application: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur initialisation application", e);
        }
    }

    private Connection connectWithRetry(String url, String user, String password, 
                                        int maxRetries, int delayMs) throws SQLException {
        Connection conn = null;
        int attempt = 0;
        SQLException lastException = null;

        while (conn == null && attempt < maxRetries) {
            attempt++;
            try {
                System.out.println("🔄 Tentative de connexion " + attempt + "/" + maxRetries + "...");
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("✅ Connexion établie à la base de données");
                return conn;
            } catch (SQLException e) {
                lastException = e;
                System.err.println("⚠️  Échec connexion (tentative " + attempt + "): " + e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        System.out.println("⏳ Attente de " + delayMs + "ms avant nouvelle tentative...");
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connexion interrompue", ie);
                    }
                }
            }
        }

        throw new SQLException("Impossible de se connecter après " + maxRetries + " tentatives", lastException);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Application arrêtée");
    }
}