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
        drawRecord(document.getElementById("record-content"), response.data.points);
    });
});

function drawRecord(canvas, points) {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;

    const width = canvas.width;
    const height = canvas.height;

    const maxX = max(points, function (l, r) {
        return l.x > r.x;
    }).x;
    if (width < maxX) {
        const factor = maxX / width + 5;
        scaleDown(points, factor);
    }

    const maxY = max(points, function (l, r) {
        return l.y > r.y;
    }).y;
    if (height < maxY) {
        const factor = maxY / height + 5;
        scaleDown(points, factor);
    }

    console.log("W: " + width + " H: " + height + " max X: " + maxX + " max Y:" + maxY);

    const c = canvas.getContext("2d");
    c.beginPath();
    c.strokeStyle = "#353535";
    c.moveTo(points[0].x, points[0].y);
    for (var i = 1; i < points.length; i++) {
        const p = points[i];
        console.log(p);
        c.lineTo(p.x, p.y);
    }
    c.stroke();
}

function max(arr, comparator) {
    if (arr.length === 0) {
        return null;
    }
    var max = arr[0];
    for (var i = 1; i < arr.length; i++) {
        var el = arr[i];
        if (comparator(el, max)) {
            max = el;
        }
    }
    return max;
}

function scaleDown(points, factor) {
    points.forEach(function (element) {
        element.x /= factor;
        element.y /= factor;
    });
}
