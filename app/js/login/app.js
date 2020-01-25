/**
 * Created by jpereira on 12/19/2016.
 */
// app sources
var baseApp = require('../base.js');

var translation = require('../login/translations/translation_en.js');

var loginServices = require('../login/services.js');
var loginControllers = require('../login/controllers.js');

angular.module('loginApp', ['baseApp', 'baseApp.envConfig'])

    //Config
    .config([
        '$translateProvider',
        function($translateProvider) {
            $translateProvider.useSanitizeValueStrategy('escapeParameters');
            $translateProvider.translations('en', translation.en);
            $translateProvider.preferredLanguage('en');
        }
    ])

    //Config for recaptcha
    .config(['ENV_VARS', 'vcRecaptchaServiceProvider', function(ENV_VARS, vcRecaptchaServiceProvider){
        vcRecaptchaServiceProvider.setDefaults({
            key: ENV_VARS.recaptchaPublic,
            theme: 'light',
            size: 'normal',
            lang: 'en'
        });
    }])

    // services
    .factory('loginApp.userService', ['$q', '$filter', '$log', 'ENV_VARS', 'baseApp.baseService', 'localStorageService', loginServices.userService])

    //controllers
    .controller('loginController', ['$scope', 'ENV_VARS', 'loginApp.userService', "baseApp.recaptchaService", 'vcRecaptchaService', 'localStorageService', loginControllers.loginController]);

;