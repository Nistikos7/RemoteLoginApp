package com.mycompany.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {
    // Χρήση environment variables με fallback σε local values
    private static final String URL = System.getenv("DATABASE_URL") != null ? 
            System.getenv("DATABASE_URL") : 
            "jdbc:postgresql://localhost:5432/remote_login";
            
    private static final String USER = System.getenv("DATABASE_USER") != null ? 
            System.getenv("DATABASE_USER") : 
            "postgres";
            
    private static final String PASSWORD = System.getenv("DATABASE_PASSWORD") != null ? 
            System.getenv("DATABASE_PASSWORD") : 
            "makaronia999";
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    
    private static final ThreadPoolExecutor threadPool = 
        (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    
    private static final ThreadMonitor monitor = 
        new ThreadMonitor(threadPool, 60); // Έλεγχος κάθε 60 δευτερόλεπτα
    
    static {
        // Εκκίνηση του thread monitor
        Thread monitorThread = new Thread(monitor);
        monitorThread.setDaemon(true);
        monitorThread.start();
        
        // Φόρτωση του PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
            LOGGER.info("PostgreSQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Error loading PostgreSQL JDBC driver: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            LOGGER.info("Attempting database connection...");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error: " + e.getMessage(), e);
            throw new SQLException("Database connection error: " + e.getMessage());
        }
    }
    
    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
    
    public static void shutdownThreadPool() {
        LOGGER.info("Initiating shutdown of thread pool and resources...");
        
        // Σταματήστε πρώτα το monitor
        if (monitor != null) {
            LOGGER.info("Shutting down thread monitor...");
            monitor.shutdown();
            try {
                Thread.sleep(100); // Δώστε χρόνο στο monitor να σταματήσει
            } catch (InterruptedException e) {
                LOGGER.warning("Thread monitor shutdown interrupted");
                Thread.currentThread().interrupt();
            }
        }
        
        // Μετά σταματήστε το thread pool
        if (threadPool != null && !threadPool.isShutdown()) {
            LOGGER.info("Shutting down thread pool...");
            threadPool.shutdown();
            try {
                // Περιμένετε μέχρι 60 δευτερόλεπτα για να ολοκληρωθούν τα τρέχοντα tasks
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    LOGGER.warning("Thread pool did not terminate in time, forcing shutdown...");
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.warning("Thread pool shutdown interrupted, forcing immediate shutdown");
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // Καθαρίστε τους JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                LOGGER.info("Deregistering JDBC driver: " + driver);
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deregistering JDBC driver: " + e.getMessage(), e);
            }
        }
        
        LOGGER.info("Shutdown completed");
    }
    
    // Μέθοδος για έλεγχο της κατάστασης του thread pool
    public static String getThreadPoolStatus() {
        return String.format(
            "Thread Pool Status: [Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s]",
            threadPool.getActiveCount(),
            threadPool.getCompletedTaskCount(),
            threadPool.getTaskCount(),
            threadPool.isShutdown(),
            threadPool.isTerminated()
        );
    }
}