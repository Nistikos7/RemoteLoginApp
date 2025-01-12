package com.mycompany.servlet;

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

@WebServlet("/checkEmail")
public class CheckEmailServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CheckEmailServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        response.setContentType("application/json");
        
        try {
            boolean exists = userDAO.emailExists(email);
            response.getWriter().write(String.valueOf(exists));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email: " + e.getMessage(), e);
            response.getWriter().write("false");
        }
    }
}