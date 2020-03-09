package com.lessons.services;

import com.lessons.models.FilterBabyDTO;
import com.lessons.models.FilterDaddyDTO;
import com.lessons.models.SqlInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class FilterService {
    private static final Logger logger = LoggerFactory.getLogger(FilterService.class);

    public static List<String> validFilterTypes = Arrays.asList("string", "number", "date");





    public FilterService(){
        logger.debug("FilterService constructor called.");
    }

    @PostConstruct
    public void init(){
        logger.debug("FilterService post constructor called.");
    }

    public SqlInfoDTO getSqlInfoForFilters(FilterDaddyDTO filterParams){

        // Make the return object
        SqlInfoDTO sqlClauses = new SqlInfoDTO();

        // Make LIMIT clause
        String limitClause = "LIMIT " + filterParams.getPageSize() + " OFFSET " + filterParams.getOffset();
        // Append LIMIT clause
        sqlClauses.setLimitClause(limitClause);

        // Make WHERE clause
        List<FilterBabyDTO> filterList = filterParams.getFilters();

        if (filterList != null && !filterList.isEmpty()){
            sqlClauses.setWhereClause(generateWhereClause(filterList));
        }

        return sqlClauses;
    }

    private String generateWhereClause(List<FilterBabyDTO> filterList) {

        StringBuilder whereSB = new StringBuilder();







        return "";
    }

}
