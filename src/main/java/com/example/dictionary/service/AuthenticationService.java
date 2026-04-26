package com.example.dictionary.service;

import com.example.dictionary.model.User;
import com.example.dictionary.model.LdapUser;
import com.example.dictionary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.logging.Logger;

@Service
public class AuthenticationService {
    
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LdapAuthenticationService ldapAuthenticationService;
    
    public AuthenticationService() {
    }
    
    @Autowired
    public void initializeMockUsers() {
        List<String> keepCwids = Arrays.asList("CW001", "CW002", "CW003");

        // Remove any mock users that are not in the keep list
        userRepository.findAll().stream()
            .filter(u -> !keepCwids.contains(u.getCwid()))
            .forEach(userRepository::delete);

        // Seed the three base users if they don't exist yet
        if (!userRepository.findByCwid("CW001").isPresent()) {
            userRepository.save(new User("CW001", "James Wilson", "james.wilson@example.com", "password123", "Super Admin", true));
        }
        if (!userRepository.findByCwid("CW002").isPresent()) {
            userRepository.save(new User("CW002", "Sarah Anderson", "sarah.anderson@example.com", "password123", "Admin", true));
        }
        if (!userRepository.findByCwid("CW003").isPresent()) {
            userRepository.save(new User("CW003", "Michael Chen", "michael.chen@example.com", "password123", "User", true));
        }
    }    /**
     * Authenticate user by CWID and password
     * Uses LOCAL authentication for existing users in database
     * Uses LDAP authentication for new users
     * Returns user with role from database
     * @param cwid User's CWID
     * @param password User's password
     * @return User object if authenticated, null otherwise
     */
    public User authenticate(String cwid, String password) {
        try {
            // Step 1: Check if user exists in database
            logger.info("Step 1: Checking if user exists in database: " + cwid);
            Optional<User> userOpt = userRepository.findByCwid(cwid);
            
            if (userOpt.isPresent()) {
                // User exists in database - authenticate based on auth type
                User user = userOpt.get();
                logger.info("User found in database: " + cwid + ", Auth Type: " + user.getAuthType());
                
                if ("LDAP".equals(user.getAuthType())) {
                    // LDAP authentication
                    logger.info("Using LDAP authentication for user: " + cwid);
                    LdapUser ldapUser = ldapAuthenticationService.getUserByCredentials(cwid, password);
                    
                    if (ldapUser == null) {
                        logger.warning("LDAP authentication failed for user: " + cwid);
                        return null;
                    }
                    logger.info("User authenticated via LDAP: " + cwid);
                } else {
                    // LOCAL authentication
                    logger.info("Using LOCAL authentication for user: " + cwid);
                    if (user.getPassword() == null || !password.equals(user.getPassword())) {
                        logger.warning("Local authentication failed - Invalid credentials for user: " + cwid);
                        return null;
                    }
                    logger.info("User authenticated via LOCAL: " + cwid);
                }
                
                // Step 2: Verify user is active
                if (!user.isActive()) {
                    logger.warning("User account is inactive: " + cwid);
                    return null;
                }
                
                // Step 3: Grant access based on user role from database
                logger.info("User login successful: " + cwid + " with role: " + user.getRole() + " - Granting access based on role");
                return user;
                
            } else {
                // User does not exist in database - try LDAP authentication for new user
                logger.info("User not found in database, attempting LDAP authentication for: " + cwid);
                LdapUser ldapUser = ldapAuthenticationService.getUserByCredentials(cwid, password);
                
                if (ldapUser == null) {
                    logger.warning("LDAP authentication failed for new user: " + cwid);
                    return null;
                }
                
                logger.info("New user authenticated via LDAP: " + cwid + ". Creating user record with User role.");
                
                // Create new user with LDAP auth type and User role
                User newUser = new User(cwid, ldapUser.getUsername(), ldapUser.getEmail(), null, "User", true);
                newUser.setAuthType("LDAP");
                userRepository.save(newUser);
                
                logger.info("New user created in database: " + cwid);
                return newUser;
            }
            
        } catch (Exception e) {
            logger.warning("Authentication error for user: " + cwid + ". Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get user by CWID
     * @param cwid User's CWID
     * @return User object if found, null otherwise
     */
    public User getUserByCwid(String cwid) {
        return userRepository.findByCwid(cwid).orElse(null);
    }    /**
     * Get all users in reverse order (most recent first)
     * @return List of all users with most recent at the top
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(userRepository.findAll());
        // Reverse the list to show most recent users at the top
        Collections.reverse(userList);
        return userList;
    }

    /**
     * Add a new user
     * @param user User object to add
     * @return true if user was added, false if CWID already exists
     */
    public boolean addUser(User user) {
        // Check if user with same CWID already exists
        if (userRepository.findByCwid(user.getCwid()).isPresent()) {
            return false;
        }
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Update existing user
     * @param cwid CWID of user to update
     * @param user Updated user object
     * @return true if user was updated, false if user not found
     */
    public boolean updateUser(String cwid, User user) {
        Optional<User> existingUser = userRepository.findByCwid(cwid);
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setRole(user.getRole());
            userToUpdate.setActive(user.isActive());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(user.getPassword());
            }
            userRepository.save(userToUpdate);
            return true;
        }
        return false;
    }    /**
     * Delete user by CWID
     * @param cwid CWID of user to delete
     * @return true if user was deleted, false if user not found
     */
    public boolean deleteUser(String cwid) {
        Optional<User> user = userRepository.findByCwid(cwid);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }
    
    /**
     * Get LDAP user details by CWID (for admin user management)
     * This method retrieves user information from LDAP without requiring authentication
     * @param cwid User's CWID
     * @return LdapUser object if found, null otherwise
     */
    public LdapUser getLdapUserDetails(String cwid) {
        logger.info("Fetching LDAP user details for CWID: " + cwid);
        LdapUser ldapUser = ldapAuthenticationService.getUserDetailsByCwid(cwid);
        
        if (ldapUser != null) {
            logger.info("LDAP user details found for CWID: " + cwid + " - Username: " + ldapUser.getUsername());
        } else {
            logger.warning("LDAP user details not found for CWID: " + cwid);
        }
        
        return ldapUser;
    }
}

