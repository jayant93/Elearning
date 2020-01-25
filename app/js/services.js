/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    httpRequestInterceptor : function (ENV_VARS, localStorageService) {
        var keyUserId = "USER_ID";
        var keyUserFirstName = "USER_FIRSTNAME";
        var keyUserLastName = "USER_LASTNAME";

        return {
            request: function (config) {
                // use this to prevent destroying other existing headers
                config.headers['OT4-APP-SECRET'] =  ENV_VARS.appSecret;
                config.headers['OT4-USER-TOKEN'] = localStorageService.get("TOKEN");

                config.data = config.data || {};
                config.data.auditUsername =  localStorageService.get(keyUserFirstName) + " " + localStorageService.get(keyUserLastName);
                config.data.auditUserNosqlId = localStorageService.get(keyUserId);

                return config;
            }
        };
    },

    requestsErrorHandler : function(ENV_VARS, $q, $log, $location, $injector, $filter, localStorageService) {
        var keyToken = "TOKEN";
        var keyUserId = "USER_ID";
        var keyUserName = "USER_NAME";
        var keyUserFirstName = "USER_FIRSTNAME";
        var keyUserLastName = "USER_LASTNAME";
		var keyManageUM = "MANAGE_UM";

        var notification = null;
        var getNotification = function() {
            if (!notification) {
                notification = $injector.get('Notification');
            }
            return notification;
        };

        return {
            // --- Response interceptor for handling errors generically ---
            responseError: function(rejection) {
                var shouldHandle = (rejection && rejection.config && rejection.config.headers && !rejection.config.hideErrorNotif );

                // --- Your generic error handling goes here ---
                if (shouldHandle) {
                    getNotification()
                        .error({ message: $filter('translate')('ERROR_' + rejection.status) });

                    //If connection unauthorized go to login page
                    if(rejection.data.error == "Unauthorized"){
                        //Remove user info
                        localStorageService.remove(keyUserFirstName);
                        localStorageService.remove(keyUserLastName);
                        localStorageService.remove(keyUserName);
                        localStorageService.remove(keyUserId);
                        localStorageService.remove(keyToken);
						localStorageService.remove(keyManageUM);
						
                        //Go to login page
                        window.location.href = ENV_VARS.loginUrl;
                    }
                }

                return $q.reject(rejection);
            }
        };
    },
    
    baseService: function (ENV_VARS, $http, $log, $q, $filter, $httpParamSerializer, ResourceContext, HalResource) {
        var context = new ResourceContext(HalResource);

        var apiBasePath = ENV_VARS.apiUrl;

        var postFormHeaders = function (header) {
            header = header || {};
            header['Content-Type'] = 'application/x-www-form-urlencoded';

            return header;
        };

        return {
            getBaseUrl: function () {
                return apiBasePath;
            },

            /**
             * Configs
             * @param resourceUrl
             * @param config
             * @returns {*}
             */
            get: function (resourceUrl, config) {
                var deferred = $q.defer();

                config = config || {};
                config.method = 'GET';
                config.url =  apiBasePath + resourceUrl;

                var url = config.url;
                $log.info("Getting from the server at " + url);
                $http(config).then(function (result) {
                    $log.debug("Process succesfully completed.");
                    $log.debug(result);

                    deferred.resolve(result.data);
                }, function (error, status, headers, config) {
                    $log.error("Failed to GET data from " + url + ": " + error);

                    deferred.reject(error);
                });

                return deferred.promise;
            },

            postForm: function (resourceUrl, config) {
                var deferred = $q.defer();

                config = config || {};
                config.method = 'POST';
                config.headers = postFormHeaders(config.headers);
                config.data = jQuery.param(config.data);

                config.url = apiBasePath + resourceUrl;

                var url = config.url;
                $http(config).then(function (result) {
                    $log.debug("Process succesfully completed. Returning result ");
                    $log.debug(result);

                    deferred.resolve(result.data);
                }, function (error, status, headers, config) {
                    $log.error("Failed to POST data to " + url);

                    deferred.reject(error);
                });

                return deferred.promise;
            },

            post: function (resourceUrl, config) {
                var deferred = $q.defer();

                config = config || {};
                config.method = 'POST';

                config.url = apiBasePath + resourceUrl;

                var url = config.url;
                $http(config).then(function (result) {
                    $log.debug("Process succesfully completed. Returning result ");
                    $log.debug(result);

                    deferred.resolve(result.data);
                }, function (error, status, headers, config) {
                    $log.error("Failed to POST data to " + url);

                    deferred.reject(error);
                });

                return deferred.promise;
            },

            update: function (resourceUrl, config) {
                var deferred = $q.defer();

                config.method = 'PUT';
                config.url = apiBasePath + resourceUrl;
                config.headers = { 'Content-Type': 'application/json' };

                var url = config.url;

                $log.info("PUTting data to " + url + " with data");
                $log.debug(config.data)

                $http(config).then(function (result) {
                    $log.debug("Process succesfully completed. Returning result ");
                    $log.debug(result);

                    deferred.resolve(result.data);
                }, function (error, status, headers, config) {
                    $log.error("Failed to PUT data to " + url);

                    $log.debug(error);
                    deferred.reject(error);
                });

                return deferred.promise;
            },

            delete: function (resourceUrl, id) {
                var deferred = $q.defer();
                var config = {};

                config.method = 'DELETE';
                config.headers = postFormHeaders(config.headers);
                config.url = apiBasePath + resourceUrl;

                var url = config.url;
                $log.info("Deleting from the server at " + url);

                $http(config)
                    .then(function (result) {
                        $log.debug("Process succesfully completed. Returning result ");
                        $log.debug(result);

                        deferred.resolve(result.data);
                    }, function (error, status, headers, config) {
                        $log.error("Failed to DELETE data from " + url + " with record id of: " + id);
                        $log.debug(error);

                        deferred.reject(error);
                    });

                return deferred.promise;
            },
            /**
             * Returns the angularjs-hypermedia resource object from the default application resource context
             *
             * @param the URI  of the resource
             * @returns the resource object used for operations
             */
            getResource: function (uri) {
                return context.get(apiBasePath + '/' + uri);
            },

            findByExample: function (resource, example) {
                var params = $httpParamSerializer(example);
                return this.getResource(resource + '/search/findByExample?' + params).$get();
            },

            findAll: function (resource) {
                var deferred = $q.defer();

                this.get(resource + '/findAll',{ cache: true })
                    .then(function (result) {  
                        deferred.resolve(result);
                    });

                return deferred.promise;
            }
        };
    },

    fileService: function ($q, $log, baseService, Upload, FileSaver) {
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            upload: function (file) {
                var deferred = $q.defer();
                var url = baseService.getScanningUrl() + 'misc/document-file/upload-file';

                Upload.upload({
                    url: url,
                    method: 'POST',
                    file: file,
                    data: {f: file},
                    uploadEventHandlers: {
                        progress: function (evt) {
                            if (evt.lengthComputable) {
                                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                                file.progress = progressPercentage;

                                $log.debug('File upload progress: ' + progressPercentage + '% of ' + evt.config.file.name);
                                deferred.notify(evt);
                            }
                        }
                    }
                }).then(function (data, status, headers, config) {
                    $log.info("Successfully uploaded file " + file.name);
                    deferred.resolve(data.data);

                }, function (data, status, headers, config) {
                    $log.error("Failed to upload file. Status is " + status);
                    deferred.reject(data);

                });

                return deferred.promise;
            },

            getFile: function (file) {
                var deferred = $q.defer();
                var url = "misc/document-file/" + file.nosqlid + "/" + file.fileName;

                //Get file
                baseService.get(url, {app: 'file', responseType: 'arraybuffer'}).then(function (result) {
                    //Download
                    FileSaver.saveAs(new Blob([result], {type: file.contentType}), file.fileName);

                    deferred.resolve(result);
                });

                return deferred.promise;
            }
        });
    },

    utilityService: function () {
        return {
            titleCase: function (str) {
                return str.replace(/([^\W_]+[^\s-]*) */g,
                    function (txt) {
                        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
                    }
                );
            },
            datePickerOptions: function () {
                return {
                    'datepicker-append-to-body': true,
                    'close-on-date-selection': true,
                    'on-open-focus': true,
                    'show-weeks': false,
                    'initDate': new Date(),
                    'dateDisabled': function (data) {
                        // disable weekend selection
                        /*var date = data.date,
                         mode = data.mode;
                         return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);*/
                    },
                    'ng-model-options': "{ timezone: 'utc' }"
                };
            }
        };
    },

    recaptchaService: function($q, baseService){
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            verify : function(response){
                var deferred = $q.defer();
                baseService.postForm("/recaptcha",
                    {
                        data: {response: response},
                        hideErrorNotif : true
                    }
                ).then(function(result){
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });
        
                return deferred.promise;
            }
        });

    }
}