package tickets.web.controller;

import tickets.model.Organisateur;
import tickets.model.Utilisateur;
import tickets.service.TicketService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@WebServlet("/events/create")
public class CreateEventServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h2>Créer un événement</h2>");
        out.println("<form method='post' action='" + req.getContextPath() + "/events/create'>");
        out.println("Titre: <input name='titre'/><br/>");
        out.println("Description: <input name='description'/><br/>");
        out.println("Date (ISO, ex: 2025-12-25T20:00): <input name='date'/><br/>");
        out.println("Lieu: <input name='lieu'/><br/>");
        out.println("Nombre de places: <input name='nbPlaces'/><br/>");
        out.println("Prix de base: <input name='prix'/><br/>");
        out.println("<button type='submit'>Créer</button>");
        out.println("</form>");
        out.println("<a href='" + req.getContextPath() + "/events'>Annuler</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Utilisateur u = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
        if (u == null || !u.isOrganisateur()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        try {
            String titre = req.getParameter("titre");
            String description = req.getParameter("description");
            String dateStr = req.getParameter("date");
            String lieu = req.getParameter("lieu");
            int nbPlaces = Integer.parseInt(req.getParameter("nbPlaces"));
            double prix = Double.parseDouble(req.getParameter("prix"));

            LocalDateTime date = LocalDateTime.parse(dateStr);

            // Cast en Organisateur (sûr grâce au contrôle ci-dessus)
            Organisateur org = (Organisateur) u;
            service.creerEvenement(org, titre, description, date, lieu, nbPlaces, prix);

            req.getSession().setAttribute("msg", "Événement créé avec succès");
            resp.sendRedirect(req.getContextPath() + "/events");
        } catch (NumberFormatException | DateTimeParseException ex) {
            req.getSession().setAttribute("msg", "Paramètres invalides : " + ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events/create");
        } catch (Exception ex) {
            req.getSession().setAttribute("msg", ex.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events/create");
        }
    }
}