package com.lessons.services;

import com.lessons.models.CountermeasureAddDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CountermeasureService {
    public static final Logger logger = LoggerFactory.getLogger(CountermeasureService.class);

    @Resource
    private DataSource dataSource;

    public void addCountermeasure(CountermeasureAddDTO cmDTO){

        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(dataSource);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("value", cmDTO.getValue());
        paramMap.put("status", cmDTO.getStatus());
        paramMap.put("startDate", cmDTO.getStartDate());
        paramMap.put("endDate", cmDTO.getEndDate());

        String sql = "INSERT INTO countermeasures(id, version, value, status, start_date, end_date) " +
                        "VALUES (nextval('seq_table_ids'), 1, :value, :status, :startDate, :endDate)";

        np.update(sql, paramMap);

    }


}
