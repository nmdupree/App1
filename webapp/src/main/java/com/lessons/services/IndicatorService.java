package com.lessons.services;

import com.lessons.models.IndicatorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Service
public class IndicatorService {
    public static final Logger logger = LoggerFactory.getLogger(IndicatorService.class);

    @Resource
    private DataSource dataSource;

    public IndicatorService(){logger.debug("IndicatorService constructor called.");}

    @PostConstruct
    public void init(){logger.debug("IndicatorService post constructor called.");}

    public int getCount(){
        logger.debug("IndicatorService getCount called.");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT COUNT (*) FROM indicators";
        int indicatorRecordCount = jt.queryForObject(sql, Integer.class);
        return indicatorRecordCount;
    }

    public List<IndicatorDTO> getAllIndicators(){
        logger.debug("IndicatorService getIndicators called");

        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(IndicatorDTO.class);
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT id, type, value FROM indicators";

        List<IndicatorDTO> indicatorDTOList = jt.query(sql, rowMapper);

        return indicatorDTOList;
    }

}
