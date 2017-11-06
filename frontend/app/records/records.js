'use strict';

var app = angular.module('myApp.records', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/records', {
        templateUrl: 'records/records.html',
        controller: 'RecordsController'
    });
}]);

app.controller('RecordsController', function ($scope) {

    $scope.recordsList = [
        {
            title: "First Record",
            timestamp: "21-10-2017"
        },
        {
            title: "Second Record",
            timestamp: "25-10-2017"
        }
    ];
});
