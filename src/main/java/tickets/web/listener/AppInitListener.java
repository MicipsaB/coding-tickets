package tickets.web.listener;
import tickets.service.TicketService;
import tickets.model.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDateTime;

@WebListener
public class AppInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        TicketService ticketService = new TicketService();

        // Création d'utilisateurs de test
        Client c1 = new Client(0, "batman", "batman@mail.com", "passe");
        Client c2 = new Client(0, "spiderman", "spiderman@mail.com", "passe");
        Client c3 = new Client(0, "flash", "flash@mail.com", "passe");
        Client c4 = new Client(0, "joker", "joker@mail.com", "passe");
        
        Organisateur o1 = new Organisateur(0, "orga1", "orga1@mail.com", "admin");
        Organisateur o2 = new Organisateur(0, "orga2", "orga2@mail.com", "admin");
        
        ticketService.creerUtilisateur(c1);
        ticketService.creerUtilisateur(c2);
        ticketService.creerUtilisateur(c3);
        ticketService.creerUtilisateur(c4);
        ticketService.creerUtilisateur(o1);

        // Création d'événements de test
        ticketService.creerEvenement(o1, "Concert de blues", "soirée de blues", LocalDateTime.now().plusDays(10), "Zénith", 100, 20.0);
        ticketService.creerEvenement(o1, "Conférence", "Conférence de presse", LocalDateTime.now().plusDays(5), "Villette", 50, 0.0);

        ServletContext context = sce.getServletContext();
        context.setAttribute("ticketService", ticketService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // rien
    }
}