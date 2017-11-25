'use strict';

var app = angular.module('myApp.record', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/record/:id', {
        templateUrl: 'record/record.html',
        controller: 'RecordController'

    });
}]);

app.controller('RecordController', function ($scope, $httpBackend, BACKEND_DOMAIN, $location) {
    const url = $location.absUrl;
    const id = url.slice(url.lastIndexOf('/'), url.length());

    $httpBackend.get(BACKEND_DOMAIN + "/record/" + id).then(function (response) {
        console.log(response.toString());
    });
});
