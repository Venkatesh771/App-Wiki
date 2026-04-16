package com.example.dictionary.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.logging.Logger;

@Service
public class LdapAuthenticationService {
    
    private static final Logger logger = Logger.getLogger(LdapAuthenticationService.class.getName());
    
    @Value("${spring.ldap.urls:ldaps://ldaps.bayer-ag.com:636}")
    private String ldapUrl;
    
    @Value("${spring.ldap.base:o=bayer}")
    private String searchBase;
    
    /**
     * Authenticate user against LDAP server
     * Verifies that the user exists in Bayer's LDAP directory
     * @param username LDAP username or CWID
     * @param password LDAP password
     * @return true if user is authenticated and exists in Bayer LDAP, false otherwise
     */
    public boolean authenticateWithLdap(String username, String password) {
        try {
            // Build the DN (Distinguished Name) for the user
            String userDn = buildUserDn(username);
            
            logger.info("Attempting LDAP authentication for user: " + username + " with DN: " + userDn);
            
            // Set up LDAP environment for authentication
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put("com.sun.jndi.ldap.connect.pool", "false");
            env.put("java.naming.ldap.version", "3");
            
            // Try to create a context with the user's credentials
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
     * Verify if user exists in Bayer LDAP
     * @param username LDAP username or CWID
     * @return true if user exists in LDAP
     */
    public boolean userExistsInLdap(String username) {
        try {
            logger.info("Checking if user exists in LDAP: " + username);
            String userDn = buildUserDn(username);
            
            // Set up LDAP environment
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put("com.sun.jndi.ldap.connect.pool", "false");
            
            try {
                DirContext context = new InitialDirContext(env);
                // Try to get attributes of the user DN
                context.getAttributes(userDn);
                context.close();
                
                logger.info("User found in LDAP: " + username);
                return true;
                
            } catch (javax.naming.NameNotFoundException e) {
                logger.warning("User not found in LDAP: " + username);
                return false;
            }
            
        } catch (Exception e) {
            logger.warning("Error checking if user exists in LDAP: " + username + ". Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Build the user's Distinguished Name (DN)
     * @param username Username or CWID
     * @return Full DN for the user
     */
    private String buildUserDn(String username) {
        // Users are stored as uid=username,ou=itaccounts,o=bayer
        return "uid=" + username + ",ou=itaccounts," + searchBase;
    }
}
