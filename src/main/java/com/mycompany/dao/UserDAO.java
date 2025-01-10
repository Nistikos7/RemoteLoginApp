package com.mycompany.dao;

import com.mycompany.model.User;
import com.mycompany.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    
    public boolean validateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("Attempting login for user: " + username); // debug
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                System.out.println("Stored password: " + storedPassword); // debug
                System.out.println("Input password: " + password); // debug
                
                // Αν ο κωδικός δεν είναι κρυπτογραφημένος
                if (!storedPassword.startsWith("$2a$")) {
                    System.out.println("Password not hashed, comparing directly"); // debug
                    return password.equals(storedPassword);
                }
                
                System.out.println("Comparing with BCrypt"); // debug
                boolean result = BCrypt.checkpw(password, storedPassword);
                System.out.println("BCrypt comparison result: " + result); // debug
                return result;
            }
            System.out.println("User not found"); // debug
            return false;
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage()); // debug
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("Registering new user: " + user.getUsername()); // debug
            
            // Κρυπτογράφηση του κωδικού
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            System.out.println("Hashed password: " + hashedPassword); // debug
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, "user"); // Default role
            
            int result = pstmt.executeUpdate();
            System.out.println("Registration result: " + result); // debug
            return result > 0;
            
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage()); // debug
            e.printStackTrace();
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Username " + username + " exists: " + (count > 0)); // debug
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Username check error: " + e.getMessage()); // debug
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Email " + email + " exists: " + (count > 0)); // debug
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Email check error: " + e.getMessage()); // debug
            e.printStackTrace();
            return false;
        }
    }
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                System.out.println("Found user: " + username); // debug
                return user;
            }
            System.out.println("User not found: " + username); // debug
            return null;
        } catch (SQLException e) {
            System.out.println("Get user error: " + e.getMessage()); // debug
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ? WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getUsername());
            
            int result = pstmt.executeUpdate();
            System.out.println("Update user result: " + result); // debug
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Update user error: " + e.getMessage()); // debug
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            System.out.println("New hashed password for " + username + ": " + hashedPassword); // debug
            
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, username);
            
            int result = pstmt.executeUpdate();
            System.out.println("Password update result: " + result); // debug
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Password update error: " + e.getMessage()); // debug
            e.printStackTrace();
            return false;
        }
    }
}