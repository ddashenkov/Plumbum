'use strict';

var app = angular.module('myApp.record', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/record/:id', {
        templateUrl: 'record/record.html',
        controller: 'RecordController'

    });
}]);

app.controller('RecordController', function ($scope, $http, BACKEND_DOMAIN, $location) {
    const url = $location.absUrl();
    const id = url.slice(url.lastIndexOf('/'), url.length);

    $http.get(BACKEND_DOMAIN + "/record/" + id).then(function (response) {
        drawRecord(document.getElementById('record-content'), response.data.points)
    });
});

function drawRecord(canvas, points) {
    const c = canvas.getContext("2d");
    c.moveTo(points[0].x, points[0].y);
    for (var i = 1; i < points.length; i++) {
        const p = points[i];
        c.lineTo(p.x, p.y);
    }
    c.stroke();
}
