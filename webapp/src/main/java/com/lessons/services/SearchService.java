package com.lessons.services;

import com.lessons.models.SearchElasticDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public String searchElasticIndex(SearchElasticDTO searchParams) {
        logger.debug("searchElasticIndex called");

        String queryBase =
                "{\n" +
                "  \"size\": %d,\n" +
                "  \"from\": %d,\n" +
                "  \"query\": {\n" +
                "    \"query_string\": {\n" +
                "      \"query\": \"%s\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String query = String.format(
                queryBase,
                searchParams.getPageSize(),
                searchParams.getStartingRecordNumber(),
                searchParams.getRawQuery()
        );

        logger.debug("Query: {}", query);

        return null;
    }
}
