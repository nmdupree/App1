package com.lessons.controllers;

import com.lessons.models.SearchElasticDTO;
import com.lessons.services.SearchService;
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

    @Resource
    private SearchService searchService;

    public SearchController(){
        logger.debug("SearchController constructor called.");
    }

    @PostConstruct
    public void init(){
        logger.debug("SearchController post constructor called.");
    }

    @RequestMapping(value = "/api/search", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> searchElastic(@RequestBody SearchElasticDTO searchParams){

        logger.debug("searchElastic method called");

        String jsonResponse = searchService.searchElasticIndex(searchParams);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(searchParams);
    }
}
