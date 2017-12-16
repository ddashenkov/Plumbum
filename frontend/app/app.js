'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'myApp.login',
    'myApp.signup',
    'myApp.version',
    'myApp.records',
    'myApp.record'
]).config(['$locationProvider', '$routeProvider', '$httpProvider', function ($locationProvider, $routeProvider, $httpProvider) {
    $locationProvider.hashPrefix('!');
    $httpProvider.defaults.withCredentials = true;
    $routeProvider.otherwise({redirectTo: '/login'});
}]).constant("BACKEND_DOMAIN", "https://guarded-badlands-19791.herokuapp.com/");
