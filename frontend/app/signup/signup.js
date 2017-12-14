'use strict';

var app = angular.module('myApp.signup', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/signup', {
        templateUrl: 'signup/signup.html',
        controller: 'SignUpCtrl'
    });
}]);

app.controller('SignUpCtrl', function ($scope, $http, BACKEND_DOMAIN) {
    $scope.credentials = {
        name: '',
        password: ''
    };
    $scope.trySignUp = function () {
        document.cookie = "name=" + $scope.credentials.name;
        document.cookie = "password=" + $scope.credentials.password;
        $http.get(BACKEND_DOMAIN + '/signup')
            .then(function (response) {
                console.log(response.data)
            });
    }
});
