package tickets.web.controller;

import tickets.model.Organisateur;
import tickets.model.Utilisateur;
import tickets.service.TicketService;
import tickets.service.ServiceException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("motDePasse");

        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");

        try {
            Utilisateur user = service.authentifier(email, password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);

                resp.sendRedirect(req.getContextPath() + "/events");
            } else {
            	req.getSession().setAttribute("msg", "Identifiants invalides");
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (ServiceException e) {
            req.getSession().setAttribute("loginMsg", "Erreur lors de l'authentification");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
