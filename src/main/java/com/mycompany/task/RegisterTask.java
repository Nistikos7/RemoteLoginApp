package com.mycompany.task;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import java.util.concurrent.Callable;

public class RegisterTask implements Callable<Boolean> {
    private final User user;
    private final UserDAO userDAO;
    
    public RegisterTask(User user, UserDAO userDAO) {
        this.user = user;
        this.userDAO = userDAO;
    }
    
    @Override
    public Boolean call() {
        // Έλεγχος αν υπάρχει ήδη το username ή email
        if (userDAO.usernameExists(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userDAO.emailExists(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        return userDAO.registerUser(user);
    }
}