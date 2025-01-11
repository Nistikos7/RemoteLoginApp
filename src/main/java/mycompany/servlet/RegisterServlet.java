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
import java.util.logging.Level;
import java.util.logging.Logger;

// ... υπάρχοντα imports ...

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    // Μέθοδος ελέγχου εγκυρότητας κωδικού
    private boolean isValidPassword(String password) {
        // Τουλάχιστον 5 χαρακτήρες
        if (password.length() < 5) {
            return false;
        }
        
        // Τουλάχιστον 1 αριθμό
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");

        // Validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required");
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

        // ... υπόλοιπος κώδικας ...
    }
}