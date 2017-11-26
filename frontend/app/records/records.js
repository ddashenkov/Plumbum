'use strict';

var app = angular.module('myApp.records', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/records', {
        templateUrl: 'records/records.html',
        controller: 'RecordsController'
    });
}]);

app.controller('RecordsController', function ($scope, $http, BACKEND_DOMAIN) {
    $http.get(BACKEND_DOMAIN + "/records").then(function (response) {
        console.log(response);
        $scope.recordsList = response.data.records;
    });
});
