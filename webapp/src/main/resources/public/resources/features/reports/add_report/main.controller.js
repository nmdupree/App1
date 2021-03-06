(function(){
    angular.module('app.features')
        .controller('addReport', ['$timeout', '$stateParams', '$scope', '$window', '$mdToast', 'ReportFactory', 'lookupMap', Callback])

    function Callback($timeout, $stateParams, $scope, $window, $mdToast, ReportFactory, lookupMap) {
        console.log('addReport controller started.');

        // Public Functions
        let addReportVM = this;
        addReportVM.save = save;
        addReportVM.clear = clear;
        addReportVM.validate = validate;

        window.document.title = "Add Report | APP1";

        // Tracks whether a query is in progress, triggers the progress bar
        addReportVM.dataIsLoading = false;

        // Object to hold form data,
        addReportVM.new = {};


        // List of priority options
        addReportVM.lookupData = lookupMap;

        addReportVM.$onInit = function() {
            console.log('addReport onInit() started.');
            console.log('addReport onInit() finished.');
        };

        // Called when Add Report is clicked
        function save(){
            // Start the progress bar
            addReportVM.dataIsLoading = true;
            console.log("Save called.");

            // Create an object for the DTO
            let addReportDTO = {
                'description': addReportVM.new.description,
                'priority': addReportVM.new.priority,
                'reviewed': addReportVM.new.review
            };


            ReportFactory.addReport(addReportDTO).then(function (res) {
                // The REST worked  (it returned a status between 200-299)
                console.log('REST call succeeded.  returned info is res=', res);
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Report added successfully!')
                        .hideDelay(3000)
                );
                addReportVM.new = {};


            })
                .catch(function (res) {
                    // The REST failed  (it returned a status code outside of 200-299)
                    console.log('REST call failed.  returned info is res=', res);
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('Ya done fucked up, son.')
                            .hideDelay(3000)
                    );

                })
                .finally(function () {
                    // This method is always called
                    addReportVM.dataIsLoading = false;
                    console.log('REST call finally() was reached.');

                });
        }

        function clear(){
            addReportVM.new = {};
            $scope.addReportForm.$setPristine();
            $scope.addReportForm.$setUntouched();

        }

        function validate(){
            console.log('validate() you entered: ', $scope.addReportForm.name.$modelValue);
            let userString = $scope.addReportForm.name.$modelValue;

            if (userString == 'good'){
                $scope.addReportForm.name.$setValidity('customError', true);
            }
            else {
                $scope.addReportForm.name.$setValidity('customError', false);
            }
        }


        console.log('addReport controller finished.');
    }
})();