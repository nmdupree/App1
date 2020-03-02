(function(){

    angular.module('app1')
        .factory('IndicatorFactory', ['$http', '$q', init])

    function init($http, $q) {
        let CountermeasureFactory = {};

        CountermeasureFactory.addIndicator = addIndicator;

        return CountermeasureFactory;

        function addIndicator(aIndicatorDTO) {

            return $http.post('./api/indicators/add', aIndicatorDTO).then(function (results) {

                return results.data;
            })

        }

    }

})();