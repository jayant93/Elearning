/**
 * @author jpereira on 3/20/2017.
 */
module.exports = {
    divisionListForm: function () {
        return {
            restrict: 'EA',
            scope: {
                ngModel: '=',
                divisionList: '=',
                onTypeAhead: '&',
                typeaheadOnSelect: '&'
            },
            templateUrl: 'main/partials/templates/division-list.html',
        };
    },
    toKey : function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, modelCtrl) {
                var convertToKey = function(inputValue) {
                    if (inputValue == undefined) inputValue = '';
                    var key = inputValue.toString().replace(/[^A-Z0-9]/ig, "_");
                    if (key !== inputValue) {
                        modelCtrl.$setViewValue(key);
                        modelCtrl.$render();
                    }
                    return key;
                }
                modelCtrl.$parsers.push(convertToKey);
                convertToKey(scope[attrs.ngModel]); // capitalize initial value
            }
        }
    }
}