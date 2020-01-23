package com.lessons.services;

import com.lessons.config.ElasticSearchResources;
import com.lessons.models.SearchElasticDTO;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

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


    @PostConstruct
    public void init() {
        logger.debug("init() started.");

        // In order to make outgoing calls to ElasticSearch you need 2 things:
        //  1) The elastic search url
        //  2) The initialiaed AsyncHttpClient object
        this.elasticSearchUrl = elasticSearchResources.getElasticSearchUrl();
        this.asyncHttpClient = elasticSearchResources.getAsyncHttpClient();
    }


    public String queryIndex(SearchElasticDTO searchParams) throws Exception {

        String indexName = searchParams.getIndexName();
        if (StringUtils.isEmpty(indexName)){
            throw new RuntimeException("The passed-in indexName is null or empty.");
        }

        String jsonQuery = createJsonQuery(searchParams);

        // Make a synchronous POST call to ElasticSearch to create this an index
        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/" + indexName + "/_search")
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

        String jsonQuery;
        String queryBase;

        if (StringUtils.isBlank(searchParams.getRawQuery())){
            queryBase =
                    "{\n" +
                            "  \"size\": %d,\n" +
                            "  \"from\": %d,\n" +
                            "  \"query\": {\n" +
                            "    \"match_all\": {}\n" +
                            "  }\n" +
                            "}";

            jsonQuery = String.format(
                    queryBase,
                    searchParams.getPageSize(),
                    searchParams.getStartingRecordNumber()
            );

        }
        else {
            queryBase =
                    "{\n" +
                    "  \"size\": %d,\n" +
                    "  \"from\": %d,\n" +
                    "  \"query\": {\n" +
                    "    \"query_string\": {\n" +
                    "      \"query\": \"%s\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            jsonQuery = String.format(
                    queryBase,
                    searchParams.getPageSize(),
                    searchParams.getStartingRecordNumber(),
                    searchParams.getRawQuery()
            );
        }

        logger.debug("Query: {}", jsonQuery);

        return jsonQuery;
    }
}
