package com.lessons;


import com.lessons.controllers.DashboardController;
import com.lessons.security.UserInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)    // Required to work with JUnit 4
@SpringBootTest                 // Start up a Spring Application Context
public class SecurityTest
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityTest.class);

    @Resource
    private DashboardController dashboardController;


    /**
     * Setup an Authentication Token so that the classes believe a user is logged-in
     */
    @Before
    public void setupAuthenticationToken(){
        // Create a list of granted Authorities
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SUPERUSER"));

        // Create the UserInfo object
        UserInfo userInfo = new UserInfo()
                .withId(5)
                .withUsernameUID("BOGUS_USER")
                .withGrantedAuthorities(grantedAuthorities);

        // Cast the UserInfo object to UserDetails
        UserDetails userDetails = userInfo;

        // Create the AuthenticationToken
        PreAuthenticatedAuthenticationToken preapproved = new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        preapproved.setAuthenticated(true);

        // Set the authentication token
        SecurityContextHolder.getContext().setAuthentication(preapproved);
    }




    @Test
    public void testSecurity()
    {
        logger.debug("testSecurity() started.");

        ResponseEntity<String> response = (ResponseEntity<String>) dashboardController.getDateTime();

        String datetime = response.getBody();
        Assert.assertNotNull(datetime);

        logger.debug("testSecurity() finished.");

    }
}
