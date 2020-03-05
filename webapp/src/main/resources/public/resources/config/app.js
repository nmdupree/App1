(function(){

    angular.module('app1', [
        'app.routes',
        'app.features',
        'ngMessages',
        'ngAnimate',
        'ngMaterial',
        'ngSanitize',         // Used for rendering html content in $mdDialog popups
        'angularResizable',
        'ui.router',
        'ui.grid',
        'ui.grid.saveState',
        'ui.grid.autoResize',
        'ui.grid.selection',
        'ui.grid.pagination',
        'ui.grid.resizeColumns',
        'ui.grid.moveColumns',
        'ui.grid.exporter',
        'ui.grid.edit',
        'ui.grid.cellNav'
    ])


        .config(['$mdThemingProvider', function ($mdThemingProvider) {

                $mdThemingProvider.theme('default')
                    .primaryPalette('light-blue', {'default': '600', 'hue-1': '400', 'hue-2': '900', 'hue-3': 'A200'})
                    .accentPalette('teal')
                    .warnPalette('red', {'default': '600'})
                    .backgroundPalette('grey');

                $mdThemingProvider.theme('error-toast').backgroundPalette('red').dark();
                $mdThemingProvider.theme('cyan').backgroundPalette('cyan').dark();
                $mdThemingProvider.theme('purple').backgroundPalette('purple').dark();
                $mdThemingProvider.theme('blue-grey').backgroundPalette('blue-grey').dark();
                $mdThemingProvider.theme('blue').backgroundPalette('blue').dark();
                $mdThemingProvider.theme('light-blue').backgroundPalette('light-blue').dark();
            }],
            function($mdGestureProvider) {
                if (isIE) {
                    $mdGestureProvider.disableAll();
                }
            },
            function($mdInkRippleProvider) {
                if (isIE) {
                    $mdInkRippleProvider.disableInkRipple();
                }
            }
        )

        .run(['$rootScope', '$transitions', '$state', Callback]);

    function Callback($rootScope, $transitions, $state) {
        //If you need to debug routing, please add in the '$trace' service and invoke $trace.enable('TRANSITION')
        console.log('app.js Callback() started.');

        $state.defaultErrorHandler(function(error) {
            if (error.type !== 2 && error.type !== 5) { //Ignore Transition superseded

                // Show the error message using some kind of popup or toast factory
                console.log('error message is ', error)
            }
        })



        // Initialize the Factories here (both make REST calls)
        // NOTE:
        //   1) This Callback is called before all of the controllers are called
        //   2) The angular views might flicker if this initializeSecurityRoles() call takes a long
        console.log('app.js Callback() finished.');
    }
})();
