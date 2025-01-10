package com.mycompany.task;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import java.util.concurrent.Callable;

public class UserProfileTask implements Callable<User> {
    private final String username;
    private final UserDAO userDAO;
    
    public UserProfileTask(String username, UserDAO userDAO) {
        this.username = username;
        this.userDAO = userDAO;
    }
    
    @Override
    public User call() {
        return userDAO.getUserByUsername(username);
    }
}