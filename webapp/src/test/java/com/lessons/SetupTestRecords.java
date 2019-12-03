package com.lessons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SetupTestRecords {
    private static final Logger logger = LoggerFactory.getLogger(SetupTestRecords.class);

    @Resource
    private DataSource dataSource;

    @Test
    public void createTestReports(){
        logger.debug("SetupTestRecords createTestRecords() called");
        long startTime = System.currentTimeMillis();
        int recordsCreated = 50000;
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "INSERT INTO reports (id, version, display_name, reviewed, priority, created_date)" +
                     "VALUES (nextval('seq_table_ids'), ?, ?, ?, ?, now())";

        for (int i = 0; i < recordsCreated; i++) {
            int version = getRandomInt(1, 9);
            String displayName = "display name " + i;
            boolean reviewed = getRandomBool();
            int priority = getRandomInt(1, 5);

            jt.update(sql, version, displayName, reviewed, priority);

            if (i % 1000 == 0) {
                logger.debug("{} of 5000 Completed. Time elapsed: {}ms", i, System.currentTimeMillis() - startTime);
            }
        }
        logger.debug("Total time elapsed: {}ms", System.currentTimeMillis() - startTime);
        // 6678.0ms
    }

    @Test
    public void createTestReports2(){
        logger.debug("SetupTestRecords createTestRecords() called");
        long startTime = System.currentTimeMillis();
        int recordsCreated = 5;
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO reports (id, version, display_name, reviewed, priority, created_date) VALUES ");

        for (int i = 0; i < recordsCreated; i++){

            int version = getRandomInt(1,9);
            String displayName = "display name " + i;
            boolean reviewed = getRandomBool();
            int priority = getRandomInt(1,5);

            String valuesClause = "(nextval('seq_table_ids'), %d, '%s', %b, %d, now()), ";
            sb.append(String.format(valuesClause, version, displayName, reviewed, priority));

        }

        String prelimSql = sb.toString();



        //.update(sql);
        logger.debug("Total time elapsed: {}ms", System.currentTimeMillis() - startTime);
    }

    // HOMEWORK
    // Create 3 million indicator records (id, type, value)
    // Make it fast <2 minutes
    // Type 3 - String (domain)
    // Type 5 - IP Address

    @Test
    public void createTestIndicators(){
        logger.debug("CreateTestIndicators() called.");

        long startTime = System.currentTimeMillis();
        // How many records should be added
        int recordsCreated = 5;

        // Object needed to query the database
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        // Create some strings to be manipulated by String.format and StringBuilder
        String baseInsert = "INSERT INTO indicators (id, type, value) VALUES ";
        String baseValue = "(nextval('seq_table_ids'), %d, '%s'),";

        // Create a StringBuilder object
        StringBuilder stringBuilder = new StringBuilder();
        // Add the baseInsert String to the stringbuilder object
        stringBuilder.append(baseInsert);

        for (int i = 0; i < recordsCreated; i++){
            // Determine random type and value
            int indicatorType;
            String indicatorValue;

            if (getRandomBool() == true){
                indicatorType = 3;
                indicatorValue = "myDomain.com";
            }
            else{
                indicatorType = 5;
                indicatorValue = createFakeIp();
            }

            // Insert the randomized variables into the baseValue string using String.format
            String modifiedValue = String.format(baseValue, indicatorType, indicatorValue);
            // Append the formatted string to the stringBuilder object
            stringBuilder.append(modifiedValue);
        }
        String prelimSql = stringBuilder.toString();
        String finalSql = StringUtils.substring(prelimSql, 0, prelimSql.length()-1);
        jt.update(finalSql);

        logger.debug("Total time elapsed: {}ms", System.currentTimeMillis() - startTime);
    }

    private int getRandomInt(int min, int max){
        return (int) ((Math.random() * ((max - min) + 1)) + min);
    }

    private boolean getRandomBool(){
        double randomDouble = Math.random();
        int randomNum = (int) (randomDouble * 100);
        if (randomNum % 2 == 0){
            return true;
        }
        return false;
    }

    private String createFakeIp(){
        int firstOct = getRandomInt(1, 191);
        int secondOct = getRandomInt(1,255);
        int thirdOct = getRandomInt(1,255);
        int fourthOct = getRandomInt(1,255);

        String ipAddress = firstOct + "." + secondOct + "." + thirdOct + "." + fourthOct;
        return ipAddress;
    }


}
