package com.mycompany.servlet;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import com.mycompany.task.RegisterTask;
import com.mycompany.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;
    private ExecutorService threadPool;
    
    @Override
    public void init() {
        userDAO = new UserDAO();
        threadPool = DatabaseUtil.getThreadPool();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        User user = new User(username, password, email, "user");
        
        try {
            // Δημιουργία task για ασύγχρονη επεξεργασία
            RegisterTask registerTask = new RegisterTask(user, userDAO);
            Future<Boolean> future = threadPool.submit(registerTask);
            
            // Περιμένουμε το αποτέλεσμα για μέγιστο 5 δευτερόλεπτα
            boolean isRegistered = future.get(5, TimeUnit.SECONDS);
            
            if (isRegistered) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Registration error: " + e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}