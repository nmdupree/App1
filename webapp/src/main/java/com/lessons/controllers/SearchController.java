package com.lessons.controllers;

import com.lessons.models.SearchElasticDTO;
import com.lessons.models.SortDTO;
import com.lessons.services.ElasticSearchService;
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


        // Verify the index name is not null or blank
        if (StringUtils.isBlank(searchParams.getIndexName())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Index_name may not be null or blank.");
        }
        // Verify the index name exists in ElasticSearch
        if (!elasticSearchService.isIndexValid(searchParams.getIndexName())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Index_name value is not a valid index.");
        }
        // Verify the page size falls within the accepted range
        if (searchParams.getPageSize() < 1 || searchParams.getPageSize() >= PAGE_SIZE_LIMIT){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Page_size must greater than or equal to 1 and less than or equal to" + PAGE_SIZE_LIMIT);
        }
        // Verify the starting record number falls within the accepted range
        if (searchParams.getStartingRecordNumber() < 1){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Starting_record_number must be greater than or equal to 1");
        }
        // Verify sorting fields, if supplied, are not null or blank
        // ALSO verify sort direction are one of the accepted values
        // ALSO verify sort fields are one of the accepted values
        if (searchParams.getSorts() != null) {
            for (SortDTO sort : searchParams.getSorts()) {
                if (StringUtils.isBlank(sort.getDirection())) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("If sorting is specified, direction may not be null or blank.");
                } else if (!sort.getDirection().equalsIgnoreCase("desc") && !sort.getDirection().equalsIgnoreCase("asc")) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Sort direction supplied for field " + sort.getField() + " is invalid. Please enter either \"asc\" or \"desc\".");
                }
                if (StringUtils.isBlank(sort.getField())) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("If sorting is specified, direction may not be null or blank.");
                }
                else if (!elasticSearchService.isSortFieldValid(searchParams.getIndexName(), sort.getField())){
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(sort.getField() + " is not a sortable field.");
                }
            }
        }

        String jsonResponse = elasticSearchService.queryIndex(searchParams);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jsonResponse);
    }
}
