package com.lessons.controllers;

import com.lessons.models.CountermeasureAddDTO;
import com.lessons.services.CountermeasureService;
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

@Controller("com.lessons.controllers.CountermeasureController")
public class CountermeasureController {
    private static final Logger logger = LoggerFactory.getLogger(CountermeasureController.class);

    @Resource
    private CountermeasureService countermeasureService;

    public CountermeasureController(){
        logger.debug("CountermeasureController constructor called");
    }

    @PostConstruct
    public void init(){
        logger.debug("CountermeasureController post constructor called");
    }

    @RequestMapping(value = "/api/countermeasures/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> addCountermeasure(@RequestBody CountermeasureAddDTO cmDTO){

        logger.debug("addCountermeasure started");

        if(cmDTO.getValue().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Value may not be null or empty");
        }
        if(cmDTO.getStartDate() == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Start date may not be null");
        }
        if(cmDTO.getEndDate() == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("End date may not be null");
        }


        countermeasureService.addCountermeasure(cmDTO);



        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("");
    }
}
