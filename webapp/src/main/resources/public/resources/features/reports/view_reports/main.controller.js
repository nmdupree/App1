
(function(){
    angular.module('app.features')
        .controller('viewReports', ['$timeout', '$stateParams', '$scope', '$window', Callback])

    function Callback($timeout, $stateParams, $scope, $window) {
        console.log('viewReports controller started.');

        let viewReportsVM = this;

        window.document.title = "View Reports | APP1";

        viewReportsVM.$onInit = function() {
            console.log('viewReports onInit() started.');
            console.log('viewReports onInit() finished.');
        };


        console.log('viewReports controller finished.');
    }
})();