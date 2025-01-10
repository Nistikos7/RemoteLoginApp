package com.mycompany.rest;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    
    private final UserDAO userDAO = new UserDAO();
    
    @GET
    @Path("/test")
    public Response test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "API is working!");
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/{username}")
    public Response getUser(@PathParam("username") String username) {
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            // Αφαιρούμε τον κωδικό για ασφάλεια
            user.setPassword(null);
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                      .entity("User not found")
                      .build();
    }
    
    @POST
    @Path("/register")
    public Response registerUser(User user) {
        try {
            if (userDAO.usernameExists(user.getUsername())) {
                return Response.status(Response.Status.CONFLICT)
                             .entity("Username already exists")
                             .build();
            }
            if (userDAO.emailExists(user.getEmail())) {
                return Response.status(Response.Status.CONFLICT)
                             .entity("Email already exists")
                             .build();
            }
            
            boolean success = userDAO.registerUser(user);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User registered successfully");
                return Response.status(Response.Status.CREATED)
                             .entity(response)
                             .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity("Registration failed")
                             .build();
            }
        } catch (Exception e) {
            return Response.serverError()
                         .entity("Error: " + e.getMessage())
                         .build();
        }
    }
    
    @POST
    @Path("/login")
    public Response login(Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("Username and password are required")
                         .build();
        }
        
        try {
            boolean isValid = userDAO.validateUser(username, password);
            if (isValid) {
                User user = userDAO.getUserByUsername(username);
                user.setPassword(null); // Αφαιρούμε τον κωδικό για ασφάλεια
                
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("message", "Login successful");
                
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                             .entity("Invalid credentials")
                             .build();
            }
        } catch (Exception e) {
            return Response.serverError()
                         .entity("Error: " + e.getMessage())
                         .build();
        }
    }
    
    @PUT
    @Path("/{username}")
    public Response updateUser(@PathParam("username") String username, User user) {
        if (!username.equals(user.getUsername())) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("Username mismatch")
                         .build();
        }
        
        try {
            boolean success = userDAO.updateUser(user);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "User updated successfully");
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("User not found")
                             .build();
            }
        } catch (Exception e) {
            return Response.serverError()
                         .entity("Error: " + e.getMessage())
                         .build();
        }
    }
}