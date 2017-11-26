'use strict';

angular.module('myApp.login', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'login/login.html',
    controller: 'loginCtrl'
  });
}])

.controller('loginCtrl', function($scope, $http, BACKEND_DOMAIN) {
    $scope.credentials = {
        name: '',
        password: ''
    };

    $scope.tryLogin = function() {
        $http({
            method: 'GET',
            url: BACKEND_DOMAIN + '/login',
            headers: {
                name: $scope.credentials.name,
                password: $scope.credentials.password,
                'Access-Control-Allow-Origin': '*/*'
            }
        }).then(function(response) {
            console.log(response.data)
        });
    }
});
