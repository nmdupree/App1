package com.lessons.sync.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lessons.sync.models.ReportDTO;
//import com.sun.org.apache.xpath.internal.operations.String;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service("com.lessons.sync.services.RefreshService")
public class RefreshService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshService.class);

    private boolean isRefreshInProgress = false;

    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource
    private DataSource dataSource;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init(){

        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

    }

    /**
     * Refresh all mappings
     *
     * @throws Exception if something bad happens
     */
    public void refreshAllMappings() throws Exception {
        logger.debug("refreshAllMappings() started.");

        // Create a new index with an alias of "reports" --> reports_YYYYMMDD__HH24MISS
        refreshMappingWithAlias("reports");

        logger.debug("refreshAllMappings() finished.");
    }


    /**
     * Refresh this mapping
     *  1. Create a new index called         reports_YYYYMMDD__HH24MISS
     *  2. Add records from reports table to reports_YYYYMMDD__HH24MISS
     *  3. Have the reports alias point to   reports_YYYYMMDD__HH24MISS
     *  4. Cleanup:  Delete any other indexes that start with reports_
     *
     * @param aAliasName the alias name -- e.g., "reports"
     * @throws Exception if something bad happens
     */
    private void refreshMappingWithAlias(String aAliasName) throws Exception {
        try {
            // Set the flag to indicate a refresh is work-in-progress
            isRefreshInProgress = true;

            String mappingFilename = aAliasName + ".mapping.json";

            // Get the json mapping as a string
            String jsonMapping = readFileInClasspathToString(mappingFilename);

            // Create a new index
            String esNewIndexName = aAliasName + "_" + getCurrentDateTime();
            elasticSearchService.createIndex(esNewIndexName, jsonMapping);

            // Add data to the new index
            addDataToIndex(esNewIndexName);

            // Switch the alias over
            setAlias(aAliasName, esNewIndexName);

            // Cleanup the leftover indicies
            cleanup(aAliasName, esNewIndexName);
        }
        finally {
            isRefreshInProgress = false;
        }
    }

    private void setAlias(String alias, String newIndexName) throws Exception {
        // Get existing indices used by the reports alias
        List<String> indices = elasticSearchService.getIndexNamesWithAlias(alias);

        // Construct JSON to make the switch
        StringBuilder sbJsonAliasPost = new StringBuilder();
        sbJsonAliasPost.append("{ \"actions\": [ {\"add\": { \"index\": \"");
        sbJsonAliasPost.append(newIndexName);
        sbJsonAliasPost.append("\", \"alias\": \"reports\" } }");

        String baseRemove = ", {  \"remove\": { \"index\": \"%s\", \"alias\": \"reports\" } }";
        for (String index : indices){
            if (!index.equalsIgnoreCase(newIndexName)){
                sbJsonAliasPost.append(String.format(baseRemove, index));
            }
        }

        sbJsonAliasPost.append(" ] }");

        // Submit the JSON to make the switch
        elasticSearchService.swapAlias(sbJsonAliasPost.toString());
    }



    /**
     * Pulls data from Postgres; builds a bulk index statement; posts to ES
     * @param esNewIndexName the name of the index to receive the
     */
    private void addDataToIndex(String esNewIndexName) throws Exception {

        String sql = "SELECT id, description, display_name, priority, created_date FROM view_all_reports LIMIT 50";
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(ReportDTO.class);

        StringBuilder bulkInsertSB = new StringBuilder();
        String base1 = "{ \"index\": { \"_index\": \"%s\", \"_type\": \"record\", \"_id\": %d }} \n";

        List<ReportDTO> reportList = jt.query(sql, rowMapper);

        for (ReportDTO report : reportList){
            bulkInsertSB.append(String.format(base1, esNewIndexName, report.getId()));
            bulkInsertSB.append(objectMapper.writeValueAsString(report) + "\n");
        }
        elasticSearchService.submitBulkJson(bulkInsertSB.toString());
    }

    private void cleanup(String prefix, String indexToNotDelete) {
        try {
            List<String> listOfIndicies = elasticSearchService.getIndexNamesWithPrefix(prefix);

            for(String index : listOfIndicies){
                if (!index.equalsIgnoreCase(indexToNotDelete)){
                    elasticSearchService.deleteIndex(index);
                }
            }
        } catch (Exception e) {
            logger.warn("An exception occured during deletion of indicies:", e);
        }
    }


    /**************************************************************
     * getCurrentDateTime()
     **************************************************************/
    private static String getCurrentDateTime()
    {
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return(df.format(new Date()));
    }


    /**
     * @param aFilename holds the name of the filename to look for in the classpath
     * @return the file contents as a string
     * @throws IOException
     */
    public static String readFileInClasspathToString(String aFilename) throws IOException {
        try (InputStream inputStream =  RefreshService.class.getResourceAsStream("/" + aFilename)) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }


    public boolean isRefreshInProgress() {
        return isRefreshInProgress;
    }
}
