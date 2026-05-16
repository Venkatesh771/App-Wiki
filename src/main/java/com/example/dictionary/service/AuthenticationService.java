package com.example.dictionary.service;

import com.example.dictionary.model.User;
import com.example.dictionary.model.LdapUser;
import com.example.dictionary.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static boolean looksHashed(String s) {
        return s != null && s.length() >= 60 && s.startsWith("$2");
    }

    public String generateNextLocalCwid() {
        int max = 0;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("^CW(\\d+)$");
        for (User u : userRepository.findAll()) {
            if (u.getCwid() == null) continue;
            java.util.regex.Matcher m = p.matcher(u.getCwid());
            if (m.matches()) {
                try {
                    int n = Integer.parseInt(m.group(1));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) { }
            }
        }
        return String.format("CW%03d", max + 1);
    }


    public AuthenticationService() {
    }


    @Autowired
    public void initializeMockUsers() {
        if (userRepository.count() == 0) {
            String seed = passwordEncoder.encode("password123");
            userRepository.save(
                    new User("CW001", "James Wilson", "james.wilson@example.com", seed, "Super Admin", true));
            userRepository.save(
                    new User("CW002", "Sarah Anderson", "sarah.anderson@example.com", seed, "Admin", true));
            userRepository
                    .save(new User("CW003", "Michael Chen", "michael.chen@example.com", seed, "User", true));
        }

        for (User u : userRepository.findAll()) {
            String pwd = u.getPassword();
            if (pwd != null && !pwd.isBlank() && !looksHashed(pwd)) {
                u.setPassword(passwordEncoder.encode(pwd));
                userRepository.save(u);
                logger.info("Migrated plaintext password to BCrypt for: " + u.getCwid());
            }
        }
    }

    /**
     * Authenticate user by CWID and password
     * Uses LOCAL authentication for existing users in database
     * Uses LDAP authentication for new users
     * Returns user with role from database
     * 
     * @param cwid     User's CWID
     * @param password User's password
     * @return User object if authenticated, null otherwise
     */
    public User authenticate(String cwid, String password) {
        try {

            if (cwid != null) {
                cwid = cwid.trim().toUpperCase();
            }

            logger.info("Step 1: Checking if user exists in database: " + cwid);
            Optional<User> userOpt = userRepository.findByCwid(cwid);


            if (userOpt.isPresent()) {

                User user = userOpt.get();
                logger.info("User found in database: " + cwid + ", Auth Type: " + user.getAuthType());


                if ("LDAP".equals(user.getAuthType())) {

                    logger.info("Using LDAP authentication for user: " + cwid);
                    LdapUser ldapUser = ldapAuthenticationService.getUserByCredentials(cwid, password);


                    if (ldapUser == null) {
                        logger.warning("LDAP authentication failed for user: " + cwid);
                        return null;
                    }
                    logger.info("User authenticated via LDAP: " + cwid);
                } else {

                    logger.info("Using LOCAL authentication for user: " + cwid);
                    String stored = user.getPassword();
                    if (stored == null || stored.isBlank()) {
                        logger.warning("Local authentication failed - no password on file for: " + cwid);
                        return null;
                    }
                    boolean ok = looksHashed(stored)
                            ? passwordEncoder.matches(password, stored)
                            : password.equals(stored);
                    if (!ok) {
                        logger.warning("Local authentication failed - Invalid credentials for user: " + cwid);
                        return null;
                    }

                    if (!looksHashed(stored)) {
                        user.setPassword(passwordEncoder.encode(password));
                        userRepository.save(user);
                        logger.info("Upgraded legacy plaintext password to BCrypt for: " + cwid);
                    }
                    logger.info("User authenticated via LOCAL: " + cwid);
                }


                // Step 2: Verify user is active
                if (!user.isActive()) {
                    logger.warning("User account is inactive: " + cwid);
                    return null;
                }

                // Step 3: Grant access based on user role from database
                logger.info("User login successful: " + cwid + " with role: " + user.getRole()
                        + " - Granting access based on role");
                return user;


            } else {

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
     * 
     * @param cwid User's CWID
     * @return User object if found, null otherwise
     */
    public User getUserByCwid(String cwid) {
        return userRepository.findByCwid(cwid).orElse(null);
    }

    /**
     * Get all users in reverse order (most recent first)
     * 
     * @return List of all users with most recent at the top
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(userRepository.findAll());

        Collections.reverse(userList);
        return userList;
    }

    /**
     * Add a new user
     * 
     * @param user User object to add
     * @return true if user was added, false if CWID already exists
     */
    public boolean addUser(User user) {

        if (userRepository.findByCwid(user.getCwid()).isPresent()) {
            return false;
        }
        if (user.getPassword() != null && !user.getPassword().isBlank() && !looksHashed(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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
     * 
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
                String pwd = user.getPassword();
                if (!looksHashed(pwd)) pwd = passwordEncoder.encode(pwd);
                userToUpdate.setPassword(pwd);
            }
            userRepository.save(userToUpdate);
            return true;
        }
        return false;
    }

    /**
     * Delete user by CWID
     * 
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

    private static final ObjectMapper FILTER_GROUPS_MAPPER = new ObjectMapper();

    public void renameGroupInAllFilters(String oldGroup, String newGroup) {
        if (oldGroup == null || oldGroup.isBlank() || newGroup == null || newGroup.isBlank()) return;
        if (oldGroup.equals(newGroup)) return;

        List<User> all = userRepository.findAll();
        for (User u : all) {
            String json = u.getFilterGroups();
            if (json == null || json.isBlank()) continue;
            try {
                List<String> groups = FILTER_GROUPS_MAPPER.readValue(json, new TypeReference<List<String>>() {});
                boolean changed = false;
                List<String> updated = new ArrayList<>(groups.size());
                for (String g : groups) {
                    if (oldGroup.equals(g)) {
                        if (!updated.contains(newGroup)) updated.add(newGroup);
                        changed = true;
                    } else {
                        if (!updated.contains(g)) updated.add(g);
                    }
                }
                if (changed) {
                    u.setFilterGroups(FILTER_GROUPS_MAPPER.writeValueAsString(updated));
                    userRepository.save(u);
                }
            } catch (Exception ex) {
                logger.warning("Failed to migrate filter groups for user " + u.getCwid() + ": " + ex.getMessage());
            }
        }
    }

    public boolean updateFilterGroups(String cwid, String jsonGroups) {
        if (cwid == null) return false;
        Optional<User> existing = userRepository.findByCwid(cwid.trim().toUpperCase());
        if (existing.isEmpty()) return false;
        User user = existing.get();
        user.setFilterGroups(jsonGroups);
        userRepository.save(user);
        return true;
    }


    /**
     * Get LDAP user details by CWID (for admin user management)
     * This method retrieves user information from LDAP without requiring
     * authentication
     * 
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
