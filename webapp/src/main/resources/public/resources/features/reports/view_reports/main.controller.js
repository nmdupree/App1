
(function(){
    angular.module('app.features')
        .controller('viewReports', ['$timeout', '$stateParams', '$scope', '$window', 'reportsViewData', Callback])

    function Callback($timeout, $stateParams, $scope, $window, reportsViewData) {
        console.log('viewReports controller started.');

        let viewReportsVM = this;

        window.document.title = "View Reports | APP1";

        viewReportsVM.$onInit = function() {
            console.log('viewReports onInit() started.');

            let gridOptions = { };

            // Initialize the grid with hard-coded data
            gridOptions.data = reportsViewData;

            gridOptions.appScopeProvider = $scope;
            gridOptions.flatEntityAccess = true;
            gridOptions.enableFiltering = true;
            gridOptions.enableColumnResizing = true;
            gridOptions.enableGridMenu = true;
            gridOptions.saveFocus = false;
            gridOptions.saveGrouping = false;
            gridOptions.savePinning = false;
            gridOptions.saveSelection = false;
            gridOptions.saveTreeView = false;
            gridOptions.multiSelect = true;
            gridOptions.exporterMenuPdf = false;
            gridOptions.exporterMenuExcel = false;
            gridOptions.exporterFieldApplyFilters = true;
            gridOptions.rowHeight = 45;
            gridOptions.enableSelectAll = true;
            gridOptions.enableSelectionBatchEvent = false;
            gridOptions.scope = $scope;

            // Define the grid columns
            gridOptions.columnDefs = [
                {
                    name: 'id',
                    field: 'id',
                    displayName: 'Id',
                    visible: true,
                    type: 'number'
                },
                {
                    name: 'display_name',
                    field: 'display_name',
                    displayName: 'Report Name',
                    visible: true,
                    type: 'string'
                },
                {
                    name: 'priority',
                    field: 'priority',
                    displayName: 'Priority',
                    visible: true,
                    type: 'string'
                },
                {
                    name: 'active',
                    field: 'active',
                    displayName: 'Active',
                    visible: true,
                    type: 'boolean'
                },
            ];


            gridOptions.onRegisterApi = function(gridApi) {
                $scope.gridApi = gridApi;
            };


            viewReportsVM.gridOptions = gridOptions;

            // Set the pagination properties (required in order for ui-grid pagination to work)
            gridOptions.totalItems = gridOptions.data.length;
            gridOptions.paginationPageSize = 50;
            gridOptions.paginationPageSizes = [25, 50, 100, 250];

            console.log('viewReports onInit() finished.');
        };


        console.log('viewReports controller finished.');
    }
})();