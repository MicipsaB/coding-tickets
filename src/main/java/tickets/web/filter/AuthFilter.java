package tickets.web.filter;

import tickets.model.Utilisateur;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        // ✅ URLs publiques - Ajoutez /styles/ ici
        if (path.startsWith("/login") || 
            path.startsWith("/static") || 
            path.startsWith("/resources") ||
            path.startsWith("/styles/") ||          // ← Ajouté
            path.endsWith(".css") ||                // ← Ajouté pour sécurité
            path.endsWith(".js") ||                 // ← Ajouté pour JS
            path.endsWith(".png") ||                // ← Ajouté pour images
            path.endsWith(".jpg") ||
            path.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = req.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
        
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        chain.doFilter(request, response);
    }
}