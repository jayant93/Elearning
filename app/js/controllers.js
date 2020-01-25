/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    baseController : function($scope, $rootScope, $location, $route, utilityService) {
        $rootScope.$route = $route

        $scope.changeLocation = function(url){
            $location.url(url);
        }

        // date picker related attributes
        $scope.shortDateFormat = 'MM/dd/yyyy';
        $scope.dateformat = 'MMM dd, yyyy';
        $scope.datetimeFormat = 'MMM dd, yyyy hh:mm a';
        $scope.datePickerOptions = utilityService.datePickerOptions();
        $scope.openDate = function(name) {
            $scope[name] = $scope[name] || {};
            $scope[name].opened = true;
        }
    },
}