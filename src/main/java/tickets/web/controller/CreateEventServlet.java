package tickets.web.controller;

import tickets.model.Evenement;
import tickets.model.Organisateur;
import tickets.model.Utilisateur;
import tickets.service.TicketService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@WebServlet("/events/create")
public class CreateEventServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession(false);
        Utilisateur u = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (u == null || !u.isOrganisateur()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        Organisateur org = (Organisateur) u;

        String id = req.getParameter("id");
        if (id != null && !id.isEmpty()) {
            try {
                Evenement ev = service.getEvenement(Long.parseLong(id));
                // 🔒 Sécurité : un organisateur ne peut modifier que ses propres événements
                if (ev.getOrganisateur().getId() != org.getId()) {
                    resp.sendRedirect(req.getContextPath() + "/events");
                    return;
                }
                req.setAttribute("event", ev);
            } catch (Exception ignored) {}
        }

        req.getRequestDispatcher("/WEB-INF/jsp/event-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(false);
        Utilisateur u = (session != null) ? (Utilisateur) session.getAttribute("user") : null;

        if (u == null || !u.isOrganisateur()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Organisateur org = (Organisateur) u;
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");

        try {
            String titre = req.getParameter("titre");
            String description = req.getParameter("description");
            String dateStr = req.getParameter("date");
            String lieu = req.getParameter("lieu");
            int nbPlaces = Integer.parseInt(req.getParameter("nbPlaces"));
            double prix = Double.parseDouble(req.getParameter("prix"));

            LocalDateTime date = LocalDateTime.parse(dateStr);

            Evenement ev = new Evenement(
                    0,
                    titre,
                    description,
                    lieu,
                    date,
                    nbPlaces,
                    nbPlaces,
                    prix
            );

            String id = req.getParameter("id");

            if (id == null || id.isEmpty()) {
                // ✅ Création
                service.creerEvenement(ev, org.getId());
                session.setAttribute("msg", "✅ Événement créé avec succès");
            } else {
                // 🔒 Sécurité : vérifier l'organisateur
                long eventId = Long.parseLong(id);
                Evenement original = service.getEvenement(eventId);
                if (original.getOrganisateur().getId() != org.getId()) {
                    session.setAttribute("msg", "❌ Vous ne pouvez pas modifier cet événement");
                    resp.sendRedirect(req.getContextPath() + "/events");
                    return;
                }

                ev.setId(eventId);
                service.modifierEvenement(ev, org.getId());
                session.setAttribute("msg", "✅ Événement modifié avec succès");
            }

            resp.sendRedirect(req.getContextPath() + "/events");

        } catch (NumberFormatException | DateTimeParseException e) {
            session.setAttribute("msg", "❌ Paramètres invalides : " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events/create");
        } catch (Exception e) {
            session.setAttribute("msg", "❌ Erreur : " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/events/create");
        }
    }
}
