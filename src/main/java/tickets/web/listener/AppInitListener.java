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

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");

            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            // ✅ fallback si tu lances hors Docker
            if (url == null) {
                url = "jdbc:postgresql://localhost:5432/ticketing";
                user = "postgres";
                password = "password";
            }

            Connection conn = DriverManager.getConnection(url, user, password);

            UtilisateurDao utilisateurDao = new JdbcUtilisateurDao(conn);
            EvenementDao evenementDao = new JdbcEvenementDao(conn);
            ReservationDao reservationDao = new JdbcReservationDao(conn);

            TicketService ticketService =
                    new TicketService(evenementDao, reservationDao, utilisateurDao);

            ServletContext context = sce.getServletContext();
            context.setAttribute("ticketService", ticketService);

            System.out.println("✅ Application initialisée avec succès");

        } catch (Exception e) {
            throw new RuntimeException("❌ Erreur initialisation application", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Application arrêtée");
    }
}
