(function(){
    angular.module('app.features')
        .controller('addIndicator', ['$timeout', '$stateParams', '$scope', '$window', '$mdToast', 'indicatorTypeMap', 'classificationMap', 'IndicatorFactory', Callback])

    function Callback($timeout, $stateParams, $scope, $window, $mdToast, indicatorTypeMap, classificationMap, IndicatorFactory) {
        console.log('addIndicator controller started.');

        let addIndicatorVM = this;
        addIndicatorVM.save = save;
        addIndicatorVM.clear = clear;


        window.document.title = "Add Indicator | APP1";

        // Object to hold form data
        addIndicatorVM.new = {};

        // List of indicator types
        addIndicatorVM.indicatorTypes = indicatorTypeMap;
        // List of classification types
        addIndicatorVM.classification = classificationMap;


        addIndicatorVM.$onInit = function() {
            console.log('addIndicator onInit() started.');
            console.log('addIndicator onInit() finished.');
        };

        // Called when Add Indicator is clicked
        function save(){
            // Start the progress bar
            addIndicatorVM.dataIsLoading = true;

            // Create the DTO
            let addIndicatorDTO = {
                'value' : addIndicatorVM.new.value,
                'type' : addIndicatorVM.new.type.id,
                'classification' : addIndicatorVM.new.classification.id,
            };

            // Call the Countermeasure Factory to add the new countermeasure
            IndicatorFactory.addIndicator(addIndicatorDTO).then(function (result) {
                // Log the successful REST call (200 status code)
                console.log('REST call successful, results = ', result);

                // Show a pop-up indicating successful results, duration 3 seconds
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Indicator added successfully.')
                        .hideDelay(3000)
                );

                // Reset the input form
                addIndicatorVM.new = {};
                $scope.addIndicatorForm.$setPristine();
                $scope.addIndicatorForm.$setUntouched();
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
                    addIndicatorVM.dataIsLoading = false;
                    console.log('REST call finally() was reached.');
                });

        }

        // Called when Reset is clicked
        function clear(){
            addIndicatorVM.new = {};
            $scope.addIndicatorForm.$setPristine();
            $scope.addIndicatorForm.$setUntouched();

        }


        console.log('addIndicator controller finished.');
    }
})();