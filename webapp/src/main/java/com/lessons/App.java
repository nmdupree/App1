package com.lessons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application
 **/
@SpringBootApplication
public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);


    /**
     * Web Application Starts Here
     **/
    public static void main( String[] args ) throws Exception
    {
        logger.debug("main() started.");

        // Start up Spring Boot
        SpringApplication.run(App.class, args);

        logger.debug("WebApp is Up.");
    }
}
