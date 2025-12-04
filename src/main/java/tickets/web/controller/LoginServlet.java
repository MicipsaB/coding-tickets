package tickets.web.controller;
import tickets.model.Utilisateur;
import tickets.service.TicketService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h2>Login</h2>");
        String msg = (String) req.getSession().getAttribute("loginMsg");
        if (msg != null) {
            out.println("<p style='color:red;'>" + msg + "</p>");
            req.getSession().removeAttribute("loginMsg");
        }
        out.println("<form method='post' action='login'>");
        out.println("Email: <input name='email'/><br/>");
        out.println("Mot de passe: <input type='password' name='password'/><br/>");
        out.println("<button type='submit'>Se connecter</button>");
        out.println("</form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String pwd = req.getParameter("password");
        TicketService service = (TicketService) getServletContext().getAttribute("ticketService");
        Utilisateur u = service.authentifier(email, pwd);
        if (u != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", u);
            resp.sendRedirect(req.getContextPath() + "/events");
        } else {
            req.getSession().setAttribute("loginMsg", "Identifiants invalides");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}