package com.lessons.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private DataSource dataSource;

    public UserService(){logger.debug("UserService() Constructor called.");}

    @PostConstruct
    public void init(){logger.debug("UserService() post constructor called.");}

    public int getCount(){
        logger.debug("UserService getCount called.");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT COUNT (*) FROM users";
        int userRecordCount = jt.queryForObject(sql, Integer.class);
        return userRecordCount;
    }
}
