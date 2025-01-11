/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycompany.task;

import com.mycompany.dao.UserDAO;
import java.sql.SQLException;
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
    public Boolean call() throws SQLException {
        return userDAO.validateUser(username, password);
    }
}