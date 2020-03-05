package com.lessons.services;

import com.lessons.models.GetReportDTO;
import com.lessons.models.ReportByIdDTO;
import com.lessons.models.AddReportDTO;
import com.lessons.models.UpdateReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportsService {
    private static final Logger logger = LoggerFactory.getLogger(ReportsService.class);

    @Resource
    private DataSource dataSource;

    @Value("${bypass.id}")
    private int bypassId;

    @Value("${development.mode}")
    private boolean developmentMode;

    public ReportsService() {
        logger.debug("ReportsService constructor called");
    }

    @PostConstruct
    public void init() {
        logger.debug("ReportsService post constructor called");
    }

    public void addRecord(AddReportDTO addReportDTO) {
        logger.debug("ReportsService addRecord called.");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "INSERT INTO reports(id, description, reviewed, priority) " +
                "VALUES (?, ?, ?, ?)";
        int rowsAltered = jt.update(sql, getNextTableId(), addReportDTO.getDescription(), addReportDTO.getReviewed(), addReportDTO.getPriority());

        if (rowsAltered != 1) {
            throw new RuntimeException("Expected to insert one (1) record. The number of records inserted is: " + rowsAltered + ". That's bad and you're dumb.");
        }
    }

    public ReportByIdDTO getRecordById(Integer id) {
        logger.debug("ReportsService getRecordById called.");

        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(ReportByIdDTO.class);
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT id, reviewed, description, priority, created_date, last_modified_date FROM reports\n" +
                "WHERE id = ?";
        ReportByIdDTO reportByIdDTO = (ReportByIdDTO) jt.queryForObject(sql, rowMapper, id);

        return reportByIdDTO;
    }

    public int deleteRecordById(Integer id) {
        logger.debug("ReportsService deleteRecordById() called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "DELETE FROM reports WHERE id = ?";

        int rowsUpdated = jt.update(sql, id);

        return rowsUpdated;
    }

    public void updateRecord(UpdateReportDTO updateReportDTO) {
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

        int rowsUpdated = jt.update(sql, updateReportDTO.getVersion(), updateReportDTO.getDescription(),
                updateReportDTO.getDisplay_name(), updateReportDTO.getPriority(),
                updateReportDTO.getReference_source(), updateReportDTO.getId());

        if (rowsUpdated == 0) {
            throw new RuntimeException("Record update failed, 0 rows affected.");
        }

    }

    public Map<String, Object> updateRecordWithNamedVariables(UpdateReportDTO updateReportDTO) {
        logger.debug("ReportService updateRecordWithNamedVariables() called");

        // HashMap is a type/implementation of Map
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("reference_source", updateReportDTO.getReference_source());
        variableMap.put("version", updateReportDTO.getVersion());
        variableMap.put("description", updateReportDTO.getDescription());
        variableMap.put("display_name", updateReportDTO.getDisplay_name());
        variableMap.put("priority", updateReportDTO.getPriority());
        variableMap.put("id", updateReportDTO.getId());
        // Order of map put() commands doesn't matter!

        // MUST use the NamedParameterJdbcTemplat to use :namedparams
        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(dataSource);
        String sql = "UPDATE reports " +
                "SET version = :version, " +
                "    description = :description, " +
                "    display_name = :display_name, " +
                "    priority = :priority, " +
                "    reference_source = :reference_source, " +
                "    last_modified_date = now() " +
                "WHERE id = :id " +
                "RETURNING *";
        // The :namedParam must be the same as the key in the map

        Map<String, Object> updateMap = np.queryForMap(sql, variableMap);


        if (updateMap == null) {
            throw new RuntimeException("Record was not updated.");
        }

        return updateMap;

    }


    public void updateReportsAudit(Map<String, Object> rowMap, String username) {
        rowMap.put("rev_type", 1);
        rowMap.put("username", username);
        rowMap.put("rev", getNextTableId());

        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(dataSource);

        String sql = "INSERT INTO reports_aud(id, version, description, display_name, reviewed, " +
                "reference_source, priority, created_date, last_modified_date, is_custom_report, " +
                "reserved, reserved_by, rev, rev_type, username, timestamp)" +
                "VALUES (:id, :version, :description, :display_name, :reviewed, " +
                ":reference_source, :priority, :created_date, :last_modified_date, :is_custom_report, " +
                ":reserved, :reserved_by, :rev, :rev_type, :username, now())";

        np.update(sql, rowMap);
    }

    public void updateRecordTransaction(final UpdateReportDTO dto, final String username) {
        logger.debug("updateRecordAudAsTransaction() started");

        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(new DataSourceTransactionManager(dataSource));

        // This transaction will throw a TransactionTimedOutException after 60 seconds (causing the transaction to rollback)
        tt.setTimeout(60);

        tt.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus aStatus) {

                Map<String, Object> rowMap = updateRecordWithNamedVariables(dto);
                updateReportsAudit(rowMap, username);

            }
        });

        logger.debug("updateRecordAudAsTransaction() finished");
    }

    public boolean doesReportIdExist(int reportId) {
        logger.debug("ReportsService doesReportIdExist called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT * FROM reports WHERE id = ?";
        SqlRowSet sqlRowSet = jt.queryForRowSet(sql, reportId);

        return sqlRowSet.next();
    }

    public boolean isLatestVersion(int currentVersion, int recordId){

        logger.debug("ReportsService isLatestVersion() called");

        String sql = "SELECT version FROM reports WHERE id = ?";
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        int previousVersion = jt.queryForObject(sql, Integer.class, recordId);

        return currentVersion >= previousVersion;
    }

    public String getLastUpdateUser(int reportId){
        logger.debug("ReportsService getLastUpdateUser() called");

        String sql = "";

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        String username = jt.queryForObject(sql, String.class, reportId);

        return username;

    }

    /*  HELPER METHODS   */

    private int getNextTableId() {
        logger.debug("ReportsService getNextTableId() called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT nextval('seq_table_ids')";

        int tableID = jt.queryForObject(sql, Integer.class);

        return tableID;
    }


    public List<GetReportDTO> getAllReports() {

        String sql = "SELECT id, display_name, description, priority FROM reports ORDER BY id";

        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(GetReportDTO.class);

        JdbcTemplate jt = new JdbcTemplate(dataSource);

        List<GetReportDTO> listOfReports = jt.query(sql, rowMapper);

        return listOfReports;
    }
}
