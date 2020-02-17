(function(){
    angular.module('app1')
        .controller('navController', ["$timeout", "$stateParams", "$state", "$location", '$rootScope', "$scope", "$mdDialog", "$mdSidenav", "$transitions", Callback]);

    function Callback($timeout, $stateParams, $state, $location, $rootScope, $scope, $mdDialog, $mdSidenav, $transitions){
        let navVM = this;

        console.log('navVM controller started.');

        navVM.location = $location.$$path;
        navVM.dataIsLoading = false;

        navVM.isNavGroup1Open = false;
        navVM.isNavGroup2Open = false;

        navVM.$onInit = function(){
            // Initialize this controller
            console.log('navVm.onInit() started.');
            console.log('navVm.onInit() finished.');
        }



        navVM.toggleNav = function(){
            navVM.toggleClass = navVM.toggleClass ? "" : "toggled"
        }


        function goToState (state){
            $state.go(state)
        }


        $transitions.onStart({}, function(trans) {
            navVM.dataIsLoading = true
        })

        $transitions.onSuccess({}, function(trans) {
            console.log('nav.controller.js  onSuccess() started. \ntrans.from()=', trans.from(), '   trans.to()=', trans.to()  );
            navVM.from=trans.from();
            navVM.to=trans.to();
            console.log('nav.controller.js  onSuccess() finished');
        })

        $transitions.onError({}, function(trans) {
            navVM.dataIsLoading = false
        })

        navVM.toggleNav = function(){
            navVM.toggleClass = navVM.toggleClass ? "" : "toggled"
        }

        navVM.search = function(){
            $state.go('search')
        }


        function buildToggler(navID) {
            $mdSidenav(navID)
                .toggle()
        }

        function toggleItem(item){
            navVM.nav[item] = !navVM.nav[item];
            for (var thing in navVM.nav){
                var thisItem = navVM.nav[thing];
                if((thisItem === true) && (thing != item)){
                    navVM.nav[thing] = false;
                }
            }
        }

        function openItemNewWindow(state){
            var location = $state.href(state)
            window.open(location,'_blank');
        }

        navVM.openLeftMenu = function() {
            $mdSidenav('left').toggle();
        };

        navVM.closeSidenav = function () {
            $mdSidenav('left').close()
        };

        console.log('navVM controller finished.');

    }
})()

