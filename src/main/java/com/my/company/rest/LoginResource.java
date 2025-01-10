package com.mycompany.rest;

import com.mycompany.dao.UserDAO;
import com.mycompany.model.User;
import com.mycompany.model.LoginRequest;
import com.mycompany.model.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/remote")
public class LoginResource {
    
    @Inject
    private UserDAO userDAO;
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            // Χρησιμοποιούμε τη validateUser αντί της findByUsername
            boolean isValid = userDAO.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (isValid) {
                return Response
                    .ok(new ApiResponse("success", "Login successful"))
                    .build();
            } else {
                return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse("error", "Invalid username or password"))
                    .build();
            }
        } catch (Exception e) {
            return Response
                .serverError()
                .entity(new ApiResponse("error", "Server error: " + e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        try {
            // Έλεγχος αν υπάρχει το username
            if (userDAO.usernameExists(user.getUsername())) {
                return Response
                    .status(Response.Status.CONFLICT)
                    .entity(new ApiResponse("error", "Username already exists"))
                    .build();
            }
            
            // Έλεγχος αν υπάρχει το email
            if (userDAO.emailExists(user.getEmail())) {
                return Response
                    .status(Response.Status.CONFLICT)
                    .entity(new ApiResponse("error", "Email already exists"))
                    .build();
            }
            
            // Χρησιμοποιούμε τη registerUser αντί της save
            boolean success = userDAO.registerUser(user);
            
            if (success) {
                return Response
                    .ok(new ApiResponse("success", "User registered successfully via remote API"))
                    .build();
            } else {
                return Response
                    .serverError()
                    .entity(new ApiResponse("error", "Failed to register user"))
                    .build();
            }
        } catch (Exception e) {
            return Response
                .serverError()
                .entity(new ApiResponse("error", "Server error: " + e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/validate/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUsername(@PathParam("username") String username) {
        try {
            boolean exists = userDAO.usernameExists(username);
            
            return Response
                .ok(new ApiResponse(
                    !exists ? "success" : "error",
                    !exists ? "Username is available" : "Username is already taken"
                ))
                .build();
                
        } catch (Exception e) {
            return Response
                .serverError()
                .entity(new ApiResponse("error", "Server error: " + e.getMessage()))
                .build();
        }
    }
}