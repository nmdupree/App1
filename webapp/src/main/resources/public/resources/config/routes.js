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
            .state('addIndicator',   getAddIndicatorState())
            .state('addCountermeasure',   getAddCountermeasureState());


        $urlRouterProvider.otherwise(URL_PAGE_PREFIX + '/404/');


        function getViewReportState() {
            return {
                url: URL_PAGE_PREFIX + '/reports/view',
                templateUrl: './resources/features/reports/view_reports/index.html',
                controller: 'viewReports',
                controllerAs: 'viewReportsVM',
                resolve: {
                    // Inject reportsViewData into the viewReports controller
                    reportsViewData: function (ReportFactory) {
                        return ReportFactory.getAllReports();
                    },
                    // Inject lookupData into the viewReports controller
                    lookupPriorities: function (LookupFactory) {
                        return LookupFactory.getLookupWithTypeName('priority');
                    }
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

        function getAddCountermeasureState() {
            return {
                url: URL_PAGE_PREFIX + '/countermeasures/add',
                templateUrl: './resources/features/countermeasures/add_countermeasure/index.html',
                controller: 'addCountermeasure',
                controllerAs: 'addCountermeasureVM',
                resolve: {
                    countermeasureStatus: function(LookupFactory) {
                        return LookupFactory.getLookupWithTypeName( 'countermeasure_status')
                    }

                }
            }
        }

    }
})();