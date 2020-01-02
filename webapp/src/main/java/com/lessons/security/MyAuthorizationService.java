package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service("com.lessons.security.myAuthorizationService")
public class MyAuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthorizationService.class);

    @Value("${spring.profiles.active")
    private String activeProfileName;               // Inject the active spring profile name

    @PostConstruct
    public void init()
    {
        logger.debug("init() started.");
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



}