package com.lessons.sync.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service("com.lessons.sync.services.RefreshService")
public class RefreshService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshService.class);

    private boolean isRefreshInProgress = false;

    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource
    private DataSource dataSource;

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
            String esNewIndexName = aAliasName + getCurrentDateTime();
            elasticSearchService.createIndex(esNewIndexName, jsonMapping);
        }
        finally {
            isRefreshInProgress = false;
        }
    }


    /**************************************************************
     * getCurrentDateTime()
     **************************************************************/
    private static String getCurrentDateTime()
    {
        DateFormat df = new SimpleDateFormat("yyyymmdd_HHmmss");
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
