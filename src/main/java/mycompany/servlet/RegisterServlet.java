package com.mycompany.servlet;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import com.mycompany.task.RegisterTask;
import com.mycompany.util.DatabaseUtil;
import java.io.IOException;
import java.util.concurrent.Future;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    private boolean isValidPassword(String password) {
        if (password.length() < 5) {
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");

        try {
            // Check if username exists
            if (userDAO.usernameExists(username)) {
                request.setAttribute("error", "Username already exists. Please choose a different username.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Check if email exists
            if (userDAO.emailExists(email)) {
                request.setAttribute("error", "Email already registered. Please use a different email address.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Validation
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty() || 
                email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "All fields are required");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Email validation
            if (!isValidEmail(email)) {
                request.setAttribute("error", "Please enter a valid email address");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Password validation
            if (!isValidPassword(password)) {
                request.setAttribute("error", "Password must be at least 5 characters long and contain at least one number");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Password confirmation check
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Create user object
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            // Submit registration task
            Future<Boolean> future = DatabaseUtil.getThreadPool()
                    .submit(new RegisterTask(user, userDAO));

            if (future.get()) {
                // Successful registration
                LOGGER.log(Level.INFO, "User registered successfully: {0}", username);
                
                // Αυτόματο login
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("username", username);
                
                // Ανακατεύθυνση στο dashboard
                response.sendRedirect("dashboard.jsp");
            } else {
                // Failed registration
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during registration: " + e.getMessage(), e);
            request.setAttribute("error", "An error occurred. Please try again later.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during registration: " + e.getMessage(), e);
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}