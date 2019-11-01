(function(){

    /*******************************************************
     * app.js
     *******************************************************/
    var app = angular.module('app1', [
        'app1.routes'
    ]);

    app.run(['$http', runCallback]);
    function runCallback($http) {
        console.log('app1 runCallback() started')
    }

})();