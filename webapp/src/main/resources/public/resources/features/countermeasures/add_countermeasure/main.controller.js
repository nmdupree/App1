(function(){
    angular.module('app.features')
        .controller('addCountermeasure', ['$timeout', '$stateParams', '$scope', '$window', '$mdToast' , 'countermeasureStatus', 'CountermeasureFactory',  Callback])

    function Callback($timeout, $stateParams, $scope, $window, $mdToast, countermeasureStatus, CountermeasureFactory) {
        console.log('addCountermeasure controller started.');

        let addCountermeasureVM = this;
        addCountermeasureVM.save = save;
        addCountermeasureVM.clear = clear;


        window.document.title = "Add Countermeasure | APP1";

        // Triggers progress bar
        addCountermeasureVM.dataIsLoading = false;

        // Object to hold form data
        addCountermeasureVM.new = {};

        // Status codes from db lookup
        addCountermeasureVM.lookupStatus = countermeasureStatus;


        addCountermeasureVM.$onInit = function() {
            console.log('addCountermeasure onInit() started.');
            console.log('addCountermeasure onInit() finished.');
        };

        // Called when Add Countermeasure is clicked
        function save(){
            // Start the progress bar
            addCountermeasureVM.dataIsLoading = true;

            // Create the DTO
            let addCountermeasureDTO = {
                'value' : addCountermeasureVM.new.value,
                'status' : addCountermeasureVM.new.status.id,
                'start_date' : addCountermeasureVM.new.startDate,
                'end_date' : addCountermeasureVM.new.endDate
            };

            // Call the Countermeasure Factory to add the new countermeasure
            CountermeasureFactory.addCountermeasure(addCountermeasureDTO).then(function (result) {
                // Log the successful REST call (200 status code)
                console.log('REST call successful, results = ', result);

                // Show a pop-up indicating successful results, duration 3 seconds
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Countermeasure added successfully.')
                        .hideDelay(3000)
                );

                // Reset the input form
                addCountermeasureVM.new = {};
            })
                .catch(function (result) {
                    // Log a failed REST call
                    console.log('REST call failed, results = ', result);

                    // Show a pop-up indicating the call failed, duration 3 seconds
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent("There was a problem and the update failed.")
                            .hideDelay(3000)
                    );

                })
                .finally(function () {
                    // This method is always called
                    addCountermeasureVM.dataIsLoading = false;
                    console.log('REST call finally() was reached.');
                });

        }

        // Called when Reset is clicked
        function clear(){
            addCountermeasureVM.new = {};
            $scope.addCountermeasureForm.$setPristine();
            $scope.addCountermeasureForm.$setUntouched();

        }


        console.log('addCountermeasure controller finished.');
    }
})();