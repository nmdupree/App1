(function(){
    angular.module('app1')
        .factory('CountermeasureFactory', ['$http', '$q', init])

    function init($http, $q) {
        let CountermeasureFactory = {};

        CountermeasureFactory.addCountermeasure = addCountermeasure;

        return CountermeasureFactory;

        function addCountermeasure(aCountermeasureDTO) {

            return $http.post('./api/countermeasures/add', aCountermeasureDTO).then(function (results) {

                return results.data;
            })

        }

    }
}());