package com.lessons.services;

import com.lessons.models.SortDTO;
import com.lessons.models.FilteredIndicatorReturnDTO;
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

    public List<FilteredIndicatorReturnDTO> getFilteredIndicators(int size, int startingRecord, List<SortDTO> sortFields){
        logger.debug("IndicatorService getFilteredIndicators called");

        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(FilteredIndicatorReturnDTO.class);
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = createFilteredIndicatorsSql(size, startingRecord, sortFields);

        List<FilteredIndicatorReturnDTO> indicatorsDTOList = jt.query(sql, rowMapper);

        return indicatorsDTOList;
    }

    private String createFilteredIndicatorsSql(int size, int startingRecord, List<SortDTO> sortFields){
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT i.id, t.type AS type, i.value FROM indicators i JOIN types t ON i.type = t.id ");
        sb.append(createSortOrderClause(sortFields));


        sb.append("LIMIT ");
        sb.append(size);
        sb.append(" OFFSET ");
        sb.append(startingRecord);

        return sb.toString();
    }

    private String createSortOrderClause(List<SortDTO> sortParamsDTOS){
        StringBuilder sb = new StringBuilder();

        if (sortParamsDTOS == null || sortParamsDTOS.isEmpty()){
            return "";
        }
        sb.append("ORDER BY");
        for (int i = 0; i < sortParamsDTOS.size(); i++){
            SortDTO currentDto = sortParamsDTOS.get(i);
            sb.append(" ");
            sb.append(currentDto.getField());
            sb.append(" ");
            sb.append(currentDto.getDirection());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() -1);
        sb.append(" ");

        return sb.toString();

    }

    public boolean areSortDirectionsValid(List<SortDTO> sortDTOList){

        // If the list is null or empty, user elected not to use sorting
        if (sortDTOList == null || sortDTOList.isEmpty()){
            return true;
        }

        // If the direction field does not equal (case insensitive) "ASC" or "DESC", the request fails the checks
        for (SortDTO sortDTO : sortDTOList){
            String direction = sortDTO.getDirection();
            if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
                return false;
            }
        }
        return true;
    }

    public boolean areSortFieldsPresent(List<SortDTO> sortDTOList){

        // If the list is null or empty, user elected not to use sorting
        if (sortDTOList == null || sortDTOList.isEmpty()){
            return true;
        }

        // If the DTO exists but a field is empty, the request fails the checks
        for (SortDTO sortDTO : sortDTOList){
            if (sortDTO.getField() == null || sortDTO.getDirection() == null){
                return false;
            }
        }

        return true;
    }

}
