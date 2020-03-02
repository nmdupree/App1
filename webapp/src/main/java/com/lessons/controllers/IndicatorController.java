package com.lessons.controllers;

import com.lessons.models.*;
import com.lessons.services.IndicatorService;
import com.lessons.services.LookupService;
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
import java.util.List;

@Controller("com.lessons.controllers.IndicatorController")
public class IndicatorController {
    private static final Logger logger = LoggerFactory.getLogger(IndicatorController.class);

    @Resource
    private IndicatorService indicatorService;

    @Resource
    private LookupService lookupService;

    public IndicatorController(){logger.debug("IndicatorController constructor called");}

    @PostConstruct
    public void init(){logger.debug("IndicatorController post constructor called");}


    /*************************************************************************
     * REST endpoint /api/indicators
     *
     * @return a json formatted list of indicators
     *************************************************************************/
    @RequestMapping(value = "/api/indicators", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getIndicators() {
        logger.debug("getIndicators() started.");

        List<IndicatorDTO> indicatorDTOList = indicatorService.getAllIndicators();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(indicatorDTOList);
    }

    /*************************************************************************
     * REST endpoint /api/indicators/filtered
     *
     * @return a json formatted list of indicators
     *************************************************************************/
    @RequestMapping(value = "/api/indicators/filtered", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getFilteredIndicators(@RequestBody FilteredIndicatorInputDTO paramsDTO){
        logger.debug("getFilteredIndicators() called");

        Integer pageSize = paramsDTO.getPageSize();
        Integer startingRecord = paramsDTO.getStartingRecordNumber();
        List<SortDTO> sortParamsDTOS = paramsDTO.getSorting();


        // TODO: Insert null and bad data checks
        if (pageSize == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The pageSize field is a required value.");
        }
        if (pageSize < 1){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The pageSize field must be a positive integer.");
        }
        if (startingRecord == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The startingRecord field is a required value.");
        }
        if (startingRecord < 1){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The startingRecord field must be a positive integer.");
        }
        if (!indicatorService.areSortFieldsPresent(sortParamsDTOS)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("If sorting is defined, both field and direction must be supplied.");
        }
        if (!indicatorService.areSortDirectionsValid(sortParamsDTOS)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The sorting direction does not match approved values: ASC, DESC");
        }

        List<FilteredIndicatorReturnDTO> filteredIndicatorReturnDTOList = indicatorService.getFilteredIndicators(pageSize, startingRecord, sortParamsDTOS);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredIndicatorReturnDTOList);
    }

    @RequestMapping(value = "/api/indicators/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> addIndicator(@RequestBody IndicatorAddDTO indicatorDTO){

        logger.debug("addIndicator() started.");

        if (indicatorDTO.getValue().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Indicator value may not be null or empty");
        }

        if (indicatorDTO.getClassification() == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Indicator classification may not be null");
        }

        if(lookupService.lookupIdExists(indicatorDTO.getClassification(), "classification")){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Classification with id " + indicatorDTO.getClassification() + " does not exist.");
        }

        if (indicatorDTO.getType() == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Indicator type may not be null");
        }

        if(lookupService.lookupIdExists(indicatorDTO.getType(), "indicator_type")){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Indicator type with id " + indicatorDTO.getType() + " does not exist.");
        }

        indicatorService.addIndicator(indicatorDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("");
    }



}
