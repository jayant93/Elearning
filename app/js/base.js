/**
 * Created by jpereira on 12/19/2016.
 */
'use strict';

// external dependencies
var angular = require('angular');
    global.jQuery = require('jquery');
    global.util = require('util');

require('bootstrap');
require('angular-route');
require('angular-animate');
require('angular-filter');
require('ng-file-upload');
require('angular-translate');
require('angular-ui-bootstrap');
require('angular-ui-bootstrap-datetimepicker');
require('angular-ui-notification');
require('angular-ui-validate');
require('ng-file-upload');
require('angular-file-saver');
require('angular-local-storage');
require('angular-spinner');
require('checklist-model');
require('isteven-angular-multiselect/isteven-multi-select.js');
require('angular-recaptcha');

// app sources
var baseControllers = require('./controllers.js');
var baseServices = require('./services.js');
var baseDirectives = require('./directives.js');
var baseFilters = require('./filters.js');

require('./config.js');

angular.module('baseApp', ['baseApp.envConfig', 'ngRoute', 'ngAnimate', 'ngFileUpload', 'pascalprecht.translate', 'ui.bootstrap', 'ui.bootstrap.datetimepicker', 'hypermedia', 'ngFileSaver', 'LocalStorageModule','angularSpinner', 'ui-notification', 'angular.filter', 'checklist-model', 'ui.validate', 'isteven-multi-select', 'vcRecaptcha'])
    .config([
        '$locationProvider',
        '$routeProvider',
        '$translateProvider',
        'localStorageServiceProvider',
        function($locationProvider, $routeProvider, $translateProvider, localStorageServiceProvider) {
            $translateProvider.useSanitizeValueStrategy('escapeParameters');
            $locationProvider.hashPrefix('!');

            //local storage
            localStorageServiceProvider.setPrefix('um');
            localStorageServiceProvider.setNotify(true, true);

            // routes
            $routeProvider
                .otherwise({
                    redirectTo: '/'
                });
        }
    ])

    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('baseApp.httpRequestInterceptor');
    }
    ])

    .config(['usSpinnerConfigProvider', function (usSpinnerConfigProvider) {
            usSpinnerConfigProvider.setDefaults({radius:15, width:6, length: 15, speed: 1.3});
        }
    ])

    .config(['NotificationProvider', function(NotificationProvider) {
        NotificationProvider.setOptions({
            delay: 3000,
            startTop: 20,
            startRight: 10,
            verticalSpacing: 10,
            horizontalSpacing: 10,
            positionX: 'right',
            positionY: 'top'
        });
    }
    ])

    // controllers
    .controller('baseApp.baseController', ['$scope', '$rootScope', '$location' , '$route', 'baseApp.utilityService',
        baseControllers.baseController])

    //interceptors
    .factory('baseApp.httpRequestInterceptor', ['ENV_VARS', 'localStorageService', baseServices.httpRequestInterceptor])
    .factory('baseApp.requestsErrorHandler', ['ENV_VARS', '$q', '$log', '$location', '$injector', '$filter', 'localStorageService', baseServices.requestsErrorHandler])

    // services
    .factory('baseApp.baseService', ['ENV_VARS', '$http', '$log', '$q', '$filter', '$httpParamSerializer','ResourceContext', 'HalResource', baseServices.baseService])
    .factory('baseApp.utilityService', [baseServices.utilityService])
    .factory('baseApp.fileService', ['$q', '$log', 'baseApp.baseService', 'Upload', 'FileSaver', baseServices.fileService])
    .factory('baseApp.recaptchaService', ['$q', 'baseApp.baseService', baseServices.recaptchaService])

    // directives
    .directive('ngLazyShow', ['$animate', baseDirectives.ngLazyShow])
    .directive('routeLoadingIndicator', ['$rootScope' ,'$log', baseDirectives.routeLoadingIndicator])
    .directive('confirmOnExit', ['$rootScope' ,'$log', '$location', baseDirectives.confirmOnExit])
    .directive('usSpinner', ['$http' ,'$rootScope', baseDirectives.usSpinner])
    .directive('page', [baseDirectives.page])
    .directive('resultInfo', [baseDirectives.resultInfo])
    .directive('disallowSpaces', [baseDirectives.disallowSpaces])
    .directive('capitalize', [baseDirectives.capitalize])

    // filters
    .filter('encodeURI', ['$encodeURI', baseFilters.encodeURI])
    .filter('range', ['$filter', baseFilters.range])
    .filter('trust', ['$sce', baseFilters.trust])

;