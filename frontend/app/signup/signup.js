'use strict';

var app = angular.module('myApp.signup', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/signup', {
        templateUrl: 'signup/signup.html',
        controller: 'SignUpCtrl'
    });
}]);

app.controller('SignUpCtrl', ['$scope', function ($scope) {

}]);
