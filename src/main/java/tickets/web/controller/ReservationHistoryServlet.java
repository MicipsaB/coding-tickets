package tickets.web.controller;

import tickets.model.*;
import tickets.service.TicketService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/reservations/history")
public class ReservationHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        HttpSession s = req.getSession(false);
        Client client = (Client) s.getAttribute("user");
        if (client == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        List<Reservation> list = service.listerReservationsClient(client);
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h2>Mes réservations</h2>");
        out.println("<a href='" + req.getContextPath() + "/events'>Retour aux événements</a><br/>");
        out.println("<ul>");
        for (Reservation r : list) {
            out.println("<li>Réf: " + r.getId() + " - " + r.getEvenement().getTitre() + " - nb: " + r.getNbPlaces() + " - statut: " + r.getStatut());
            if (r.getStatut() == StatutReservation.CONFIRMEE) {
                out.println("<form method='post' action='" + req.getContextPath() + "/reservations/cancel' style='display:inline;'>");
                out.println("<input type='hidden' name='reservationId' value='" + r.getId() + "'/> ");
                out.println("<button type='submit'>Annuler</button>");
                out.println("</form>");
            }
            out.println("</li>");
        }
        out.println("</ul>");
        out.println("</body></html>");
    }
}