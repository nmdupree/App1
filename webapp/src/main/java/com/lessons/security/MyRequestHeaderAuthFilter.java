package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Component("com.lessons.security.RequestHeaderAuthFilter")
public class MyRequestHeaderAuthFilter extends RequestHeaderAuthenticationFilter
{
    @Resource
    private MyAuthenticationManager myAuthenticationManager;

    @Value("${security.authenticate.header.dn:SSL_CLIENT_S_DN}")
    private String nameOfHeaderHoldingDN;

    @Value("${development.mode}")
    private boolean developmentMode;

    private static final Logger logger = LoggerFactory.getLogger(MyRequestHeaderAuthFilter.class);

    @PostConstruct
    public void init() {
        this.setAuthenticationManager(myAuthenticationManager);
        logger.debug("RequestHeaderAuthFilter()  sNameOfHeaderHoldingDN={}", nameOfHeaderHoldingDN);
    }


    /**************************************************************
     * getPreAuthenticatedPrincipal()
     *
     * This is called when a request is made, the returned object identifies the
     * user and will either be {@literal null} or a String.
     *
     * This method will throw an exception if
     * exceptionIfHeaderMissing is set to true (default) and the required header is missing.
     **************************************************************/
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request)
    {
        logger.debug("getPreAuthenticatedPrincipal() called");

        // Get the principal from the header
        String userDnFromHeader = (String) request.getHeader(this.nameOfHeaderHoldingDN);
        if (developmentMode) {
            //Since the dev profile is the only one that does not have info in the header
            // The name has to be "hacked here" so the the CEDAuthenticationManager is called.
            // We are assuming a preApprovedAuthentication so something must be in the header.
            userDnFromHeader = "Bogus_user";
        }

        // If this method returns null, then the user will see a 403 Forbidden Message
        return userDnFromHeader;
    }


}