package com.lessons.services;

import com.lessons.models.ReportByIdDTO;
import com.lessons.models.ReportDTO;
import jdk.nashorn.internal.scripts.JD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Service
public class ReportsService {
    private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);

    @Resource
    private DataSource dataSource;

    public ReportsService(){logger.debug("ReportsService constructor called");}

    @PostConstruct
    public void init(){logger.debug("ReportsService post constructor called");}

    public void addRecord(ReportDTO reportDTO){
        logger.debug("ReportsService addRecord called.");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql =    "INSERT INTO reports(id, description, reviewed, priority) " +
                        "VALUES (?, ?, ?, ?)";
        int rowsAltered= jt.update(sql, getNextTableId(), reportDTO.getDescription(), reportDTO.getReviewed(), reportDTO.getPriority());

        if (rowsAltered != 1) {
            throw new RuntimeException("Expected to insert one (1) record. The number of records inserted is: " + rowsAltered + ". That's bad and you're dumb.");
        }
    }

    public ReportByIdDTO getRecordById(Integer id){
        logger.debug("ReportsService getRecordById called.");

        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(ReportByIdDTO.class);
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql =    "SELECT id, reviewed, description, priority, created_date, last_modified_date FROM reports\n" +
                        "WHERE id = ?";
        ReportByIdDTO reportByIdDTO = (ReportByIdDTO) jt.queryForObject(sql, rowMapper, id);

        return reportByIdDTO;
    }

    public int deleteRecordById(Integer id){
        logger.debug("ReportsService deleteRecordById() called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "DELETE FROM reports WHERE id = ?";

        int rowsUpdated = jt.update(sql, id);

        return rowsUpdated;
    }

    private int getNextTableId(){
        logger.debug("ReportsService getNextTableId() called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT nextval('seq_table_ids')";

        int tableID = jt.queryForObject(sql, Integer.class);

        return tableID;
    }

    public boolean doesReportIdExist(int reportId){
        logger.debug("ReportsService doesReportIdExist called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT * FROM reports WHERE id = ?";
        SqlRowSet sqlRowSet = jt.queryForRowSet(sql, reportId);

        return sqlRowSet.next();
    }



}
