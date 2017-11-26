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
        const name = "name=" + $scope.credentials.name;
        document.cookie = name;
        const pass = "password=" + $scope.credentials.password;
        document.cookie = pass;
        console.log(name);
        console.log(pass);
        $http.get(BACKEND_DOMAIN + '/signup')
            .then(function (response) {
                console.log(response.data)
            });
    }
});
