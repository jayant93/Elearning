/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    /**
     @description This combines the best features of ng-if and ng-show by lazy loading the DOM element and then simply showing and hiding it instead of removing it

     @see http://developers.lendio.com/blog/combining-ng-if-and-ng-show-for-better-angularjs-performance/
     */
    ngLazyShow : function ($animate) {
        return {
            multiElement: true,
            transclude: 'element',
            priority: 600,
            terminal: true,
            restrict: 'A',
            link: function ($scope, $element, $attr, $ctrl, $transclude) {
                var loaded;
                $scope.$watch($attr.ngLazyShow, function ngLazyShowWatchAction(value) {
                    if (loaded) {
                        $animate[value ? 'removeClass' : 'addClass']($element, 'ng-hide');
                    }
                    else if (value) {
                        loaded = true;
                        $transclude(function (clone) {
                            clone[clone.length++] = document.createComment(' end ngLazyShow: ' + $attr.ngLazyShow + ' ');
                            $animate.enter(clone, $element.parent(), $element);
                            $element = clone;
                        });
                    }
                });
            }
        };

    },

    /**
     @see http://www.technofattie.com/2014/07/27/easy-loading-indicator-when-switching-views-in-angular.html
     */
    routeLoadingIndicator : function($rootScope, $log) {
        return {
            restrict : 'AE',
            templateUrl : 'app/partials/misc/route-loading.html',
            replace : true,
            link : function(scope, elm, attrs) {
                $rootScope.routeLoading = false;

                $rootScope.$on('$routeChangeStart', function() {
                    $rootScope.routeLoading = true;
                    $log.debug("Route change is starting.");
                });

                $rootScope.$on('$routeChangeSuccess', function() {
                    $rootScope.routeLoading = false;
                    $log.debug("Route change has successfully completed.");
                });
            }
        };
    },

    /**
     @name confirmOnExit
     @description Prompts user if he/she tries to navigate away the page if the form is dirty/modified. This allows
     the user to cancel the route change in case there are important changes to the form that needs to be saved first.

     Note:
     If the form has an 'ng-submitted' class, the prompt will not show even if the form is dirty. This class acts a flag
     to let the directive know that this form is navigating away because it was submitted. The controller of the form
     can add this flag/class upon the submission of the form by the user, either through directives (ng-click, ng-submit, etc) or manually
     through javascript.

     @element Attribute (on <form> elements only)
     @param confirmMessageWindow message to display if the user closes the browser or does a reload
     @param confirmMessageRoute message to display if the user clicks a link to other routes
     @param confirmMEssage default fallback message

     @see http://stackoverflow.com/a/28905954/340290
     */
    confirmOnExit : function($rootScope, $log, $location) {
        return {
            restrict: 'A',
            scope: {
                confirmMessageWindow: '@',
                confirmMessageRoute: '@',
                confirmMessage: '@'
            },
            link: function($scope, elem, attrs) {
                var $locationChangeStartUnbind = $scope.$on('$locationChangeStart', function(event, next, current) {
                    if(!elem.hasClass('ng-submitted') && elem.hasClass('ng-dirty') && !confirm($scope.confirmMessageRoute || $scope.confirmMessage)) {
                        event.preventDefault();
                        $rootScope.routeLoading = false;
                    }
                });

                window.onbeforeunload = function() {
                    if(!elem.hasClass('ng-submitted') && elem.hasClass('ng-dirty')) {
                        return $scope.confirmMessageWindow || $scope.confirmMessage;
                    }
                }

                $scope.$on('$destroy', function() {
                    window.onbeforeunload = null;
                    $locationChangeStartUnbind();
                });
            }
        };
    },

    usSpinner: function($http, $rootScope){
        return{
            link: function (scope, elm, attrs)
            {
                $rootScope.spinnerActive = false;
                scope.isLoading = function () {
                    return $http.pendingRequests.length > 0;
                };

                scope.$watch(scope.isLoading, function (loading)
                {
                    $rootScope.spinnerActive = loading;
                    if(loading){
                        elm.removeClass('ng-hide');
                    }else{
                        elm.addClass('ng-hide');
                    }
                });
            }
        }
    },

    page: function(){
        return {
            restrict: 'E',
            scope: {
                searchResults: '=',
                loadList: '&'
            },
            templateUrl: 'pagination.html',
        };
    },

    resultInfo: function(){
        return {
            restrict: 'EA',
            scope: {
                searchResults: '='
            },
            templateUrl: 'result-info.html',
        };
    },

    disallowSpaces: function () {
        return {
            restrict: 'A',
            link: function($scope, $element) {
                $element.bind('input', function() {
                    jQuery(this).val(jQuery(this).val().replace(/ /g, ''));
                });
            }
        }
    },

    capitalize: function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, modelCtrl) {
                var capitalize = function(inputValue) {
                    if (inputValue == undefined) inputValue = '';
                    var capitalized = inputValue.toUpperCase();
                    if (capitalized !== inputValue) {
                        modelCtrl.$setViewValue(capitalized);
                        modelCtrl.$render();
                    }
                    return capitalized;
                }
                modelCtrl.$parsers.push(capitalize);
                capitalize(scope[attrs.ngModel]); // capitalize initial value
            }
        }
    }
}