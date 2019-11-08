package com.lessons.controllers;

import com.lessons.models.ReportDTO;
import com.lessons.services.ReportsService;
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

@Controller("com.lessons.controllers.ReportsController")
public class ReportsController {
    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Resource
    private ReportsService reportsService;

    public ReportsController() {
        logger.debug("ReportsController constructor called");
    }

    @PostConstruct
    public void init() {
        logger.debug("ReportsController post contructor called");
    }

    /*************************************************************************
     * REST endpoint /api/getStuff
     * Call using ?id=some_long_string
     *
     * @param id whatever string you want to give it
     * @return the string value passed in as a parameter
     *************************************************************************/
    @RequestMapping(value = "/api/getStuff", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getStuff(@RequestParam String id) {
        logger.debug("id={}", id);

        String returnValue = "id=" + id;

        // Return the string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(returnValue);
    }

    /*************************************************************************
     * REST endpoint /api/getStuff2
     * Call using ?id=some_long_string
     *
     * @param stuffId whatever string you want to give it
     * @return the string value passed in as a parameter
     *************************************************************************/
    @RequestMapping(value = "/api/getStuff2", method = RequestMethod.GET, produces = "application/json")
    public  ResponseEntity<?>  getStuff2(@RequestParam(name="id") String stuffId) {
        logger.debug("id={}", stuffId);

        String returnValue = "stuffId=" + stuffId;

        // Return the string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(returnValue);
    }

    /*************************************************************************
     * REST endpoint /api/getStuff3
     * Call using ?id=an_integer&optional=a_string
     *
     * @param stuffId whatever string you want to give it
     * @param optional (OPTIONAL) a string, not required
     * @return the string value passed in as a parameter
     *************************************************************************/
    @RequestMapping(value = "/api/getStuff3", method = RequestMethod.GET, produces = "application/json")
    public  ResponseEntity<?>  getStuff3(@RequestParam(name="id") Integer stuffId, @RequestParam(name="optional", required=false) String optional) {
        logger.debug("id={}  optional={}", stuffId, optional);

        String returnValue = "id=" + stuffId + " optional=" + optional;

        // Return the string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(returnValue);
    }

    /*************************************************************************
     * REST endpoint /api/getStuff4
     * Call using ?id=7
     *         or ?optional=something&id=55
     *
     * @param stuffId whatever string you want to give it
     * @param optional (OPTIONAL) a string, not required
     * @return the string value passed in as a parameter
     *************************************************************************/
    @RequestMapping(value = "/api/getStuff4", method = RequestMethod.GET, produces = "application/json")
    public  ResponseEntity<?>  getStuff4(@RequestParam(name="id") Integer stuffId,
            @RequestParam(name="optional", defaultValue="not_set") String optional) {
        logger.debug("id={}  optional={}", stuffId, optional);

        String returnValue = "id=" + stuffId + " optional=" + optional;

        // Return the string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(returnValue);
    }

    /*************************************************************************
     * REST endpoint /api/reports/add
     *
     * @param reportDTO via JSON body containing key-value pairs for
     *                  description, priority, and reviewed
     *************************************************************************/
    @RequestMapping(value = "/api/reports/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> addReport(@RequestBody ReportDTO reportDTO){
        logger.debug("addReport() called; reportDTO = {}", reportDTO);

        reportsService.addRecord(reportDTO);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }
}
