/**
 * Created by jpereira on 12/19/2016.
 */

// app sources
var loginApp = require('../login/app.js');

var translation = require('../main/translations/translation_en.js');

var mainControllers = require('../main/controllers.js');
var mainServices = require('../main/services.js');
var mainFilters = require('../main/filters.js');
var mainDirectives = require('../main/directives.js');

angular.module('umApp', ['loginApp'])

    .config([
        '$locationProvider',
        '$routeProvider',
        '$translateProvider',
        function($locationProvider, $routeProvider, $translateProvider) {
            $translateProvider.useSanitizeValueStrategy('escapeParameters', 'sanitizeParameters');
            $translateProvider.translations('en', translation.en);
            $translateProvider.preferredLanguage('en');

            $locationProvider.hashPrefix('!');

            // routes
            $routeProvider
                .when('/users', {
                    controller: 'userController',
                    templateUrl: 'main/partials/users.html',
                    activetab: 'users'
                })
                .when('/user/:id', {
                    controller: 'userController',
                    templateUrl: 'main/partials/user-form.html',
                    activetab: 'users'
                })
                .when('/user-groups', {
                    controller: 'userGroupController',
                    templateUrl: 'main/partials/user-groups.html',
                    activetab: 'userGroups'
                })
                .when('/user-group', {
                    controller: 'userGroupController',
                    templateUrl: 'main/partials/user-group-form.html',
                    activetab: 'userGroups'
                })
                .when('/user-group/:id', {
                    controller: 'userGroupController',
                    templateUrl: 'main/partials/user-group-form.html',
                    activetab: 'userGroups'
                })
                .when('/system-codes', {
                    controller: 'systemCodesController',
                    templateUrl: 'main/partials/system-codes.html',
                    activetab: 'systemCodes'
                })
                .when('/divisions', {
                    controller: 'divisionController',
                    templateUrl: 'main/partials/divisions.html',
                    activetab: 'divisions'
                })
                .when('/divisions/:id/:edit?', {
                    controller: 'divisionController',
                    templateUrl: 'main/partials/division.html',
                    activetab: 'divisions'
                })
                .otherwise('/users')
            
        }
    ])

    .config(['$httpProvider', function ($httpProvider) {
            $httpProvider.interceptors.push('baseApp.requestsErrorHandler');
        }
    ])

    .run(['ENV_VARS', '$rootScope', '$location', '$interval', '$log', 'loginApp.userService',
        function(ENV_VARS, $rootScope, $location, $interval, $log, userService){
            //Go to login if no tokens
            $rootScope.$on( "$routeChangeStart", function(event, next, current) {
                //Check if with token and user info
                var user = userService.getUserInfo();
				
                if(user.token == undefined || user.token == null || user.nosqlid == undefined || user.nosqlid == null  || !user.manageUM) {
                    $log.warn("No USER_ID , MANAGE_UM, or USER_TOKEN found. Redirecting to login page.");

                    userService.goToLogin();
                }
            })

            //Interval for token refresh
            $interval(function() {
                userService.refreshToken();
            }, ENV_VARS.tokenRefreshTimeOut);
        }
    ])

    // interceptors

    // filters
    
    // services
    .factory('systemCodeService', ['$q', '$httpParamSerializer', 'baseApp.baseService', 'ResourceContext', mainServices.systemCodeService])
    .factory('userService', ['$q', '$filter', 'Notification', 'baseApp.baseService', mainServices.userService])
    .factory('authorityService', ['$q', 'baseApp.baseService', mainServices.authorityService])
    .factory('userGroupService', ['$q', '$filter', 'Notification', 'baseApp.baseService', mainServices.userGroupService])
    .factory('divisionService', ['$q', '$filter', 'Notification', 'baseApp.baseService', mainServices.divisionService])

    // directives
    .directive('divisionListForm', [mainDirectives.divisionListForm])
    .directive('toKey', [mainDirectives.toKey])

    // controllers
    .controller('mainController', ['$scope','$log', '$controller', 'loginApp.userService', mainControllers.mainController])
    .controller('headerController', ['$scope', 'loginApp.userService', mainControllers.headerController])
    .controller('userController', ['$scope', '$q', '$log', '$routeParams', '$uibModal', 'userService', 'userGroupService', 'divisionService', mainControllers.userController])
    .controller('userFormController', ['$scope', '$log', '$controller', '$uibModalInstance', 'userService', mainControllers.userFormController])
    .controller('userGroupController', ['$scope', '$log', '$routeParams', 'authorityService', 'userGroupService', mainControllers.userGroupController])
    .controller('systemCodesController', ['$scope', mainControllers.systemCodesController])
    .controller('divisionController', ['$scope', '$log', '$routeParams', 'divisionService', mainControllers.divisionController])
;