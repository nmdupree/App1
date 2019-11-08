package com.lessons.controllers;

import com.lessons.models.IndicatorDTO;
import com.lessons.services.IndicatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
}