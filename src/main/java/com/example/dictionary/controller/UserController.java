package com.example.dictionary.controller;

import com.example.dictionary.model.User;
import com.example.dictionary.service.ActivityLogService;
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

    @Autowired
    private ActivityLogService activityLogService;

        @GetMapping("/validate-cwid/{cwid}")
    public ResponseEntity<Map<String, Object>> validateAndGetLdapUser(@PathVariable String cwid, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        if (cwid == null || cwid.isEmpty() || !cwid.matches("^[A-Za-z0-9]{3,}$")) {
            response.put("success", false);
            response.put("message", "Invalid CWID format");
            return ResponseEntity.badRequest().body(response);
        }

        User existingUser = authenticationService.getUserByCwid(cwid);
        if (existingUser != null) {
            response.put("success", false);
            response.put("message", "User with CWID " + cwid + " already exists");
            response.put("alreadyExists", true);
            return ResponseEntity.badRequest().body(response);
        }

        com.example.dictionary.model.LdapUser ldapUser = authenticationService.getLdapUserDetails(cwid);

        if (ldapUser == null) {
            response.put("success", false);
            response.put("message", "CWID not found in LDAP directory");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

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

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            return ResponseEntity.status(403).build();
        }

        List<User> users = authenticationService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/next-cwid")
    public ResponseEntity<Map<String, String>> getNextCwid(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null
                || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(Map.of("cwid", authenticationService.generateNextLocalCwid()));
    }

    @GetMapping("/{cwid}")
    public ResponseEntity<User> getUserByCwid(@PathVariable String cwid, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            return ResponseEntity.status(403).build();
        }

        User user = authenticationService.getUserByCwid(cwid);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }        @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.FORBIDDEN);
        }

        if (user.getCwid() == null || user.getCwid().isEmpty()) {
            response.put("success", false);
            response.put("message", "CWID is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (!user.getCwid().matches("^[A-Za-z0-9]{3,}$")) {
            response.put("success", false);
            response.put("message", "CWID must be at least 3 alphanumeric characters");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            response.put("success", false);
            response.put("message", "Valid email address is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            response.put("success", false);
            response.put("message", "Role is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (authenticationService.getUserByCwid(user.getCwid()) != null) {
            response.put("success", false);
            response.put("message", "User with CWID " + user.getCwid() + " already exists");
            return ResponseEntity.badRequest().body(response);
        }

        if ("LOCAL".equalsIgnoreCase(user.getAuthType())) {
            if (user.getUsername() == null || user.getUsername().isBlank()) {
                response.put("success", false);
                response.put("message", "Username is required for a Local user");
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                response.put("success", false);
                response.put("message", "Password is required for a Local user");
                return ResponseEntity.badRequest().body(response);
            }
            user.setAuthType("LOCAL");
            if (authenticationService.addUser(user)) {
                activityLogService.recordUserAdd(currentUser.getCwid(), currentUser.getUsername(),
                        user.getCwid(), user.getUsername());
                response.put("success", true);
                response.put("message", "User added successfully");
                response.put("user", user);
                return ResponseEntity.ok(response);
            }
            response.put("success", false);
            response.put("message", "Failed to add user");
            return ResponseEntity.internalServerError().body(response);
        }

        com.example.dictionary.model.LdapUser ldapUser = authenticationService.getLdapUserDetails(user.getCwid());

        if (ldapUser == null) {
            response.put("success", false);
            response.put("message", "CWID not found in LDAP directory. Please verify the CWID and try again.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        user.setUsername(ldapUser.getUsername());

        user.setAuthType("LDAP");

        user.setPassword(null);

        if (authenticationService.addUser(user)) {
            activityLogService.recordUserAdd(
                    currentUser.getCwid(), currentUser.getUsername(),
                    user.getCwid(), user.getUsername());
            response.put("success", true);
            response.put("message", "User added successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Failed to add user");
        return ResponseEntity.internalServerError().body(response);
    }

    @PutMapping("/{cwid}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String cwid, @RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (currentUser == null || (!currentUser.getRole().equals("Admin") && !currentUser.getRole().equals("Super Admin"))) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getRole() == null || user.getRole().isEmpty()) {

            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (!user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            response.put("success", false);
            response.put("message", "Invalid email format");
            return ResponseEntity.badRequest().body(response);
        }

        User existingUser = authenticationService.getUserByCwid(cwid);
        if (existingUser == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.notFound().build();
        }

        user.setCwid(cwid);
        user.setPassword(existingUser.getPassword());

        String[] diff = summarizeUserDiff(existingUser, user);

        if (authenticationService.updateUser(cwid, user)) {
            if (!diff[0].isEmpty()) {
                activityLogService.recordUserEdit(currentUser.getCwid(), currentUser.getUsername(),
                        existingUser.getCwid(), existingUser.getUsername(),
                        "User", diff[0], diff[1]);
            }
            response.put("success", true);
            response.put("message", "User updated successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Failed to update user");
        return ResponseEntity.internalServerError().body(response);
    }

    @DeleteMapping("/{cwid}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String cwid, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (currentUser == null || !currentUser.getRole().equals("Super Admin")) {
            response.put("success", false);
            response.put("message", "Only Super Admin can delete users");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        User target = authenticationService.getUserByCwid(cwid);

        if (authenticationService.deleteUser(cwid)) {
            if (target != null) {
                String oldSummary = "Role: " + nz(target.getRole())
                        + "; Email: " + nz(target.getEmail())
                        + "; Status: " + (target.isActive() ? "Active" : "Inactive");
                activityLogService.recordUserDelete(currentUser.getCwid(), currentUser.getUsername(),
                        target.getCwid(), target.getUsername(), "User", oldSummary);
            }
            response.put("success", true);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "User not found");
        return ResponseEntity.notFound().build();
    }

    private static String[] summarizeUserDiff(User before, User after) {
        StringBuilder oldSb = new StringBuilder();
        StringBuilder newSb = new StringBuilder();
        addUserDiff(oldSb, newSb, "Username", before.getUsername(), after.getUsername());
        addUserDiff(oldSb, newSb, "Email", before.getEmail(), after.getEmail());
        addUserDiff(oldSb, newSb, "Role", before.getRole(), after.getRole());
        addUserDiff(oldSb, newSb, "Status",
                before.isActive() ? "Active" : "Inactive",
                after.isActive() ? "Active" : "Inactive");
        return new String[] { oldSb.toString(), newSb.toString() };
    }

    private static void addUserDiff(StringBuilder oldSb, StringBuilder newSb, String label, String oldV, String newV) {
        String a = nz(oldV);
        String b = nz(newV);
        if (a.equals(b)) return;
        if (oldSb.length() > 0) oldSb.append("; ");
        if (newSb.length() > 0) newSb.append("; ");
        oldSb.append(label).append(": ").append(a.isEmpty() ? "—" : a);
        newSb.append(label).append(": ").append(b.isEmpty() ? "—" : b);
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
