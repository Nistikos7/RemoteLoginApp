package com.mycompany.task;

import com.mycompany.dao.UserDAO;
import java.util.concurrent.Callable;

public class LoginTask implements Callable<Boolean> {
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
        return userDAO.validateUser(username, password);
    }
}