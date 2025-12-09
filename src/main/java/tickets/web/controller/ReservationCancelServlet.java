package tickets.web.controller;

import tickets.model.Client;
import tickets.service.TicketService;
import tickets.service.ServiceException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reservations/cancel")
public class ReservationCancelServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
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
        try {
            long reservationId = Long.parseLong(req.getParameter("reservationId"));

            // --- On passe le client connecté pour la vérification ---
            service.annulerReservation(reservationId, client);

            session.setAttribute("msg", "Réservation annulée avec succès");

        } catch (NumberFormatException e) {
            session.setAttribute("msg", "Paramètres invalides");
        } catch (ServiceException e) {
            session.setAttribute("msg", "Erreur lors de l'annulation : " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/reservations/history");
    }
}
