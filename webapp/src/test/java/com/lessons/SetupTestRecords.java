package com.lessons;

import com.lessons.services.ReportsService;
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
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SetupTestRecords {
    private static final Logger logger = LoggerFactory.getLogger(SetupTestRecords.class);

    @Resource
    private DataSource dataSource;


    /**
     * OUDATED :: DO NOT USE
     */
    @Test
    public void createTestReportsSlow(){
        logger.debug("SetupTestRecords createTestRecords() called");
        long startTime = System.currentTimeMillis();
        int recordsCreated = 50000;
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "INSERT INTO reports (id, version, display_name, reviewed, priority, created_date) " +
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


    /**
     * NOTE ::  Set the profile in \webapp\src\test\resources\application.yaml
     *          to "dev" to write to the production database
     */
    @Test
    public void createTestReportsFast(){
        logger.debug("SetupTestRecords createTestRecords() called");

        long startTime = System.currentTimeMillis();
        // How many records should be added
        int totalRecordsToCreate = 50000;

        String sql = createReportInsertStatement(totalRecordsToCreate);
        updateDatabase(sql);

        long totalMS = System.currentTimeMillis() - startTime;
        String timeElapsed = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(totalMS),
                TimeUnit.MILLISECONDS.toSeconds(totalMS) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalMS))
        );

        logger.debug("Total time elapsed: {}", timeElapsed);
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
        int totalRecordsToCreate = 3000000;
        int maxLoops = 6;
        int totalRecordsPerUpdate = totalRecordsToCreate/maxLoops;

        for (int i = 0; i < maxLoops; i++) {
            String sql = createIndicatorsInsertStatement(totalRecordsPerUpdate);
            updateDatabase(sql);
            logger.debug("{} of {} Inserts Completed. Time elapsed: {}ms", i+1, maxLoops, System.currentTimeMillis() - startTime);
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
    private String createReportInsertStatement(int totalLines) {
        StringBuilder sb = new StringBuilder();
        List<String> wordList = getDictionaryWords();

        // Add the INSERT statement
        sb.append("INSERT INTO reports (id, version, display_name, description, reviewed, reference_source, priority, created_date) VALUES ");

        // Generate multi-line randomized data
        String baseSql = "(%d, %d, '%s', '%s', %b, %d, %d, now()),";
        int idStartValue = getNextTableId();
        int idFinalValue = idStartValue + totalLines;

        for (int i = idStartValue; i < idFinalValue; i++) {

            // Determine random type and value
            int version = getRandomInt(0,9);
            String displayName = createFakeString(wordList, 10);
            String description  = createFakeString(wordList, 40);
            boolean reviewed = getRandomBool();
            int priority = getRandomInt(1,4);
            int referenceSource = getRandomInt(5,7);

            // Insert the randomized variables into the baseValue string using String.format
            // Append that new string to the StringBuilder
            sb.append(String.format(baseSql, i, version, displayName, description, reviewed, referenceSource, priority));

            if (i % 10000 == 0) {
                logger.debug("{} of {} value clauses appended", i, totalLines);
            }
        }
        // Strip out the extra comma
        sb.deleteCharAt(sb.length() - 1);

        // Update the sequence
        setNextTableId(idFinalValue);

        return sb.toString();
    }
    /*
        Build method for a multi-line insert into the indicators table
     */

    private String createIndicatorsInsertStatement(int totalLines){
        StringBuilder sb = new StringBuilder();
        List<String> wordList = getDictionaryWords();

        // Add the INSERT statement
        sb.append("INSERT INTO indicators (id, type, value, classification) VALUES ");

        // Generate multi-line randomized data
        String baseSql = "(%d, %d, '%s', %d),";
        int idStartValue = getNextTableId();
        int idFinalValue = idStartValue + totalLines;

        for (int i = idStartValue; i < idFinalValue; i++) {

            // Determine random type and value
            int indicatorType;
            String indicatorValue;

            if (getRandomBool() == true) {
                indicatorType = 3;
                indicatorValue = createFakeDomain(wordList);
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

        // Update the sequence
        setNextTableId(idFinalValue);

        return sb.toString();
    }


    /*
        Random data generation methods
     */

    private Timestamp createRandomTimestamp(){

        return null;
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

    private String createFakeDomain(List<String> wordList){
        StringBuilder sb = new StringBuilder();

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

    private String createFakeString(List<String> wordList, int size){
        StringBuilder sb = new StringBuilder();

        if (wordList.size() > 0){
            sb.append(wordList.get(getRandomInt(0, wordList.size()-1)));
            while (sb.length() < size){
                sb.append(" ");
                sb.append(wordList.get(getRandomInt(0, wordList.size()-1)));
            }
        }
        else{
            while (sb.length() < 12){
                char aChar = (char) ('a' + getRandomInt(0, 25));
                sb.append(aChar);
            }
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
                aWord = stripSpecialCharacters(bufferedReader.readLine().toString());
                wordList.add(aWord);
            }
            bufferedReader.close();
        }catch (Exception e){
            logger.warn("Could not pull dictionary from requested site.");
        }

        return wordList;
    }

    private String stripSpecialCharacters(String aWord){
        return aWord.replaceAll("[^a-zA-Z0-9]", "");
    }

    private int getNextTableId() {

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "SELECT nextval('seq_table_ids')";

        int tableID = jt.queryForObject(sql, Integer.class);

        return tableID;
    }

    private void setNextTableId(int nextTableId) {
        logger.debug("ReportsService getNextTableId() called");

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        String sql = "ALTER SEQUENCE seq_table_ids RESTART WITH " + nextTableId;

        jt.update(sql);
    }

    @Test
    public void testStringMethod(){
        String aWord = "asdf@#$%^&*()'";
        String aNewWord = stripSpecialCharacters(aWord);

        logger.debug("aNewWord = {}", aNewWord );
    }


}
