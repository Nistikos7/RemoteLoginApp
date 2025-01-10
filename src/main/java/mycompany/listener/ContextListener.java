
package mycompany.listener;


import com.mycompany.util.DatabaseUtil;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Το thread pool δημιουργείται αυτόματα
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Καθαρίστε τους πόρους
        DatabaseUtil.shutdownThreadPool();
        
        // Σταματήστε το AbandonedConnectionCleanupThread της MySQL
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}