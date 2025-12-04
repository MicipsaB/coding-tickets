package tickets.web.controller;

import tickets.model.*;
import tickets.service.TicketService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reservations/create")
public class ReservationCreateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        HttpSession s = req.getSession(false);
        Client client = (Client) s.getAttribute("user");
        if (client == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            long eventId = Long.parseLong(req.getParameter("eventId"));
            int nb = Integer.parseInt(req.getParameter("nbPlaces"));
            service.reserver(client, eventId, nb);
            resp.sendRedirect(req.getContextPath() + "/reservations/history");
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("msg", "Paramètres invalides");
            resp.sendRedirect(req.getContextPath() + "/events");
        } catch (PlacesInsuffisantesException pie) {
            req.getSession().setAttribute("msg", pie.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events");
        } catch (Exception ex) {
            req.getSession().setAttribute("msg", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events");
        }
    }
}
