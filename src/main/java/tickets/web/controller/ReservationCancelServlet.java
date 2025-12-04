package tickets.web.controller;

import tickets.model.*;
import tickets.service.TicketService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reservations/cancel")
public class ReservationCancelServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        HttpSession s = req.getSession(false);
        Client client = (Client) s.getAttribute("user");
        if (client == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            long id = Long.parseLong(req.getParameter("reservationId"));
            service.annulerReservation(id, client);
            resp.sendRedirect(req.getContextPath() + "/reservations/history");
        } catch (NumberFormatException ex) {
            req.getSession().setAttribute("msg", "Paramètres invalides");
            resp.sendRedirect(req.getContextPath() + "/reservations/history");
        } catch (AnnulationTardiveException ate) {
            req.getSession().setAttribute("msg", ate.getMessage());
            resp.sendRedirect(req.getContextPath() + "/reservations/history");
        } catch (Exception ex) {
            req.getSession().setAttribute("msg", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/reservations/history");
        }
    }
}