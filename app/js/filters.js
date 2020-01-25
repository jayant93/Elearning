/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    encodeURI : function() {
        return window.encodeURI;
    },
    range : function() {
        return function(n) {
            var res = [];
            for (var i = 1; i <= n; i++) {
                res.push(i);
            }
            return res;
        };
    },
    trust :  function($sce) {
        return function (htmlCode) {
            return $sce.trustAsHtml(htmlCode);
        }
    }
}
