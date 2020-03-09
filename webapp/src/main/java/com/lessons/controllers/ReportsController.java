package com.lessons.controllers;

import com.lessons.models.*;
import com.lessons.services.ReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    private final int MAX_PAGE_SIZE = 10000;

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
     * REST endpoint /api/reports/all
     * @return a list of reportGetDTOs
     *************************************************************************/
    @RequestMapping(value = "/api/reports/all", method = RequestMethod.GET, produces = "application/json")
    public  ResponseEntity<?>  getAllReports() {

        logger.debug("a thing");

        List<GetReportDTO> allReports = reportsService.getAllReports();



        // Return the string as plain-text
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allReports);
    }



    /*************************************************************************
     * REST endpoint /api/reports/add
     *
     * @param addReportDTO via JSON body containing key-value pairs for
     *                  description, priority, and reviewed
     *************************************************************************/
    @RequestMapping(value = "/api/reports/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> addReport(@RequestBody AddReportDTO addReportDTO){
        logger.debug("addReport() called; reportDTO = {}", addReportDTO);

        reportsService.addRecord(addReportDTO);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    /*************************************************************************
     * REST endpoint /api/reports
     *
     * @param reportId the id of the report to be returned
     *************************************************************************/
    @RequestMapping(value = "/api/reports/{reportId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getReportById(@PathVariable Integer reportId){
        logger.debug("getReportById() called; id = {}", reportId);

        if (!reportsService.doesReportIdExist(reportId)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The specified report id (" + reportId + ") does not exist.");
        }

        ReportByIdDTO reportByIdDTO = reportsService.getRecordById(reportId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reportByIdDTO);
    }

    /*************************************************************************
     * REST endpoint /api/reports
     *
     * @param reportId the id of the report to be deleted
     *************************************************************************/
    @RequestMapping(value = "/api/reports/{reportId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<?> deleteReportById(@PathVariable Integer reportId){
        logger.debug("deleteReportById() called; id = {}", reportId);


        if (reportId < 1 || !reportsService.doesReportIdExist(reportId)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The specified report id (" + reportId + ") does not exist.");
        }

        int rowsDeleted = reportsService.deleteRecordById(reportId);

        if(rowsDeleted == 0) {
            logger.warn("Checks passed but failed to delete 1 record as expected.");
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The requested report matching id = " + reportId + "was not deleted.");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body("");
    }

    /*************************************************************************
     * REST endpoint /api/reports/update
     *
     * @param updateReportDTO via JSON body containing key-value pairs for
     *                        id, description, version, priority, display_name,
     *                        and reference_source
     *************************************************************************/
    @RequestMapping(value = "/api/reports/update", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> updateReport(@RequestBody UpdateReportDTO updateReportDTO) {
        logger.debug("updateReport() called; updateReportDTO = {}", updateReportDTO);

        int reportId = updateReportDTO.getId();

        if (reportId < 1 || !reportsService.doesReportIdExist(reportId)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The specified report id (" + reportId + ") does not exist.");
        }

        int version = updateReportDTO.getVersion();

        if (!reportsService.isLatestVersion(version, reportId)){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Version Mismatch: This record has a more recent version ID than supplied with your" +
                            "update request. The update failed.");
        }

        // Update record with bind variables
        // reportsService.updateRecord(updateReportDTO);

        // Update record with named variables
        //Map<String, Object> rowsetMap =  reportsService.updateRecordWithNamedVariables(updateReportDTO);
        //reportsService.updateReportsAudit(rowsetMap, "Yo Mama");

        // Update record in a transaction and audit the update
        reportsService.updateRecordTransaction(updateReportDTO, "Pikachu");


        return ResponseEntity
                .status(HttpStatus.OK)
                .body("");
    }

    @RequestMapping(value = "/api/reports/filtered", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> getFilteredReports(@RequestBody FilterDaddyDTO searchFilters){

        logger.debug("Search filtered reports called.");


        if(searchFilters.getOffset() == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Offset value may not be null.");
        }
        if(searchFilters.getOffset() < 0){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Offset may not be a negative number.");
        }
        if(searchFilters.getPageSize() == null){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Page size may not be null.");
        }
        if(searchFilters.getPageSize() < 1){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Page size must be greater than 0.");
        }
        if(searchFilters.getPageSize() > MAX_PAGE_SIZE){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Page size must be less than " + MAX_PAGE_SIZE + " .");
        }



        List<GetReportDTO> filteredReports = reportsService.getFilteredReports(searchFilters);



        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredReports);
    }

}
