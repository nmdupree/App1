package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("com.lessons.security.myAuthorizationFilter")
public class MyAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MyAuthorizationFilter.class);


    @Resource
    private MyAuthorizationService myAuthorizationService;

    /**
     * Filters such that all api calls will be sent to the filter.  All other calls are skipped
     * @param request the request
     * @return true if the filter is not applied, false if to filter
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (request.getRequestURI().startsWith("/api")) {
            // Do not skip this check
            return false;
        } else  if (request.getRequestURI().startsWith("/app1/api")) {
            // Do not skip this check
            return false;
        }

        // Skip this check
        return true;
    }

    /**
     * Performs the security filtering for all the api calls
     * See shouldNotFilter above to see what is included
     * @param request the request
     * @param response the response
     * @param filterChain the filter chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (myAuthorizationService.isAuthorized() == false) {
            // Someone made a request that is *NOT* allowed
            logger.warn("User made a request that is not allowed:  Requested URI is {}", request.getRequestURI());

            // Change the response object to tell the front-end that this request is forbidden
            // NOTE: Do not set the content type to be TEXT/PLAIN here
            HttpServletResponse httpResponse = (HttpServletResponse ) response;
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to perform this action.");
            return;
        }

        // Continue on with the request (as the user is allowed to make this call
        logger.info("Authorized user made a request:  Requested URI is {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
