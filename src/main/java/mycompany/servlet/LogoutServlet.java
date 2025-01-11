package mycompany.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Παίρνουμε το τρέχον session
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                // Καταγράφουμε ποιος κάνει logout
                String username = (String) session.getAttribute("username");
                LOGGER.log(Level.INFO, "User logged out: {0}", username);
                
                // Καταστρέφουμε το session
                session.invalidate();
            }
            
            // Ανακατεύθυνση στη σελίδα login
            response.sendRedirect("login.jsp");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during logout: " + e.getMessage(), e);
            response.sendRedirect("error.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}