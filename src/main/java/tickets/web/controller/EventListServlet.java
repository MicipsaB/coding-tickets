package tickets.web.controller;

import tickets.model.Evenement;
import tickets.model.Utilisateur;
import tickets.service.TicketService;
import tickets.service.ServiceException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/events")
public class EventListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");

        HttpSession session = req.getSession(false);
        Utilisateur u = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        try {
            List<Evenement> events;

            boolean isOrganisateur = u != null && u.isOrganisateur();
            req.setAttribute("isOrganisateur", isOrganisateur);

            // Organisateur : voir uniquement ses événements
            if (isOrganisateur) {
                events = service.listerEvenementsOrganisateur(u.getId());
            } else {
                // Client ou non connecté : tous les événements
                events = service.listerEvenements();
            }

            req.setAttribute("events", events);

            // Message flash
            if (session != null) {
                String msg = (String) session.getAttribute("msg");
                if (msg != null) {
                    req.setAttribute("msg", msg);
                    session.removeAttribute("msg");
                }
            }

            req.getRequestDispatcher("/WEB-INF/jsp/events.jsp").forward(req, resp);

        } catch (ServiceException e) {
            resp.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur récupération événements : " + e.getMessage()
            );
        }
    }
}
