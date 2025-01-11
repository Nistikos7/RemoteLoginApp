package com.mycompany.task;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import java.util.concurrent.Callable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserProfileTask implements Callable<User> {
    private static final Logger LOGGER = Logger.getLogger(UserProfileTask.class.getName());
    private final String username;
    private final UserDAO userDAO;
    
    public UserProfileTask(String username, UserDAO userDAO) {
        this.username = username;
        this.userDAO = userDAO;
    }
    
    @Override
    public User call() {
        try {
            return userDAO.getUserByUsername(username);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user profile: " + e.getMessage(), e);
            return null;
        }
    }
}