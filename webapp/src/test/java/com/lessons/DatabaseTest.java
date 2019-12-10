package com.lessons;

import com.lessons.services.DashboardService;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)                                                  // Required to work with JUnit 4
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)   // Start up a Spring Boot webapp listening on random port
public class DatabaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTest.class);

    @Value("${local.server.port}")
    private String appServerPort;       // Holds the random port number that the test app server is listening on

    private static boolean databaseIsInitialized = false;

    @Resource
    private DashboardService dashboardService;

    @Resource
    private DataSource dataSource;

    /**
     * This method gets called before every test class
     * -- We use setup() to make sure that flyway:clean and flyway:migrate is already run on the database
     * -- Alternatively, we could have the the EmbeddedDataSourceConfig run flywy:clean and flyway:migrate as well
     */
    @Before
    public void setup() {

        if (databaseIsInitialized) {
            // The database is already initialized, so do nothing
            return;
        }
        else {
            // Initialize the database by running flyway clean migrate
            databaseIsInitialized = true;

            // Run flyway clean & flyway migrate
            FlywayUtils.runCleanMigrate(this.dataSource, "sampleData1");
        }
    }


    @Test
    public void testDatabaseReportCount() {
        logger.debug("testDatabaseCalls() started.");

        // Display the jdbc url information for the embedded data source
        HikariDataSource ds = ((HikariDataSource) this.dataSource);
        logger.debug("Database is located here: {}", ds.getJdbcUrl());

        final int expectedReportCount = 3;

        // Run a database query
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "Select count(*) from reports";
        int actualReportCount = jt.queryForObject(sql, Integer.class);

        assertTrue("Report count is not what was expected", (expectedReportCount == actualReportCount));

        logger.debug("testDatabaseCalls() finished.");
    }


    @Test
    public void testDashboardService() {
        logger.debug("testDashboardService() started.");


        String databaseTime = dashboardService.getDatabaseTime();

        assertTrue("I expected the databaes time to not be null", (databaseTime != null));


        logger.debug("testDashboardService() finished.");
    }

}

