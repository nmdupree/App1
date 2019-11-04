package com.lessons.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

@Service
public class DashboardService
{
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Resource
    private DataSource dataSource;             // Injected data source


    public DashboardService()
    {
        logger.debug("DashboardService() constructor called");
    }

    @PostConstruct
    public void init()
    {
        logger.debug("DashboardService post constructor called.");
    }


    public String getDatabaseTime()
    {
        logger.debug("getDatabaseTime() started.");

        // Construct a SQL select query to get the current date time
        String sql = "Select NOW()";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        // Get a connection from the connection pool, run the query, return the value, return the connection to the connection pool
        String sDateTime = jt.queryForObject(sql, String.class);

        // This method returns date/time as a string
        logger.debug("Database Time is {}", sDateTime);
        return sDateTime;
    }
}