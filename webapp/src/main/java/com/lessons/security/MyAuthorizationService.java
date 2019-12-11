package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Service("com.lessons.security.myAuthorizationService")
public class MyAuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthorizationService.class);


    @Resource
    private HttpServletRequest request;             // Inject the current Request object

    @Value("${spring.profiles.active")
    private String activeProfileName;               // Inject the active spring profile name


    private HashMap<String, String> mapUrlToRole;



    @PostConstruct
    public void init()
    {
        logger.debug("init() started.");

        // Initialize the map of urls-to-roles
        //   Key=URL-TYPE + url
        //   Value=Role name
        this.mapUrlToRole = new HashMap<String, String>();

        this.mapUrlToRole.put("GET/app1/api/dashboard/timeSUPERUSER", null);
        this.mapUrlToRole.put("GET/app1/api/dashboard/aggsSUPERUSER", null);
    }


    /**
     * @return the logged-in user name
     */
    public String getUsername() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return "";
        }

        // Get the UserInfo object from Spring Security
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userInfo.getUsername();
    }


    /**
     * @return TRUE if the the user's roles allows the user to see this current REST endpoint
     */
    public boolean isAuthorized() {
        if (activeProfileName.equalsIgnoreCase("dev")) {
            // Running in dev mode
            return true;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // There is no Authentication object.  This user is definitely not authorized
            return false;
        }

        // Get the UserInfo object from Spring Security
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get all of the roles from the UserInfo object
        List<String> roles = userInfo.getRoles();

        // Loop through all of the roles (to see if this user is authorized)
        for (String role: roles) {
            if (role.startsWith("ROLE_")) {
                role = role.substring(5);    // Remove the ROLE_ prefix
            }

            String key = request.getMethod() + request.getRequestURI() + role;

            // If this key is found in the map, then the user is authorized
            boolean isRoleFoundInMap = this.mapUrlToRole.containsKey(key);

            if (isRoleFoundInMap) {
                return true;
            }
        }

        // If I got this far, then the role was not found in the map
        return false;
    }

}