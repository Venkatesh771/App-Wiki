package com.example.dictionary.service;

import com.example.dictionary.model.LdapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.*;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

@Service
public class LdapAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(LdapAuthenticationService.class);
      @Value("${spring.ldap.urls:ldap://bayer.cnb:3268}")
    private String ldapServer;

    @Value("${spring.ldap.domain:ad-bayer-cnb\\\\}")
    private String domainName;

    @Value("${spring.ldap.authenticationmode:member}")
    private String securityAuthenticationMode;

    @Value("${spring.ldap.base:OU=itaccounts,DC=bayer,DC=cnb}")
    private String searchBase;

    @Value("${spring.ldap.filter:}")
    private String searchFilter;

    @Value("${spring.ldap.embedded.credential.username:}")
    private String botUsername;

    @Value("${spring.ldap.embedded.credential.password:}")
    private String botPassword;

    public LdapUser getUserByCredentials(final String username, final String password) {
        serverInfo();
        logger.info("Checking if user {} is permitted to access secured resources by the given Active Directory credentials.", username);
        LdapUser user = null;

        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                LdapContext ctx = getLdapContext(username, password);
                user = buildUser(username, ctx);
                logger.info("User {} successfully authenticated and retrieved from LDAP", username);
                return user;

            } catch (SizeLimitExceededException | AuthenticationException e) {
                logger.error("LDAP authentication failed for user {}: {}", username, e.getMessage());
                logger.error("NOT_AUTHENTICATED");
                return null;
            } catch (Exception e) {
                logger.error("LDAP connection error for user {}: {}", username, e.getMessage());
                logger.error("NO_CONNECTION");
                return null;
            }
        }
        return user;
    }

    private SearchControls getUserSearchControls() {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = { "sn", "givenName", "mail", "displayName", "sAMAccountName" };
        cons.setReturningAttributes(attrIDs);
        logger.debug("Search controls configured: {}", cons);
        return cons;
    }
    private LdapUser buildUser(String userName, LdapContext ctx) {
        logger.info("Building user object for: {}", userName);
        LdapUser user = new LdapUser();
        try {

            String searchDN = "DC=bayer,DC=cnb";
            logger.debug("Searching for user {} in DN: {}", userName, searchDN);

            NamingEnumeration<SearchResult> answer = ctx.search(searchDN, "sAMAccountName=" + userName,
                    getUserSearchControls());
            logger.debug("LDAP search result: {}", answer);              if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                logger.debug("User attributes retrieved: {}", attrs);

                try {
                    String firstName = attrs.get("givenName") != null ?
                        attrs.get("givenName").toString().replace("givenName:", "").trim() : "";
                    String lastName = attrs.get("sn") != null ?
                        attrs.get("sn").toString().replace("sn:", "").trim() : "";

                    user.setFirstname(firstName);
                    user.setLastname(lastName);

                    String fullName = (firstName + " " + lastName).trim();
                    if (fullName.isEmpty()) {
                        fullName = userName;
                    }
                    user.setUsername(fullName);

                    user.setEmail(attrs.get("mail") != null ?
                        attrs.get("mail").toString().replace("mail:", "").trim() : "");

                    logger.info("User object successfully built - Username: {} - {}", fullName, user);
                } catch (NullPointerException e) {
                    logger.warn("Some user attributes not available in LDAP, continuing with available data");
                    user.setUsername(userName);
                    logger.info("User object built with partial data: {}", user);
                }
            } else {
                logger.warn("User info not found in LDAP search for: {}", userName);
                return null;
            }
        } catch (NameNotFoundException e) {
            logger.error("LDAP search base or user not found: {}", e.getMessage());
            logger.debug("Exception details: ", e);
            return null;
        } catch (Exception e) {
            logger.error("Error building user object: {}", e.getMessage());
            logger.debug("Exception details: ", e);
            return null;
        }
        return user;
    }

    private LdapContext getLdapContext(String ldapUsername, String ldapPassword)
            throws NamingException {
        logger.info("Establishing LDAP context for user: {}", ldapUsername);

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapServer);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, domainName + ldapUsername);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);

        env.put("com.sun.jndi.ldap.connect.pool", "false");
        env.put("com.sun.jndi.ldap.connect.timeout", "5000");
        env.put("com.sun.jndi.ldap.read.timeout", "5000");
        env.put("java.naming.ldap.version", "3");

        InitialLdapContext initialLdapContext = new InitialLdapContext(env, null);
        logger.info("LDAP context successfully established");
        return initialLdapContext;
    }

    public LdapUser getUserDetailsByCwid(String cwid) {
        logger.info("Retrieving user details from LDAP for CWID: {}", cwid);
        LdapUser user = null;

        if (cwid == null || cwid.isEmpty()) {
            logger.warn("Empty CWID provided for user lookup");
            return null;
        }

        try {

            LdapContext ctx = createLookupContext();

            String searchDN = "DC=bayer,DC=cnb";
            logger.debug("Searching LDAP for CWID: {} in DN: {}", cwid, searchDN);

            NamingEnumeration<SearchResult> answer = ctx.search(searchDN,
                "sAMAccountName=" + cwid,
                getUserSearchControls());
              if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                logger.debug("LDAP attributes found for CWID: {}", cwid);

                user = new LdapUser();

                try {
                    String firstName = "";
                    String lastName = "";

                    if (attrs.get("givenName") != null) {
                        firstName = attrs.get("givenName").toString().replace("givenName:", "").trim();
                        user.setFirstname(firstName);
                    }
                    if (attrs.get("sn") != null) {
                        lastName = attrs.get("sn").toString().replace("sn:", "").trim();
                        user.setLastname(lastName);
                    }

                    String fullName = (firstName + " " + lastName).trim();
                    if (fullName.isEmpty()) {
                        fullName = cwid;
                    }
                    user.setUsername(fullName);

                    if (attrs.get("mail") != null) {
                        user.setEmail(attrs.get("mail").toString().replace("mail:", "").trim());
                    }

                    logger.info("User details successfully retrieved for CWID: {} - Username: {} - {}", cwid, fullName, user);
                } catch (NullPointerException e) {
                    logger.warn("Some attributes missing for CWID {}, continuing with available data", cwid);
                    user.setUsername(cwid);
                    logger.info("Partial user details retrieved for CWID: {}", cwid);
                }
            } else {
                logger.warn("No LDAP user found with CWID: {}", cwid);
                return null;
            }

            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    logger.debug("Error closing LDAP context: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Error retrieving user details for CWID {}: {}", cwid, e.getMessage());
            logger.debug("Exception details: ", e);
            return null;
        }

        return user;
    }

    private LdapContext createLookupContext() throws NamingException {
        logger.debug("Creating LDAP context for user lookup using bot credentials");

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapServer);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        if (botUsername != null && !botUsername.isEmpty() && botPassword != null && !botPassword.isEmpty()) {
            env.put(Context.SECURITY_PRINCIPAL, domainName + botUsername);
            env.put(Context.SECURITY_CREDENTIALS, botPassword);
            logger.debug("Using bot credentials for LDAP lookup");
        } else {
            logger.warn("Bot credentials not configured, attempting anonymous bind");
            env.put(Context.SECURITY_AUTHENTICATION, "none");
        }

        env.put("com.sun.jndi.ldap.connect.pool", "false");
        env.put("com.sun.jndi.ldap.connect.timeout", "5000");
        env.put("com.sun.jndi.ldap.read.timeout", "5000");
        env.put("java.naming.ldap.version", "3");

        logger.debug("Establishing LDAP connection for lookup");
        return new InitialLdapContext(env, null);
    }

    private void serverInfo(){
        logger.debug("LDAP Server Configuration - Server: {}, Domain: {}, AuthMode: {}, Base: {}, Filter: {}",
                ldapServer, domainName, securityAuthenticationMode, searchBase, searchFilter);
    }
}
