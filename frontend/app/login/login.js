'use strict';

angular.module('myApp.login', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'login/login.html',
    controller: 'loginCtrl'
  });
}])

.controller('loginCtrl', function($scope, $http, BACKEND_DOMAIN, $window) {
    $scope.credentials = {
        name: '',
        password: ''
    };

    $scope.tryLogin = function() {
        document.cookie = "name=" + $scope.credentials.name;
        document.cookie = "password=" + $scope.credentials.password;
        $http.get(BACKEND_DOMAIN + '/login').then(function() {
            $window.location.href = '#!/records';
        });
    }
});
