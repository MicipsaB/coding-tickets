package tickets.web.controller;

import tickets.model.Client;
import tickets.model.Reservation;
import tickets.service.TicketService;
import tickets.service.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/reservations/history")
public class ReservationHistoryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        
        // Vérification de la session
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        Client client = (Client) session.getAttribute("user");
        if (client == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        
        // Vérification que le service est initialisé
        if (service == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Service non disponible");
            return;
        }
        
        try {
            // Récupération des réservations du client
            List<Reservation> reservations = service.listerReservationsClient(client.getId());
            
            // Même si la liste est vide, on la passe à la JSP
            if (reservations == null) {
                reservations = List.of(); // Liste vide au lieu de null
            }
            
            req.setAttribute("reservations", reservations);
            req.getRequestDispatcher("/WEB-INF/jsp/reservations.jsp").forward(req, resp);
            
        } catch (ServiceException e) {
            // Logger l'erreur
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors de la récupération des réservations : " + e.getMessage());
            
        } catch (Exception e) {
            // Erreur inattendue
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur technique : " + e.getMessage());
        }
    }
}