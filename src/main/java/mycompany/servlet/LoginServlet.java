package com.mycompany.servlet;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import com.mycompany.task.LoginTask;
import com.mycompany.util.DatabaseUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username and password are required");
            return;
        }

        try {
            Future<Boolean> future = DatabaseUtil.getThreadPool()
                    .submit(new LoginTask(username, password, userDAO));

            if (future.get()) {
                // Επιτυχής σύνδεση
                User user = userDAO.getUserByUsername(username);
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("username", username);
                
                LOGGER.log(Level.INFO, "User {0} logged in successfully", username);
                response.sendRedirect("dashboard.jsp");
            } else {
                // Αποτυχημένη σύνδεση
                LOGGER.log(Level.WARNING, "Failed login attempt for user: {0}", username);
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Login process interrupted", e);
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login process interrupted");
        } catch (ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Error during login execution", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during login");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during login", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}