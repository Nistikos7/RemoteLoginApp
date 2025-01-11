package com.mycompany.task;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import java.util.concurrent.Callable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterTask implements Callable<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(RegisterTask.class.getName());
    private final User user;
    private final UserDAO userDAO;

    public RegisterTask(User user, UserDAO userDAO) {
        this.user = user;
        this.userDAO = userDAO;
    }

    @Override
    public Boolean call() {
        try {
            // Έλεγχος αν υπάρχει το username
            if (userDAO.usernameExists(user.getUsername())) {
                LOGGER.warning("Username already exists: " + user.getUsername());
                return false;
            }

            // Έλεγχος αν υπάρχει το email
            if (userDAO.emailExists(user.getEmail())) {
                LOGGER.warning("Email already exists: " + user.getEmail());
                return false;
            }

            // Εγγραφή του χρήστη
            boolean success = userDAO.registerUser(user);
            if (success) {
                LOGGER.info("User registered successfully: " + user.getUsername());
            } else {
                LOGGER.warning("Failed to register user: " + user.getUsername());
            }
            return success;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during registration: " + e.getMessage(), e);
            return false;
        }
    }
}