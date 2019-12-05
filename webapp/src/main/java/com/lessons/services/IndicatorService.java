package com.lessons.services;

import com.lessons.models.FilteredIndicatorInputParamsDTO;
import com.lessons.models.FilteredIndicatorsDTO;
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
import java.util.logging.Filter;

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

    public List<FilteredIndicatorsDTO> getFilteredIndicators(int size, int startingRecord, List<String> sortFields){
        logger.debug("IndicatorService getFilteredIndicators called");

        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(FilteredIndicatorsDTO.class);
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = createFilteredIndicatorsSql(size, startingRecord, sortFields);

        List<FilteredIndicatorsDTO> indicatorsDTOList = jt.query(sql, rowMapper);

        return indicatorsDTOList;
    }

    private String createFilteredIndicatorsSql(int size, int startingRecord, List<String> sortFields){
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT i.id, t.name AS type, i.value FROM indicators i JOIN indicator_types t ON i.type = t.id ");

        if (!sortFields.isEmpty()){
            sb.append("ORDER BY ");
            sb.append(sortFields.get(0));
            for (int i = 1; i < sortFields.size(); i++){
                sb.append(",");
                sb.append(sortFields.get(i));
            }
            sb.append(" ");
        }

        sb.append("LIMIT ");
        sb.append(size);
        sb.append(" OFFSET ");
        sb.append(startingRecord);

        return sb.toString();
    }

}
