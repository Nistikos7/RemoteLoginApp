package com.mycompany.rest;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/remote")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RemoteAuthResource {
    
    private final UserDAO userDAO = new UserDAO();
    
    @POST
    @Path("/login")
    public Response remoteLogin(Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            if (username == null || password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity("Username and password are required")
                             .build();
            }
            
            User user = userDAO.validateUser(username, password);
            if (user != null) {
                user.setPassword(null); // Για ασφάλεια
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("user", user);
                response.put("message", "Remote login successful");
                
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
    
    @POST
    @Path("/register")
    public Response remoteRegister(User user) {
        try {
            if (userDAO.usernameExists(user.getUsername())) {
                return Response.status(Response.Status.CONFLICT)
                             .entity("Username already exists")
                             .build();
            }
            
            boolean success = userDAO.registerUser(user);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "User registered successfully via remote API");
                return Response.status(Response.Status.CREATED)
                             .entity(response)
                             .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity("Remote registration failed")
                             .build();
            }
        } catch (Exception e) {
            return Response.serverError()
                         .entity("Error: " + e.getMessage())
                         .build();
        }
    }
    
    @GET
    @Path("/validate/{username}")
    public Response validateUser(@PathParam("username") String username) {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null) {
                user.setPassword(null); // Για ασφάλεια
                return Response.ok(user).build();
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