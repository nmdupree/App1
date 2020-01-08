package com.lessons.sync.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.lessons.sync.config.ElasticSearchResources;
import com.lessons.sync.models.ErrorsDTO;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service("com.lessons.sync.services.ElasticSearchService")
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    @Resource
    private ElasticSearchResources elasticSearchResources;

    @Resource
    private DataSource dataSource;

    private ObjectMapper objectMapper;

    private String elasticSearchUrl;
    private AsyncHttpClient asyncHttpClient;
    private final int ES_REQUEST_TIMEOUT_IN_MILLISECS = 30000;   // All ES requests timeout after 30 seconds


    @PostConstruct
    public void init() {
        logger.debug("init() started.");

        // In order to make outgoing calls to ElasticSearch you need 2 things:
        //  1) The elastic search url
        //  2) The initialiaed AsyncHttpClient object
        this.elasticSearchUrl = elasticSearchResources.getElasticSearchUrl();
        this.asyncHttpClient = elasticSearchResources.getAsyncHttpClient();

        this.objectMapper = new ObjectMapper();
    }


    public void createIndex(String aIndexName, String aJsonMapping) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        // Make a synchronous POST call to ElasticSearch to create this an index
        Response response = this.asyncHttpClient.preparePut(this.elasticSearchUrl + "/" + aIndexName)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonMapping)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in createIndex:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        logger.info("Successfully created this ES index: {}", aIndexName);
    }

    public void submitBulkJson(String aJson) throws Exception {
        if (StringUtils.isEmpty(aJson)) {
            throw new RuntimeException("The passed-in json is null or empty.");
        }

        // Make a synchronous POST call to ElasticSearch to add records
        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/_bulk")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJson)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in createIndex:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        // Check the response body for errors true
        String jsonResponse = response.getResponseBody();

        ErrorsDTO errorsDTO = objectMapper.readValue(jsonResponse, ErrorsDTO.class);

        if (errorsDTO.isErrors()) {
            logger.error("The JSON string contained errors.");

            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in submitBulkJsonRequest:  There were errors performing this bulk index.  ES returned this message:  " + response.getResponseBody());
        }

        logger.info("Successfully added new records");
    }

    public List<String> getIndexNamesWithAlias(String aliasName) throws Exception {

        // Make sure the provided alias isn't null
        if(aliasName == null || aliasName.isEmpty()){
            throw new RuntimeException("aliasName was null or empty");
        }

        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/aliases/" + aliasName)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200){
            throw new RuntimeException("\"Error in getIndexNamesWithAlias:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        String jsonResponse = response.getResponseBody();
        List<Map<String, Object>> listOfMaps = objectMapper.readValue(jsonResponse, new TypeReference<List<Map<String,Object>>>(){});

        List<String> listOfIndices = new ArrayList<>();
        for (Map<String, Object> currentMap : listOfMaps){
            String indexName = (String)currentMap.get("index");
            if (StringUtils.isNotEmpty(indexName)){
                listOfIndices.add(indexName);
            }
        }

        return listOfIndices;
    }


    public void swapAlias(String aJsonBody) throws Exception {

        // Make sure the provided alias isn't null
        if(aJsonBody == null || aJsonBody.isEmpty()){
            throw new RuntimeException("aliasSwapJson was null or empty");
        }

        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/_aliases")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "spplication/json")
                .setBody(aJsonBody)
                .execute()
                .get();

        if (response.getStatusCode() != 200){
            throw new RuntimeException("\"Error in swapAlias:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

    }

    public List<String> getIndexNamesWithPrefix(String prefix) throws Exception{

        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/indices/" + prefix + "*")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200){
            throw new RuntimeException("\"Error in getIndexNamesWithPrefix:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        String jsonResponse = response.getResponseBody();
        List<Map<String, Object>> listOfMaps = objectMapper.readValue(jsonResponse, new TypeReference<List<Map<String,Object>>>(){});

        List<String> listOfIndices = new ArrayList<>();
        for (Map<String, Object> currentMap : listOfMaps){
            String indexName = (String)currentMap.get("index");
            if (StringUtils.isNotEmpty(indexName)){
                listOfIndices.add(indexName);
            }
        }

        return listOfIndices;
    }

    public void deleteIndex(String index) throws Exception {

        if(index == null || index.isEmpty()){
            throw new RuntimeException("index was null or empty");
        }

        Response response = this.asyncHttpClient.prepareDelete(this.elasticSearchUrl + "/" + index)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200){
            throw new RuntimeException("\"Error in deleteIndex:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }


    }
}