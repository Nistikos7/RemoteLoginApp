package com.mycompany.servlet;

import com.mycompany.dao.UserDAO;
import com.mycompany.task.LoginTask;
import com.mycompany.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
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
        
        try {
            // Δημιουργία task για ασύγχρονη επεξεργασία
            LoginTask loginTask = new LoginTask(username, password, userDAO);
            Future<Boolean> future = threadPool.submit(loginTask);
            
            // Περιμένουμε το αποτέλεσμα για μέγιστο 5 δευτερόλεπτα
            boolean isValid = future.get(5, TimeUnit.SECONDS);
            
            if (isValid) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("dashboard.jsp");
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Server error: " + e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    @Override
    public void destroy() {
        // Το thread pool θα κλείσει όταν κλείσει ο server
        DatabaseUtil.shutdownThreadPool();
    }
}