package com.lessons.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessons.config.ElasticSearchResources;
import com.lessons.models.SearchElasticDTO;
import com.lessons.models.SortDTO;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("com.lessons.sync.services.ElasticSearchService")
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    @Resource
    private ElasticSearchResources elasticSearchResources;

    @Resource
    private DataSource dataSource;



    private String elasticSearchUrl;
    private AsyncHttpClient asyncHttpClient;
    private final int ES_REQUEST_TIMEOUT_IN_MILLISECS = 30000;   // All ES requests timeout after 30 seconds
    private Map<String, Map<String, String>> allIndiciesSortFields;
    private List<String> esIndicies;


    @PostConstruct
    public void init() {
        logger.debug("init() started.");

        // In order to make outgoing calls to ElasticSearch you need 2 things:
        //  1) The elastic search url
        //  2) The initialized AsyncHttpClient object
        this.elasticSearchUrl = elasticSearchResources.getElasticSearchUrl();
        this.asyncHttpClient = elasticSearchResources.getAsyncHttpClient();

        // Hard-coded map of
        Map<String, String> reportSortFields = new HashMap<>();
        reportSortFields.put("created_date", "created_date");
        reportSortFields.put("display_name", "display_name.sort");
        reportSortFields.put("id","id");
        reportSortFields.put("priority","priority.sort");

        allIndiciesSortFields = new HashMap();
        allIndiciesSortFields.put("reports", reportSortFields);

        esIndicies  = new ArrayList<>();
        esIndicies.add("reports");

    }


    public String queryIndex(SearchElasticDTO searchParams) throws Exception {

        logger.debug("queryIndex started");

        String indexName = searchParams.getIndexName().toLowerCase();
        if (StringUtils.isEmpty(indexName)){
            throw new RuntimeException("The passed-in indexName is null or empty.");
        }

        String jsonQuery = createJsonQuery(searchParams);

        // Make a synchronous POST call to ElasticSearch to create this an index
        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/" + indexName + "/_search?pretty=true")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(jsonQuery)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in createIndex:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        String results = response.getResponseBody();

        return results;
    }

    private String createJsonQuery(SearchElasticDTO searchParams) {
        logger.debug("searchElasticIndex called");

        StringBuilder jsonQuerySB = new StringBuilder();
        // Open the elasticSearch json query
        jsonQuerySB.append("{");

        // Append the result size clause with the value specified in the SearchDTO
        String sizeBase = "\"size\": %d,";
        jsonQuerySB.append(String.format(sizeBase, searchParams.getPageSize()));

        // Append the starting record clause with the value specified in the SearchDTO
        String fromBase = "\"from\": %d,";
        jsonQuerySB.append(String.format(fromBase, searchParams.getStartingRecordNumber()));

        // Append the query clause with the value specified in the SearchDTO
        // IF that query is null, use the elasticSearch "match_all" clause
        if (StringUtils.isBlank(searchParams.getRawQuery())){
            jsonQuerySB.append(" \"query\": { \"match_all\": {} }");
        }
        else {
            // Clean up the string to prevent query errors
            String queryString = sanitizeQuery(searchParams.getRawQuery());

            String queryBase = " \"query\": { \"query_string\": { \"query\": \"%s\" }  }";
            jsonQuerySB.append(String.format(queryBase, queryString));
        }

        // If sorting is specified, append the sort clause with the value(s) specified in the SearchDTO
        List<SortDTO> sortList = searchParams.getSorts();
        if (sortList != null && sortList.size() > 0){
            // Opens the sort clause
            jsonQuerySB.append(", \"sort\": [");

            // For each listed SortDTO, append a sort map
            String sortBase = " { \"%s\": { \"order\": \"%s\" } },";
            for (SortDTO sort : sortList){
                jsonQuerySB.append(String.format(sortBase, sort.getField(), sort.getDirection()));
            }
            // Strip out the trailing comma
            jsonQuerySB.setLength(jsonQuerySB.length() - 1);

            // Close the sort clause
            jsonQuerySB.append("]");
        }

        jsonQuerySB.append("}");

        String jsonQuery = jsonQuerySB.toString();

        logger.debug("Query: {}", jsonQuery);

        return jsonQuery;
    }

    private String sanitizeQuery(String rawQuery){

        String sanitizedQuery = StringUtils.replace(rawQuery, "\"", "\\\"");

        return sanitizedQuery;
    }

    public boolean isIndexValid(String indexName) {

        return esIndicies.contains(indexName.toLowerCase());
    }

    public boolean isSortFieldValid(String indexName, String fieldName){

        if (allIndiciesSortFields.containsKey(indexName)){
            return allIndiciesSortFields.get(indexName).containsKey(fieldName);
        }
        else {
            return false;
        }
    }

    public List<String> getAllIndexes() throws Exception{

        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/indices/")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200){
            throw new RuntimeException("\"Error in getAllIndexes:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        String jsonResponse = response.getResponseBody();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> listOfMaps = objectMapper.readValue(jsonResponse, new TypeReference<List<Map<String,Object>>>(){});

        List<String> listOfIndices = new ArrayList<>();
        for (Map<String, Object> currentMap : listOfMaps){
            String indexName = (String)currentMap.get("index");
            if (StringUtils.isNotEmpty(indexName) && !isExcludedIndex(indexName)){

                listOfIndices.add(indexName);
            }
        }

        return listOfIndices;
    }

    private boolean isExcludedIndex(String indexName){
        String regexBase = "(csaac\\S*)|([.]\\S*)";
        return indexName.matches(regexBase);
    }
}
