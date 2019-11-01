(function(){

    /*******************************************************
     * routes.js
     *******************************************************/
    angular.module('app1.routes', ['ui.router'])
        .config(['$stateProvider', '$urlRouterProvider', routingCallback]);

    function routingCallback($stateProvider, $urlRouterProvider) {
        console.log('routingCallback() started.');

        var PAGE_PREFIX = '/page';

        $stateProvider
            .state('page1', getPage1State())
            .state('page2', getPage2State());


        function getPage1State() {
            return {
                url: PAGE_PREFIX + '/page1',
                template: '<h2>page1</h2>'
            }
        }

        function getPage2State() {
            return {
                url: PAGE_PREFIX + '/page2',
                template: '<h2>page2</h2>'
            }
        }
    }

})();
