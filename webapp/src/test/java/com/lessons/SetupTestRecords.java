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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        int totalRecordsToCreate = 5;
        int maxLoops = 1;
        int totalRecordsPerUpdate = totalRecordsToCreate/maxLoops;

        for (int i = 0; i < maxLoops; i++) {
            String sql = createIndicatorsInsertStatement(totalRecordsPerUpdate);
            updateDatabase(sql);
            logger.debug("{} of {} Inserts Completed. Time elapsed: {}ms", i, maxLoops, System.currentTimeMillis() - startTime);
        }

        long totalMS = System.currentTimeMillis() - startTime;
        String timeElapsed = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(totalMS),
                TimeUnit.MILLISECONDS.toSeconds(totalMS) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalMS))
        );

        logger.debug("Total time elapsed: {}", timeElapsed);
    }

    private void updateDatabase(String sql){
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        jt.update(sql);
    }

    /*
        Build methods for a multi-line insert into the reports table
     */

    /*
        Build method for a multi-line insert into the indicators table
     */

    private String createIndicatorsInsertStatement(int totalLines){
        StringBuilder sb = new StringBuilder();

        // Add the INSERT statement
        sb.append("INSERT INTO indicators (id, type, value) VALUES ");

        // Generate multi-line randomized data
        String baseSql = "(%d, %d, '%s'),";
        int idStartValue = 1000;

        for (int i = idStartValue; i < (totalLines + i); i++) {

            // Determine random type and value
            int indicatorType;
            String indicatorValue;

            if (getRandomBool() == true) {
                indicatorType = 3;
                indicatorValue = createFakeDomain();
            } else {
                indicatorType = 5;
                indicatorValue = createFakeIp();
            }

            // Insert the randomized variables into the baseValue string using String.format
            // Append that new string to the StringBuilder
            sb.append(String.format(baseSql, i, indicatorType, indicatorValue));

            if (i % 10000 == 0) {
                logger.debug("{} of {} value clauses appended", i, totalLines);
            }
        }
        // Strip out the extra comma
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


    /*
        Random data generation methods
     */

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

    private String createFakeDomain(){
        StringBuilder sb = new StringBuilder();
        List wordList = getDictionaryWords();

        if (wordList.size() > 0){
            while (sb.length() < 5){
                sb.append(wordList.get(getRandomInt(0, wordList.size()-1)));
            }
        }
        else{
            while (sb.length() < 12){
                char aChar = (char) ('a' + getRandomInt(0, 25));
                sb.append(aChar);
            }
        }

        int randInt = getRandomInt(0,4);
        switch (randInt){
            case 0:
                sb.append(".org");
                break;
            case 1:
                sb.append(".net");
                break;
            case 2:
                sb.append(".edu");
                break;
            default:
                sb.append(".com");
        }

        return sb.toString();
    }


    private List<String> getDictionaryWords(){
        List<String> wordList = new ArrayList<String>();

        try{
            URL url = new URL("http://www-personal.umich.edu/~jlawler/wordlist");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String aWord;

            while ((aWord = bufferedReader.readLine()) != null){
                aWord = bufferedReader.readLine().toString();
                wordList.add(aWord);
            }
            bufferedReader.close();
        }catch (Exception e){
            logger.warn("Could not pull dictionary from requested site.");
        }

        return wordList;
    }

}
