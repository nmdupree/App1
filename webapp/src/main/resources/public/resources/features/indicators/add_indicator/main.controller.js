(function(){
    angular.module('app.features')
        .controller('addIndicator', ['$timeout', '$stateParams', '$scope', '$window', 'indicatorTypeMap', 'classificationMap', Callback])

    function Callback($timeout, $stateParams, $scope, $window, indicatorTypeMap, classificationMap) {
        console.log('addIndicator controller started.');

        let addIndicatorVM = this;
        addIndicatorVM.save = save;
        addIndicatorVM.clear = clear;


        window.document.title = "Add Indicator | APP1";

        // Object to hold form data
        addIndicatorVM.new = {};

        // List of indicator types
        addIndicatorVM.indicatorTypes = indicatorTypeMap;

        addIndicatorVM.classification = classificationMap;



        addIndicatorVM.$onInit = function() {
            console.log('addIndicator onInit() started.');
            console.log('addIndicator onInit() finished.');
        };

        // Called when Add Indicator is clicked
        function save(){

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