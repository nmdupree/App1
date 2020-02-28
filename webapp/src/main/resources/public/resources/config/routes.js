(function(){
    angular.module('app.routes', ['ui.router'])
        .config([
            '$stateProvider',
            '$urlRouterProvider',
            '$locationProvider',
            Callback
        ]);

    function Callback($stateProvider, $urlRouterProvider, $locationProvider){
        let URL_PAGE_PREFIX = '/page';
        $locationProvider.html5Mode(true);
        $locationProvider.hashPrefix('!');

        $stateProvider
            .state('viewReports', getViewReportState())
            .state('addReport',   getAddReportState())
            .state('addIndicator',   getAddIndicatorState());


        $urlRouterProvider.otherwise(URL_PAGE_PREFIX + '/404/');


        function getViewReportState() {
            return {
                url: URL_PAGE_PREFIX + '/reports/view',
                templateUrl: './resources/features/reports/view_reports/index.html',
                controller: 'viewReports',
                controllerAs: 'viewReportsVM',
                resolve: {

                }
            }
        }

        function getAddReportState() {
            return {
                url: URL_PAGE_PREFIX + '/reports/add',
                templateUrl: './resources/features/reports/add_report/index.html',
                controller: 'addReport',
                controllerAs: 'addReportVM',
                resolve: {
                    lookupMap: function(LookupFactory) {
                        return LookupFactory.getLookupWithTypeName( 'priority')
                    }
                }
            }
        }

        function getAddIndicatorState() {
            return {
                url: URL_PAGE_PREFIX + '/indicators/add',
                templateUrl: './resources/features/indicators/add_indicator/index.html',
                controller: 'addIndicator',
                controllerAs: 'addIndicatorVM',
                resolve: {
                    indicatorTypeMap: function (LookupFactory) {
                        return LookupFactory.getLookupWithTypeName('indicator_type')
                    },
                    classificationMap: function (LookupFactory) {
                        return LookupFactory.getLookupWithTypeName('classification')
                    }
                }
            }
        }

    }
})();