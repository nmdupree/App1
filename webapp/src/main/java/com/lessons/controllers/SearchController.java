package com.lessons.controllers;

import com.lessons.models.SearchElasticDTO;
import com.lessons.services.ElasticSearchService;
import com.lessons.services.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Controller("com.lessons.controllers.SearchController")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final int PAGE_SIZE_LIMIT = 10000;

    @Resource
    private ElasticSearchService elasticSearchService;

    public SearchController(){

        logger.debug("SearchController constructor called.");
    }

    @PostConstruct
    public void init(){

        logger.debug("SearchController post constructor called.");
    }

    @RequestMapping(value = "/api/search", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> searchElastic(@RequestBody SearchElasticDTO searchParams) throws Exception {

        logger.debug("searchElastic method called");

        if (StringUtils.isBlank(searchParams.getIndexName())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Index_name may not be null or blank.");
        }

        if (searchParams.getPageSize() < 1 || searchParams.getPageSize() >= PAGE_SIZE_LIMIT){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Page_size must greater than or equal to 1 and less than or equal to" + PAGE_SIZE_LIMIT);
        }

        if (searchParams.getStartingRecordNumber() < 1){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Starting_record_number must be greater than or equal to 1");
        }

        String jsonResponse = elasticSearchService.queryIndex(searchParams);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jsonResponse);
    }
}
