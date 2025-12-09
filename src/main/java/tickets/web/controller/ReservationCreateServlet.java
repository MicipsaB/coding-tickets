package tickets.web.controller;

import tickets.model.Client;
import tickets.model.Reservation;
import tickets.service.TicketService;
import tickets.service.ServiceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reservations/create")
public class ReservationCreateServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        
        // Vérification de la session
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Vérification que l'utilisateur connecté est un Client
        Client client = (Client) session.getAttribute("user");
        if (client == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        
        // Vérification que le service est initialisé
        if (service == null) {
            session.setAttribute("msg", "Erreur : Service non disponible");
            resp.sendRedirect(req.getContextPath() + "/events");
            return;
        }
        
        try {
            // Récupération et validation des paramètres
            String eventIdParam = req.getParameter("eventId");
            String nbPlacesParam = req.getParameter("nbPlaces");
            
            if (eventIdParam == null || nbPlacesParam == null) {
                session.setAttribute("msg", "Erreur : Paramètres manquants");
                resp.sendRedirect(req.getContextPath() + "/events");
                return;
            }
            
            long eventId = Long.parseLong(eventIdParam);
            int nbPlaces = Integer.parseInt(nbPlacesParam);
            
            // Validation métier
            if (nbPlaces <= 0) {
                session.setAttribute("msg", "Erreur : Le nombre de places doit être supérieur à 0");
                resp.sendRedirect(req.getContextPath() + "/events");
                return;
            }
            
            // Réservation
            Reservation reservation = service.reserver(client.getId(), eventId, nbPlaces);
            
            session.setAttribute("msg", "Réservation effectuée avec succès ! Réservation N°" + reservation.getId());
            resp.sendRedirect(req.getContextPath() + "/reservations/history");
            
        } catch (NumberFormatException e) {
            session.setAttribute("msg", "Erreur : Format de paramètre invalide");
            resp.sendRedirect(req.getContextPath() + "/events");
            
        } catch (ServiceException e) {
            // Afficher le message d'erreur métier détaillé
            session.setAttribute("msg", "Erreur lors de la réservation : " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events");
            
        } catch (Exception e) {
            // Logger l'erreur
            e.printStackTrace();
            session.setAttribute("msg", "Erreur technique : " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events");
        }
    }
}