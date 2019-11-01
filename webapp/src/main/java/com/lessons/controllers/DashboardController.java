package com.lessons.controllers;


import com.lessons.services.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Controller("com.lessons.controllers.DashboardController")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Resource
    private DashboardService dashboardService;

    public DashboardController()
    {
        logger.debug("DashboardController constructor called.");
    }

    @PostConstruct
    public void init()
    {
        logger.debug("DashboardController post constructor called.");
    }

    /*************************************************************************
     * REST endpoint /api/dashboard/time
     *
     * @return a plain-old string with the database time (not JSON)
     *************************************************************************/
    @RequestMapping(value = "/api/dashboard/time", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getDateTime() {
        logger.debug("getDashboardDetails() started.");

        // Get the date/time from the database
        String sDateTime = dashboardService.getDatabaseTime();

        // Return the date/time string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(sDateTime);
    }
}
