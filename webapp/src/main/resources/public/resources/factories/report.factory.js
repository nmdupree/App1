(function(){
    //
    //  ReportFactory holds methods to invoke REST calls for report operations
    //
    angular.module('app1')
        .factory('ReportFactory', ['$http', '$q', init] )

    function init($http, $q) {
        let ReportFactory = {};

        ReportFactory.addReport = addReport;

        return ReportFactory;


        /*
         * Make a REST call that adds a report to the system
         * NOTE:  This method returns a promise
         */
        function addReport(aReportDTO) {
            console.log('ReportFactory.addReport() started.   aReportDTO=', aReportDTO);

            return $http.post('./api/reports/add', aReportDTO).then(function(results) {
                // The REST call returned with a 200-299 status code

                // So, return some data
                return results.data;
            })
        }


    }   // End of ReportFactory
})();