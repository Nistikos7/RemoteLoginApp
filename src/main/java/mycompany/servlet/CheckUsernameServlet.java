package mycompany.servlet;

import com.mycompany.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/checkUsername")
public class CheckUsernameServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CheckUsernameServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        response.setContentType("application/json");
        
        try {
            boolean exists = userDAO.usernameExists(username);
            response.getWriter().write(String.valueOf(exists));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking username: " + e.getMessage(), e);
            response.getWriter().write("false");
        }
    }
}