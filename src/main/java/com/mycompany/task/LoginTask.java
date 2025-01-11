package com.mycompany.task;

import com.mycompany.dao.UserDAO;
import java.util.concurrent.Callable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginTask implements Callable<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(LoginTask.class.getName());
    private final String username;
    private final String password;
    private final UserDAO userDAO;
    
    public LoginTask(String username, String password, UserDAO userDAO) {
        this.username = username;
        this.password = password;
        this.userDAO = userDAO;
    }
    
    @Override
    public Boolean call() {
        try {
            return userDAO.validateUser(username, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during login validation: " + e.getMessage(), e);
            return false;
        }
    }
}