(function(){
    //
    //  LookupFactory holds methods to invoke REST calls for lookup operations
    //
    angular.module('app1')
        .factory('LookupFactory', ['$http', '$q', init] )

    function init($http, $q) {
        let LookupFactory = {};

        LookupFactory.getLookupWithTypeName = getLookupWithTypeName;

        return LookupFactory;

        function getLookupWithTypeName(aType){
            console.log('LookupFactory.getLookupWithTypeName() started.');

            return $http.get('./api/lookups/' + aType + '/display_order').then(function (results) {
                // The REST call returned with a 200-299 status code

                // So, return some data
                return results.data;
            })
        }

    }

})();
