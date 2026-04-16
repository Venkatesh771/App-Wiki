package com.example.dictionary.service;

import com.example.dictionary.model.User;
import com.example.dictionary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;
import java.util.logging.Logger;
import java.util.Hashtable;

@Service
public class AuthenticationService {
    
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${spring.ldap.urls:ldaps://ldaps.bayer-ag.com:636}")
    private String ldapUrl;
    
    @Value("${spring.ldap.base:o=bayer}")
    private String searchBase;
    
    public AuthenticationService() {
    }
    
    @Autowired
    public void initializeMockUsers() {
        // Initialize mock data only if database is empty
        if (userRepository.count() == 0) {
            List<User> mockUsers = new ArrayList<>();
            
            // Super Admin Users
            mockUsers.add(new User("CW001", "James Wilson", "james.wilson@example.com", "password123", "Super Admin", true));
            mockUsers.add(new User("CW006", "Lisa Thompson", "lisa.thompson@example.com", "password123", "Super Admin", true));
            mockUsers.add(new User("CW012", "Jessica Rodriguez", "jessica.rodriguez@example.com", "password123", "Super Admin", true));
            
            // Admin Users
            mockUsers.add(new User("CW002", "Sarah Anderson", "sarah.anderson@example.com", "password123", "Admin", true));
            mockUsers.add(new User("CW004", "Emma Johnson", "emma.johnson@example.com", "password123", "Admin", false));
            mockUsers.add(new User("CW008", "Jennifer Lee", "jennifer.lee@example.com", "password123", "Admin", true));
            mockUsers.add(new User("CW010", "Amanda White", "amanda.white@example.com", "password123", "Admin", true));
            mockUsers.add(new User("CW014", "Michelle Davis", "michelle.davis@example.com", "password123", "Admin", false));
            
            // Regular Users
            mockUsers.add(new User("CW003", "Michael Chen", "michael.chen@example.com", "password123", "User", true));
            mockUsers.add(new User("CW005", "David Martinez", "david.martinez@example.com", "password123", "User", true));
            mockUsers.add(new User("CW007", "Robert Garcia", "robert.garcia@example.com", "password123", "User", true));
            mockUsers.add(new User("CW009", "Christopher Brown", "christopher.brown@example.com", "password123", "User", false));
            mockUsers.add(new User("CW011", "Kevin Park", "kevin.park@example.com", "password123", "User", true));
            mockUsers.add(new User("CW013", "Daniel Taylor", "daniel.taylor@example.com", "password123", "User", true));
            mockUsers.add(new User("CW015", "Matthew Jackson", "matthew.jackson@example.com", "password123", "User", true));
            
            userRepository.saveAll(mockUsers);
        }
    }    /**
     * Authenticate user by CWID and password
     * Uses LOCAL authentication for existing users
     * Uses LDAP authentication for newly added users
     * Then checks database for user role
     * @param cwid User's CWID
     * @param password User's password
     * @return User object if authenticated, null otherwise
     */
    public User authenticate(String cwid, String password) {
        try {
            // Step 1: Check if user exists in database
            logger.info("Step 1: Checking if user exists in database: " + cwid);
            Optional<User> userOpt = userRepository.findByCwid(cwid);
            
            if (!userOpt.isPresent()) {
                logger.warning("User not found in database: " + cwid);
                return null;
            }
            
            User user = userOpt.get();
            logger.info("User found in database: " + cwid + ", Auth Type: " + user.getAuthType());
            
            // Step 2: Authenticate based on authentication type
            boolean authenticated = false;
              if ("LDAP".equals(user.getAuthType())) {
                // New users: authenticate against LDAP with CWID and Password
                logger.info("Using LDAP authentication for user: " + cwid);
                logger.info("Verifying credentials against Bayer LDAP: " + cwid);
                authenticated = authenticateWithLdap(cwid, password);
                if (!authenticated) {
                    logger.warning("LDAP authentication failed - User credentials do not match Bayer LDAP for user: " + cwid);
                    return null;
                }
                logger.info("User confirmed to be from Bayer LDAP: " + cwid);
            } else {
                // Existing/LOCAL users: authenticate with stored password
                logger.info("Using LOCAL authentication for user: " + cwid);
                if (user.getPassword() == null || !password.equals(user.getPassword())) {
                    logger.warning("Local authentication failed - Invalid credentials for user: " + cwid);
                    return null;
                }
                authenticated = true;
            }
              logger.info("Authentication successful for user: " + cwid);
            
            // Step 3: Verify user is active
            if (!user.isActive()) {
                logger.warning("User account is inactive: " + cwid);
                return null;
            }
            
            // Step 4: Grant access based on user role from database
            logger.info("User login successful: " + cwid + " with role: " + user.getRole() + " - Granting access based on role");
            return user;
            
        } catch (Exception e) {
            logger.warning("Authentication error for user: " + cwid + ". Error: " + e.getMessage());
            return null;
        }
    }
      /**
     * Authenticate against LDAP server
     */    private boolean authenticateWithLdap(String username, String password) {
        try {
            // DN format for Bayer Global Catalog (bayer.cnb)
            String userDn = "uid=" + username + ",ou=itaccounts,dc=bayer,dc=cnb";
            logger.info("Attempting LDAP authentication for user: " + username + " with DN: " + userDn);
            
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            
            // Connection timeout settings for Bayer environment
            env.put("com.sun.jndi.ldap.connect.pool", "false");
            env.put("com.sun.jndi.ldap.connect.timeout", "5000");  // 5 second timeout
            env.put("com.sun.jndi.ldap.read.timeout", "5000");     // 5 second read timeout
            
            // SSL/TLS certificate handling for Bayer LDAPS
            env.put("java.naming.ldap.version", "3");
            
            // Disable certificate validation for self-signed certs (if needed in Bayer environment)
            System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
            
            try {
                DirContext context = new InitialDirContext(env);
                context.close();
                logger.info("LDAP authentication successful for user: " + username);
                return true;
            } catch (javax.naming.AuthenticationException e) {
                logger.warning("LDAP authentication failed - Invalid credentials for user: " + username);
                return false;
            }
            
        } catch (Exception e) {
            logger.warning("LDAP authentication error for user: " + username + ". Error: " + e.getMessage());
            return false;
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
    }

    /**
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
}
