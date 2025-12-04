package tickets.web.controller;

import tickets.model.Evenement;
import tickets.model.Utilisateur;
import tickets.service.TicketService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/events")
public class EventListServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
		List<Evenement> events = service.listerEvenements();

		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<html><body>");
		out.println("<h2>Liste des événements</h2>");
		out.println("<a href='" + req.getContextPath() + "/logout'>Se déconnecter</a><br/><br/>");

		// Message éventuel
		HttpSession session = req.getSession(false);
		if (session != null) {
			String msg = (String) session.getAttribute("msg");
			if (msg != null) {
				out.println("<p style='color:green;'>" + msg + "</p>");
				session.removeAttribute("msg");
			}
		}

		// Si l'utilisateur est organisateur, proposer un lien pour créer un événement
		Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
		if (user != null && user.isOrganisateur()) {
			out.println("<a href='" + req.getContextPath() + "/events/create'>Créer un événement</a><br/><br/>");
		}

		out.println("<ul>");
		for (Evenement e : events) {
			out.println("<li>");
			out.println("<b>" + e.getTitre() + "</b> - " + e.getLieu() + " - " + e.getDate() + " - places restantes: "
					+ e.getNbPlacesRestantes());
			out.println("<form method='post' action='" + req.getContextPath()
					+ "/reservations/create' style='display:inline;'>");
			out.println("<input type='hidden' name='eventId' value='" + e.getId() + "' />");
			if (user != null && user.isClient()) {
				out.println("<input name='nbPlaces' size='2' value='1'/> ");
				out.println("<button type='submit'>Réserver</button>");
			}

			out.println("</form>");
			out.println("</li>");
		}
		out.println("</ul>");
		if (user != null && user.isClient()){
			out.println("<a href='" + req.getContextPath() + "/reservations/history'>Mes réservations</a>");
		}
		
		out.println("</body></html>");
	}
}
