package mycompany.task;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
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
        User user = userDAO.validateUser(username, password);
        return user != null;
    }
}