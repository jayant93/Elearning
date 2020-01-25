/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    userService : function($q, $filter, $log, ENV_VARS, baseService, localStorageService){
        var appUrl = ENV_VARS.appUrl;
        var loginUrl = ENV_VARS.loginUrl;
        var keyToken = "TOKEN";
        var keyUserId = "USER_ID";
        var keyUserName = "USER_NAME";
        var keyUserFirstName = "USER_FIRSTNAME";
        var keyUserLastName = "USER_LASTNAME";
		var keyManageUM = "MANAGE_UM";

        return {
            login : function (username, password) {
                var deferred = $q.defer();
                var url =  "/login";

                baseService.postForm( url,
                    {
                        data: { username : username, password: password }
                    }
                ).then(function(result){

					if (!result.manageUM) {
						
						var error = {};
						error.message = 'No authorization to Access User Management.';
						deferred.reject(error);
						
					}else{
						
						//Add result to local storage
						localStorageService.set(keyUserName, username);
						localStorageService.set(keyToken, result.token);
						localStorageService.set(keyUserId, result.id);
						localStorageService.set(keyUserFirstName, result.firstName);
						localStorageService.set(keyUserLastName, result.lastName);
						localStorageService.set(keyManageUM, result.manageUM);
					
						 //Get user info, put to local storage
						deferred.resolve(appUrl);
					}
                   
                    
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },

            getUserInfo : function(){
                return {
                    firstName :  localStorageService.get(keyUserFirstName),
                    lastName :  localStorageService.get(keyUserLastName),
                    fullName: localStorageService.get(keyUserFirstName) + " " + localStorageService.get(keyUserLastName),
                    username: localStorageService.get(keyUserName),
                    nosqlid: localStorageService.get(keyUserId),
                    token: localStorageService.get(keyToken),
					manageUM : localStorageService.get(keyManageUM)
                }

            },

            removeUserInfo : function(){
                localStorageService.remove(keyUserFirstName);
                localStorageService.remove(keyUserLastName);
                localStorageService.remove(keyUserName);
                localStorageService.remove(keyUserId);
                localStorageService.remove(keyToken);
				localStorageService.remove(keyManageUM);
            },

            goToLogin : function () {
                window.location.href = loginUrl;
            },

            getByUserGroup : function (group) {
                var deferred = $q.defer();

                baseService.get("/user/search/findByUsergroupName?name=" + group, { cache : true })
                    .then(function(result){
                        var userList = result._embedded.baseUsers;
                        deferred.resolve(userList);
                    });

                return deferred.promise;
            },

            getUserById : function (list, nosqlId) {
                return $filter('filter')(list,  nosqlId)[0];
            },

            refreshToken : function(){
                baseService.post("/refreshToken").then(function(result){
                    //Add result to local storage
                    localStorageService.set(keyToken, result.token);
                });
            }
        }
    }
}
