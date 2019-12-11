package com.lessons.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("com.lessons.security.MyAuthenticationManager")
public class MyAuthenticationManager implements AuthenticationManager {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationManager.class);

    private static final Pattern patExtractCN = Pattern.compile("cn=(.*?)(?:,|/|\\z)", Pattern.CASE_INSENSITIVE);


    @Value("${spring.profiles.active:}")             // If not found, then holds an empty string
    private String activeProfileName;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            // Users is already authenticated, so do nothing
            return  SecurityContextHolder.getContext().getAuthentication();
        }

        UserDetails userDetails;

        // We are really authenticating in the UserDetailService -- so do nothing here
        if ( activeProfileName.equalsIgnoreCase("prod") ) {
            // Get the user details from *real* source -- e.g., ActiveDirectory or a database
            userDetails = loadUserDetailsFromRealSource(authentication);
        }
        else {
            // Get the hard-coded bogus user details
            userDetails = loadUserDetailsForDevelopment(authentication);
        }

        // Return an AuthenticationToken object
        PreAuthenticatedAuthenticationToken preapproved = new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        preapproved.setAuthenticated(true);
        return preapproved;
    }


    private UserDetails loadUserDetailsFromRealSource(Authentication authentication) {
        String userDN;
        PreAuthenticatedAuthenticationToken token = null;

        if (authentication.getPrincipal() instanceof String) {
            userDN = authentication.getPrincipal().toString();
        } else if (authentication.getPrincipal() instanceof UserInfo) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            token = ( PreAuthenticatedAuthenticationToken ) authentication.getPrincipal();
            userDN = token.getName();
        }

        // Get the user's UID from the CN=<...>
        try {
            String userUID = getCnValueFromLongDnString(userDN);

            List<GrantedAuthority> grantedRoleAuthorities = new ArrayList<>();

            // Get the list or roles for this user from the real ActiveDirectory or Header
            grantedRoleAuthorities.add(new SimpleGrantedAuthority("ROLE_SUPERUSER"));


            if ((grantedRoleAuthorities != null) && (grantedRoleAuthorities.size() > 0)) {
                // This user has atleast one role found in my authorizatoin service
                // NOTE:  All granted authorities must start with the "ROLE_" prefix
                grantedRoleAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS"));
            }
            else {
                // This user has no roles so throw a runtime exception
                throw new RuntimeException("No roles were found for this user: " + userUID);
            }

            logger.info("{} successfully logged-in.", userUID);

            // User is about to login
            // -- This would be the place to add/update a database record indicating that the user logged-in

            // Get the user's userid from the database
            int userId = 25;

            // We *MUST* set the database ID in the UserInfo object here
            return new UserInfo()
                    .withId(userId)
                    .withUsernameDn(userDN)
                    .withUsernameUID(userUID)
                    .withGrantedAuthorities(grantedRoleAuthorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Exception raised in loadUserDetailsFromRealSource():  This user will definitely not login", e);
        }
    }


    public UserDetails loadUserDetailsForDevelopment(Authentication authentication) {

        final String userUID = "my_test_user";
        final String userDN = "3.2.12.144549.1.9.1=#161760312e646576,CN=my_test_user,OU=Hosts,O=ZZTop.Org,C=ZZ";

        // Create a list of granted authorities
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SUPER_USER"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS"));

        // Create a bogus UserInfo object
        // NOTE:  I am hard-coding the user's userid=25
        UserInfo anonymousUserInfo = new UserInfo()
                .withId(25)
                .withUsernameUID(userUID)
                .withUsernameDn(userDN)
                .withGrantedAuthorities(grantedAuthorities);
        return anonymousUserInfo;
    }


    private static String getCnValueFromLongDnString(String userDN) {
        String cnValue = null;
        // Use the regular expression pattern to getByUserId the value part of "CN=value"
        Matcher matcher = patExtractCN.matcher(userDN);
        if (matcher.find()) {
            cnValue = matcher.group(1);
        }
        return cnValue;
    }

}
