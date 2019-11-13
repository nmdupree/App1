package com.lessons.services;

import com.lessons.models.ReportByIdDTO;
import com.lessons.models.AddReportDTO;
import com.lessons.models.UpdateReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportsService {
    private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);

    @Resource
    private DataSource dataSource;

    public ReportsService(){logger.debug("ReportsService constructor called");}

    @PostConstruct
    public void init(){logger.debug("ReportsService post constructor called");}

    public void addRecord(AddReportDTO addReportDTO){
        logger.debug("ReportsService addRecord called.");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql =    "INSERT INTO reports(id, description, reviewed, priority) " +
                        "VALUES (?, ?, ?, ?)";
        int rowsAltered= jt.update(sql, getNextTableId(), addReportDTO.getDescription(), addReportDTO.getReviewed(), addReportDTO.getPriority());

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

    public void updateRecord(UpdateReportDTO updateReportDTO){
        logger.debug("ReportService updateRecord() called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "UPDATE reports " +
                        "SET version = ?, " +
                        "    description = ?, " +
                        "    display_name = ?, " +
                        "    priority = ?, " +
                        "    reference_source = ?, " +
                        "    last_modified_date = now()" +
                        "WHERE id = ?";

        int rowsUpdated = jt.update(sql,    updateReportDTO.getVersion(), updateReportDTO.getDescription(),
                                            updateReportDTO.getDisplay_name(), updateReportDTO.getPriority(),
                                            updateReportDTO.getReference_source(), updateReportDTO.getId());

        if (rowsUpdated == 0){
            throw new RuntimeException("Record update failed, 0 rows affected.");
        }

    }

    public void updateRecordWithNamedVariables(UpdateReportDTO updateReportDTO){
        logger.debug("ReportService updateRecordWithNamedVariables() called");

        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("version", updateReportDTO.getVersion());
        variableMap.put("description", updateReportDTO.getDescription());
        variableMap.put("display_name", updateReportDTO.getDisplay_name());
        variableMap.put("priority", updateReportDTO.getPriority());
        variableMap.put("reference_source", updateReportDTO.getReference_source());
        variableMap.put("id", updateReportDTO.getId());

        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);
        String sql = "UPDATE reports " +
                "SET version = :version, " +
                "    description = :description, " +
                "    display_name = :display_name, " +
                "    priority = :priority, " +
                "    reference_source = :reference_source, " +
                "    last_modified_date = now()" +
                "WHERE id = :id";

        int rowsUpdated = np.update(sql, variableMap);

        if (rowsUpdated == 0){
            throw new RuntimeException("Record update failed, 0 rows affected.");
        }

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
