(function(){
    //
    //  GridService
    //
    angular.module('app1')
        .factory('GridService', [init] )

    function init() {
        let GridService = {};

        GridService.getListOfFilters = getListOfFilters;

        return GridService;


        /*
         * Returns a list of maps that hold filter information
         */
        function getListOfFilters(aColumns) {
            console.log('getListOfFilters() started.');

            if ((!aColumns || aColumns.length == 0)) {
                // The passed-in list of column info is null or empty.  So, return null
                return null;
            }

            filters = [ ];

            // Loop through all of the columns, generating a map of filters
            aColumns.forEach((column) => {
                if (column.enableFiltering) {
                    // INCOMPLETE
                }

            })


            return filters;
        }


    }
})();