(function(){
    angular.module('app.features')
        .controller('addReport', ['$timeout', '$stateParams', '$scope', '$window', Callback])

    function Callback($timeout, $stateParams, $scope, $window) {
        console.log('addReport controller started.');

        let addReportVM = this;

        window.document.title = "Add Report | APP1";

        addReportVM.$onInit = function() {
            console.log('addReport onInit() started.');
            console.log('addReport onInit() finished.');
        };

        console.log('addReport controller finished.');
    }
})();