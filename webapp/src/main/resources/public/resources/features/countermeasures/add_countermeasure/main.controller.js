(function(){
    angular.module('app.features')
        .controller('addCountermeasure', ['$timeout', '$stateParams', '$scope', '$window', 'countermeasureStatus',  Callback])

    function Callback($timeout, $stateParams, $scope, $window, countermeasureStatus) {
        console.log('addCountermeasure controller started.');

        let addCountermeasureVM = this;
        addCountermeasureVM.save = save;
        addCountermeasureVM.clear = clear;


        window.document.title = "Add Countermeasure | APP1";

        // Object to hold form data
        addCountermeasureVM.new = {};

        // Hard-coded statuses
        addCountermeasureVM.status =
            [
                {
                    'id':1,
                    'name':'Active'
                },
                {
                    'id':2,
                    'name':'Inactive'
                },
                {
                    'id':3,
                    'name':'Pending'
                }
            ];

        // Status from db lookup
        addCountermeasureVM.lookupStatus = countermeasureStatus;


        addCountermeasureVM.$onInit = function() {
            console.log('addCountermeasure onInit() started.');
            console.log('addCountermeasure onInit() finished.');
        };

        // Called when Add Countermeasure is clicked
        function save(){

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