package com.example.dictionary.controller;

import com.example.dictionary.model.User;
import com.example.dictionary.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Validate CWID and get user details from LDAP
     * This endpoint is used when adding a new user to fetch their details from LDAP
     */    @GetMapping("/validate-cwid/{cwid}")
    public ResponseEntity<Map<String, Object>> validateAndGetLdapUser(@PathVariable String cwid, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        
        // Validate CWID format
        if (cwid == null || cwid.isEmpty() || !cwid.matches("^[A-Za-z0-9]{3,}$")) {
            response.put("success", false);
            response.put("message", "Invalid CWID format");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if CWID already exists in database
        User existingUser = authenticationService.getUserByCwid(cwid);
        if (existingUser != null) {
            response.put("success", false);
            response.put("message", "User with CWID " + cwid + " already exists");
            response.put("alreadyExists", true);
            return ResponseEntity.badRequest().body(response);
        }
          // Fetch user details from LDAP
        com.example.dictionary.model.LdapUser ldapUser = authenticationService.getLdapUserDetails(cwid);
        
        if (ldapUser == null) {
            response.put("success", false);
            response.put("message", "CWID not found in LDAP directory");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Return user details for pre-filling the form
        response.put("success", true);
        response.put("message", "User details retrieved from LDAP");
        response.put("data", new HashMap<String, Object>() {{
            put("cwid", cwid);
            put("username", ldapUser.getUsername());
            put("email", ldapUser.getEmail());
            put("firstName", ldapUser.getFirstname());
            put("lastName", ldapUser.getLastname());
        }});
        
        return ResponseEntity.ok(response);
    }

    /**
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
    }    /**
     * Add new user
     * Username is automatically fetched from LDAP, no manual input required
     */    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        // Check if user is authenticated and has admin privileges
        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.FORBIDDEN);
        }
        
        // Validate CWID
        if (user.getCwid() == null || user.getCwid().isEmpty()) {
            response.put("success", false);
            response.put("message", "CWID is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate CWID format
        if (!user.getCwid().matches("^[A-Za-z0-9]{3,}$")) {
            response.put("success", false);
            response.put("message", "CWID must be at least 3 alphanumeric characters");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate email format
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            response.put("success", false);
            response.put("message", "Valid email address is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Validate role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            response.put("success", false);
            response.put("message", "Role is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if CWID already exists in database
        if (authenticationService.getUserByCwid(user.getCwid()) != null) {
            response.put("success", false);
            response.put("message", "User with CWID " + user.getCwid() + " already exists");
            return ResponseEntity.badRequest().body(response);
        }
          // Fetch user details from LDAP to get the username
        com.example.dictionary.model.LdapUser ldapUser = authenticationService.getLdapUserDetails(user.getCwid());
        
        if (ldapUser == null) {
            response.put("success", false);
            response.put("message", "CWID not found in LDAP directory. Please verify the CWID and try again.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        // Set the username from LDAP (single source of truth)
        user.setUsername(ldapUser.getUsername());
        
        // Set authentication type to LDAP
        user.setAuthType("LDAP");
        
        // DO NOT set password for LDAP users - they authenticate via LDAP
        user.setPassword(null);
        
        // Add user to database
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
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
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
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
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
