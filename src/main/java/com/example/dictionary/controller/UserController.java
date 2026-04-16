package com.example.dictionary.controller;

import com.example.dictionary.model.User;
import com.example.dictionary.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthenticationService authenticationService;    /**
     * Get all users in reverse order (most recent first)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            return ResponseEntity.status(403).build();
        }
        
        List<User> users = authenticationService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by CWID
     */
    @GetMapping("/{cwid}")
    public ResponseEntity<User> getUserByCwid(@PathVariable String cwid, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            return ResponseEntity.status(403).build();
        }
        
        User user = authenticationService.getUserByCwid(cwid);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Add new user
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return ResponseEntity.status(403).body(response);
        }
        
        // Validate input
        if (user.getCwid() == null || user.getCwid().isEmpty() ||
            user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getRole() == null || user.getRole().isEmpty()) {
            
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }
          // Check if CWID format is valid
        if (!user.getCwid().matches("^[A-Za-z0-9]{3,}$")) {
            response.put("success", false);
            response.put("message", "CWID must be at least 3 alphanumeric characters");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate email format
        if (!user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            response.put("success", false);
            response.put("message", "Invalid email format");
            return ResponseEntity.badRequest().body(response);
        }
          // Check if CWID already exists
        if (authenticationService.getUserByCwid(user.getCwid()) != null) {
            response.put("success", false);
            response.put("message", "User with CWID " + user.getCwid() + " already exists");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Set authentication type to LDAP for newly added users
        user.setAuthType("LDAP");
        
        // DO NOT set password for LDAP users - they authenticate via LDAP
        // Password will be null for LDAP users
        user.setPassword(null);
        
        // Add user
        if (authenticationService.addUser(user)) {
            response.put("success", true);
            response.put("message", "User added successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "Failed to add user");
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * Update existing user
     */
    @PutMapping("/{cwid}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String cwid, @RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return ResponseEntity.status(403).body(response);
        }
        
        // Validate input
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getRole() == null || user.getRole().isEmpty()) {
            
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate email format
        if (!user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            response.put("success", false);
            response.put("message", "Invalid email format");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if user exists
        User existingUser = authenticationService.getUserByCwid(cwid);
        if (existingUser == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.notFound().build();
        }
        
        // Keep the same CWID and password
        user.setCwid(cwid);
        user.setPassword(existingUser.getPassword());
        
        // Update user
        if (authenticationService.updateUser(cwid, user)) {
            response.put("success", true);
            response.put("message", "User updated successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "Failed to update user");
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * Delete user by CWID
     */
    @DeleteMapping("/{cwid}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String cwid, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || !currentUser.getRole().equals("Super Admin")) {
            response.put("success", false);
            response.put("message", "Only Super Admin can delete users");
            return ResponseEntity.status(403).body(response);
        }
        
        // Delete user
        if (authenticationService.deleteUser(cwid)) {
            response.put("success", true);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "User not found");
        return ResponseEntity.notFound().build();
    }
}
