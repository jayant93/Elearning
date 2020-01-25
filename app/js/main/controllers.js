/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    mainController : function($scope, $log, $controller, userService){
        // "extends" the baseController by copying its scope, thus gaining access to its member functions
        $controller('baseApp.baseController', { $scope: $scope });

        $scope.init = function(){
            $scope.currentUser = userService.getUserInfo();
        }
    },

    headerController : function($scope, userService) {
        $scope.logout = function(){
            userService.removeUserInfo();
            userService.goToLogin();
        }

    },

    userController : function($scope, $q, $log, $routeParams, $uibModal, userService, userGroupService, divisionService) {
    	
    	$scope.createNewUser = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'main/partials/templates/new-user-form.html',
                controller: 'userFormController',
                controllerAs: '$ctrl',
                resolve: {}
            });

            modalInstance.result.then(function (response) {
                $scope.selected = response;
                $log.info(response);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.init = function(){
        	$scope.tempPassword = "";
        	$scope.showAdvancedSearchGuide = false
        	$scope.showAdvSearchSection = false;
        	$scope.searchType = "";
            $scope.searchForm = {};
            $scope.search = {
            	keyword: "",
            	advanceSearchCriteria:{},
            	page: 0,
            	size: 20
            };

            //Load user list            
            userService.loadUsers({}, 1).then(function (result) {
                $scope.searchResults = result;
                $scope.searchResults.type = 'standard';
            });

        }

        $scope.loadUser = function(){
        	$scope.tempUser = {};
            userService.loadUser($routeParams.id).then(function(user){
                $scope.user = user;
                $scope.tempUser = angular.copy($scope.user);
                $scope.generateTemporaryPassword();
            });
        }

        $scope.getDivisionList = function(name){
            if(name && name.length > 0){
                return divisionService.loadListByName(name);
            }
        }

        $scope.onDivisionSelect = function (item) {
            $scope.user.homeDepartment = item;
        };

        $scope.onDivisionMultiSelect = function (division) {
            if($scope.user.divisions === null || $scope.user.divisions === undefined) {
                $scope.user.divisions = {};
            }
            var index = $scope
                .user.divisions.indexOf(division.name);
            if(index < 0){
                $scope.user.divisions
                    .push(division);
            }

            $scope.user._userDivision = {};
        }

        $scope.removeUserDivision = function(division){
            var index = $scope
                .user.divisions.indexOf(division);
            $scope.user.divisions.splice(index, 1);
        }
        
        //retrieve all division list without parent grouping for home orgunit dropdown
        $scope.removeDivisionParentGrouping = function(){
        	return function(item){
        		if (item.key != undefined){
        			return true;
        		} else {
        			return false;
        		}
        	}
        }
        
        //remove to the list of divisions once selected from home orgunit
        $scope.removeToDivision = function(){
			for (var i=0; i<$scope.divisions.length; i++){
				if ($scope.divisions[i].key == $scope.user.homeDepartment.key){
					$scope.divisions[i].disabled = true;
					$scope.divisions[i].ticked = false;
				} else {
					$scope.divisions[i].disabled = false;
				}
			}
        }

        // Load all users depending on the page selected
        $scope.loadUsers = function(page){
        	userService.loadUsers({}, page).then(function (result) {
                $scope.searchResults = result;
                $scope.searchResults.type = 'standard';
            });
        }
        
        // Load all users based on search and the page selected
        $scope.loadUsersSearch = function(page){
        	$scope.search.page = page - 1;
        	if ($scope.searchType === 'keyword') {
        		userService.keywordSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        	} else {
        		userService.advancedSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        	}
        }
        
        $scope.keywordSearch = function() {
        	$scope.search.page = 0;
        	$scope.search.size = 20;
        	$scope.search.advanceSearchCriteria = {};
        	
        	// Check if keyword contains keyword term
        	// If none, then do the normal findAll query
        	if (/^\s*$/.test($scope.search.keyword)) {
        		userService.loadUsers({}, 1).then(function (result) {
                    $scope.searchResults = result;
                    $scope.searchResults.type = 'standard';
                });
        	} else { // If there is a keyword term, then do keyword search
	        	userService.keywordSearch($scope.search).then(function (data) {
	        		$scope.searchResults = data;
	        		$scope.searchResults.type = 'search';
	        	});
	        	$scope.searchType = "keyword";
        	}
        	$scope.showAdvancedSearchGuide=false;
        }
        
        $scope.advancedSearch = function() {
        	$scope.search.page = 0;
        	$scope.search.size = 20;
        	$scope.search.keyword = "";
        	var keyCtr = 0;
        	
        	// Check if all advance search criteria field is empty or not
        	Object.keys($scope.search.advanceSearchCriteria).forEach(function(key){
        		if (/^\s*$/.test($scope.search.advanceSearchCriteria[key])) {
        			keyCtr++;
        		}
        	});
        	// Set criteria object to empty when all fields are empty
        	if (keyCtr === Object.keys($scope.search.advanceSearchCriteria).length) {
        		$scope.search.advanceSearchCriteria = {};
        	}
        	
        	// If advance search criteria is empty then do a normal find all query
        	if (Object.keys($scope.search.advanceSearchCriteria).length === 0) {
        		userService.loadUsers({}, 1).then(function (result) {
                    $scope.searchResults = result;
                    $scope.searchResults.type = 'standard';
                });
        	} else { // If not, then do advanced search based on the given criteria
	        	userService.advancedSearch($scope.search).then(function (data) {
	        		$scope.searchResults = data;
	        		$scope.searchResults.type = 'search';
	        	});
	        	$scope.searchType = "advanced";
        	}
        	
        	$scope.showAdvancedSearchGuide=true;
        	$scope.showAdvSearchSection=false;
        }
        
        $scope.clearAdvancedSearchForm = function() {
        	$scope.search.advanceSearchCriteria = {};
        }

        $scope.loadUserGroups = function(sample){
            var deferred = $q.defer();

            userGroupService.loadUserGroups({ name: sample }).then(function(data) {
                deferred.resolve(data.results);
            });

            return deferred.promise;
        }

        $scope.onGroupSelect = function (group) {
            //Delete if existing
            var index = $scope
                .user._groups.indexOf(group.name);

            //Add only if not existing
            if(index < 0){
                var indexOnRemovedGroups = $scope
                    .user._removedGroups.indexOf(group.name);

                if(indexOnRemovedGroups >= 0){
                    $scope.user._removedGroups.splice(indexOnRemovedGroups, 1);
                }else{
                    $scope.user._addedGroups.push(group.name);
                }

                $scope.user._groups
                    .push(group.name);
                $scope.user.groups
                    .push(group);
            }

            $scope.user._userGroup = {};
        }

        $scope.userStatusOnChange = function() {
        	if ($scope.tempUser.credential.status !== "Pending" && $scope.user.credential.status === "Pending") {
        		$scope.user.credential.tempPassword = $scope.tempPassword; // Generate random password with 8 characters
        	}
        }
        
        $scope.generateTemporaryPassword = function() {
        	$scope.tempPassword = Math.random().toString(36).substring(3);
        }
        
        $scope.removeUserGroup = function(group){
            var index = $scope
                .user._groups.indexOf(group);

            //Remove to list
            $scope.user.groups.splice(index, 1);
            $scope.user._groups.splice(index, 1);

            var indexOnAddedGroups = $scope
                .user._addedGroups.indexOf(group);

            if(indexOnAddedGroups >= 0){
                $scope.user._addedGroups.splice(indexOnAddedGroups, 1);
            }else{
                $scope.user._removedGroups.push(group);
            }
        }

        $scope.updateUser = function(){
            $log.info('Updating user...');

            if ($scope.user.credential.status !== "Pending") {
            	$scope.user.credential.tempPassword = "";
            }
            
            userService.update($scope.user).then(function(){
                $log.info('User successfully updated!');

                $scope.errorMessages = [];
                $scope.user._addedGroups = [];
                $scope.user._removedGroups = [];

                $scope.user.displayGroups = $scope.user._groups.join(",");
                $scope.tempUser = angular.copy($scope.user);
                $scope.generateTemporaryPassword();
            }, function(error){
				
				$log.info('Updating user error found.');
				
                $log.debug(error);

                $scope.errorMessages = [];
                $scope.errorMessages.push(error.data);


                //Restore user group
                if(error.data && error.data.arguments && error.data.arguments[0] == "Groups"){
                    $scope.user._groups = $scope.user
                        .displayGroups.replace(/^\s+|\s+$/g,"").split(/\s*,\s*/);

                    $scope.user._addedGroups = [];
                    $scope.user._removedGroups = [];
                }
            });
        }

    },

    userFormController : function($scope, $log, $controller, $uibModalInstance, userService){
        // "extends" the baseController by copying its scope, thus gaining access to its member functions
        $controller('baseApp.baseController', { $scope: $scope });
        $controller('userController', { $scope: $scope });

        $scope.initUser = function () {
            $scope.user = userService.initUser();
            $scope.createNew = false;
        }

        $scope.close = function(){
            $uibModalInstance.close();
        }

        $scope.saveUser = function(){
            $log.info('Saving user...');

            if ($scope.user.credential.status === "Pending") {
            	$scope.user.credential.tempPassword = $scope.user.credential.newPassword;
            }
            
            userService.save($scope.user)
                .then(function (result) {
                    $scope.errorMessages = [];

                    // Go to view
                    if(!$scope.createNew){
                        $uibModalInstance.close();
                        $scope.changeLocation("user/" + result.nosqlId);
                    }else{
                        //Create an empty user
                        $scope.user = userService.initUser();
                        $scope.createNew = false;
                    }
                },function (error) {
                    $scope.errorMessages = error;
                });
        }
    },

    userGroupController : function($scope, $log, $routeParams, authorityService, userGroupService) {
        $scope.init = function(){
            $scope.createNew = false;
            $scope.userGroup = {};

            //Load user group if edit
            var userGroupId = $routeParams.id;

            if(userGroupId){
                userGroupService.loadUserGroup(userGroupId).then(function (result) {
                    $scope.userGroup = result;
                });
            }

            authorityService.getAll().then(function (result) {
               $scope.authorities = result;
            });
			
			authorityService.getAllInTree().then(function (result) {
               $scope.authoritiesInTree = result;
            });

        }
        
        $scope.initSearchParams = function() {
        	$scope.showAdvancedSearchGuide = false;
        	$scope.showAdvSearchSection = false;
        	$scope.searchType = "";
            $scope.searchForm = {};
            $scope.search = {
            	keyword: "",
            	advanceSearchCriteria:{},
            	page: 0,
            	size: 20
            };
            
            // Load all usergroup list
            userGroupService.loadUserGroups({}, 1).then(function(result){
				
                $scope.searchResults = result;
                $scope.searchResults.type = 'standard';
            });
        }
        
        // Keyword search, check if keyword term is empty or not
        // If empty then do a normal search, if not then do keyword search
        $scope.keywordSearch = function() {
        	$scope.search.page = 0;
        	$scope.search.size = 20;
        	$scope.search.advanceSearchCriteria = {};
        	
        	if (/^\s*$/.test($scope.search.keyword)) {
        		userGroupService.loadUserGroups({}, 1).then(function(result){
                    $scope.searchResults = result;
                    $scope.searchResults.type = 'standard';
                });
        	} else {
	        	userGroupService.keywordSearch($scope.search).then(function (data) {
	        		$scope.searchResults = data;
	        		$scope.searchResults.type = 'search';
	        	});
	        	$scope.searchType = "keyword";
        	}
        	$scope.showAdvancedSearchGuide=false;
        }
        
        // Advanced search, check if advanced search criteria is empty or not
        // If empty then query all, if not then do advanced search
        $scope.advancedSearch = function() {
        	$scope.search.page = 0;
        	$scope.search.size = 20;
        	$scope.search.keyword = "";
        	var keyCtr = 0;
        	
        	Object.keys($scope.search.advanceSearchCriteria).forEach(function(key){
        		if (/^\s*$/.test($scope.search.advanceSearchCriteria[key])) {
        			keyCtr++;
        		}
        	});
        	
        	if (keyCtr === Object.keys($scope.search.advanceSearchCriteria).length) {
        		$scope.search.advanceSearchCriteria = {};
        	}
        	
        	if (Object.keys($scope.search.advanceSearchCriteria).length === 0) {
                userGroupService.loadUserGroups({}, 1).then(function(result){
                    $scope.searchResults = result;
                    $scope.searchResults.type = 'standard';
                });
        	} else {
	        	userGroupService.advancedSearch($scope.search).then(function (data) {
	        		$scope.searchResults = data;
	        		$scope.searchResults.type = 'search';
	        	});
	        	$scope.searchType = "advanced";
        	}
        	$scope.showAdvancedSearchGuide=true;
        	$scope.showAdvSearchSection=false;
        }
        
        $scope.clearAdvancedSearchForm = function() {
        	$scope.search.advanceSearchCriteria = {};
        }

        // Load all user groups depending on the page selected
        $scope.loadUserGroups = function(page){
        	userGroupService.loadUserGroups({}, page).then(function(result){
                $scope.searchResults = result;
                $scope.searchResults.type = 'standard';
            });
        }
        
        // Load all user groups depending on search criteria and page selected
        $scope.loadUsergroupsSearch = function(page){
        	$scope.search.page = page - 1;
        	if ($scope.searchType === 'keyword') {
        		userGroupService.keywordSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        	} else {
        		userGroupService.advancedSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        	}
        }

        $scope.saveUserGroup = function(){
            $log.info('Saving user group...');

            userGroupService.saveUserGroup($scope.userGroup).then(function () {
                if($scope.createNew){
                    //Create an empty user group
                    $scope.userGroup = {};
                    $scope.createNew = false;
                }else{
                    $scope.changeLocation("user-groups");
                }
            });
        }
		
		$scope.showHideChildren = function(authz) {
			
			if (authz['showChildren']) {
				authz['showChildren'] = false;
			}else {
				authz['showChildren'] = true;
			}
		}
        
        $scope.authCheckboxChange = function(auth){
        	var viewAuth = $scope.userGroup.authorityNames.indexOf(auth.key);
        	
			// removes CHECK for ADD, EDIT, DELETE, EXPORT, PRINT when MANAGE or VIEW is unchecked. 
        	if ((auth.key.startsWith("VIEW_") || auth.key.startsWith("MANAGE_")) && viewAuth < 0){
				
				//use VIEW_ 5 char
				var str = auth.key.substring(5);
				
				
				if (auth.key.startsWith("MANAGE_")) {
					str = auth.key.substring(7);
				}
        		
        	
				var viewAuthIfManage = $scope.userGroup.authorityNames.indexOf("VIEW_"+str);
    			if (viewAuthIfManage >= 0)
    				$scope.userGroup.authorityNames.splice(viewAuthIfManage, 1);
				
    			var addAuth = $scope.userGroup.authorityNames.indexOf("ADD_"+str);
    			if (addAuth >= 0)
    				$scope.userGroup.authorityNames.splice(addAuth, 1);
    			
    			var editAuth = $scope.userGroup.authorityNames.indexOf("EDIT_"+str);
    			if (editAuth >= 0)
    				$scope.userGroup.authorityNames.splice(editAuth, 1);
    			
    			var deleteAuth = $scope.userGroup.authorityNames.indexOf("DELETE_"+str);
    			if (deleteAuth >= 0)
    				$scope.userGroup.authorityNames.splice(deleteAuth, 1);
				
				var exportAuth = $scope.userGroup.authorityNames.indexOf("EXPORT_"+str);
    			if (exportAuth >= 0)
    				$scope.userGroup.authorityNames.splice(exportAuth, 1);
				
				var printAuth = $scope.userGroup.authorityNames.indexOf("PRINT_"+str);
    			if (printAuth >= 0)
    				$scope.userGroup.authorityNames.splice(printAuth, 1);
				
        	} 
        	else {
				// Check MANAGE and VIEW when ADD, EDIT, DELETE, PRINT, EXPORT gets checked.
        		var authority = $scope.userGroup.authorityNames.indexOf(auth.key);
        		if (authority >= 0){
        			var strLevel = auth.level.split(".");
        			var str = "";
        			
        			switch (strLevel[2]){
        				case "02":
							// add
							// subtring ADD_ 4 char
        					str = auth.key.substring(4); break;
        				case "03":
							// edit
							// substring EDIT_ 5 char
        					str = auth.key.substring(5); break;
        				case "04":
							// delete
							// substring DELETE_ 7 char
        					str = auth.key.substring(7); break;
							
						//DSWD-ILO specific code
						case "05":
							// export
							// substring EXPORT_ 7 char
							str = auth.key.substring(7); break;
						case "06":
							// print
							// substring PRINT_ 6 char
							str = auth.key.substring(6); break;
        			}
        			
        			var viewAuth = $scope.userGroup.authorityNames.indexOf("VIEW_"+str);
        			if (viewAuth < 0 && str && str !== ''){
        				$scope.userGroup.authorityNames.push("VIEW_"+str);
        			}
					
					var manageAuth = $scope.userGroup.authorityNames.indexOf("MANAGE_"+str);
        			if (manageAuth < 0 && str && str !== ''){
        				$scope.userGroup.authorityNames.push("MANAGE_"+str);
        			}
        		}
        	}
        	
        	
        	
        }
    },

    systemCodesController : function($scope) {

    },

    divisionController : function($scope, $log, $routeParams, divisionService) {
        $scope.init = function () {
            $scope.modal = {};
            $scope.modal.divisionForm = false;
            $scope.showAdvancedSearchGuide = false
            $scope.showAdvSearchSection = false;
            $scope.searchType = "";
            $scope.searchForm = {};
            $scope.search = {
            	keyword: "",
            	advanceSearchCriteria:{},
            	page: 0,
            	size: 20
            };
            
            divisionService.loadList({}, 1).then(function(divisions){
    			$scope.searchResults = divisions;
         		$scope.searchResults.type = 'standard';
             });
        }
        
        // Keyword search, check if keyword term is empty or not
        // If empty then do a normal search, if not then do keyword search
        $scope.keywordSearch = function() {
        	$scope.search.page = 0;
        	$scope.search.size = 20;
        	$scope.search.advanceSearchCriteria = {};
        	
        	if (/^\s*$/.test($scope.search.keyword)) {
        		divisionService.loadList({}, 1).then(function(divisions){
	    			$scope.searchResults = divisions;
	         		$scope.searchResults.type = 'standard';
	            });
        	} else {
        		divisionService.keywordSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        		$scope.searchType = "keyword";
        	}
        	$scope.showAdvancedSearchGuide=false;
        }
        
        // Advanced search, check if advanced search criteria is empty or not
        // If empty then query all, if not then do advanced search
        $scope.advancedSearch = function() {
        	$scope.search.page = 0;
        	$scope.search.size = 20;
        	$scope.search.keyword = "";
        	var keyCtr = 0;
        	
        	Object.keys($scope.search.advanceSearchCriteria).forEach(function(key){
        		if (/^\s*$/.test($scope.search.advanceSearchCriteria[key])) {
        			keyCtr++;
        		}
        	});
        	if (keyCtr === Object.keys($scope.search.advanceSearchCriteria).length) {
        		$scope.search.advanceSearchCriteria = {};
        	}
        	
        	if (Object.keys($scope.search.advanceSearchCriteria).length === 0) {
        		 divisionService.loadList({}, 1).then(function(divisions){
        			$scope.searchResults = divisions;
             		$scope.searchResults.type = 'standard';
                 });
        	} else {
        		divisionService.advancedSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            		$scope.searchType = "advanced";
            	});
        	}
        	$scope.showAdvancedSearchGuide=true;
        	$scope.showAdvSearchSection = false;
        }
        
        $scope.clearAdvancedSearchForm = function() {
        	$scope.search.advanceSearchCriteria = {};
        }


        $scope.initForm = function(){
            $scope.division = {};
        }

        $scope.loadDivision = function () {
            $scope.isEdit = $routeParams.edit;
            var divisionId = $routeParams.id;

            divisionService.load(divisionId)
                .then(function(division){
                $scope.division = division;
            });
        }
        
        $scope.saveDivision = function(){
            divisionService.save($scope.division)
                .then(function(division){
                    if(!$scope.division.nosqlId){
                        if(!$scope.modal.createNew){
                            $scope.changeLocation("/divisions/" + division.nosqlId);
                            $scope.modal.divisionForm = false;
                        }
                        $scope.division = {};
                    }
                }, function(error){
                    $scope.errorMessages = [error];
                });
        }

        // Load all divisions depending on the page selected
        $scope.loadDivisions = function(page){
            divisionService.loadList({}, page).then(function(divisions){
                $scope.searchResults = divisions;
                $scope.searchResults.type = 'standard';
            });
        }
        
        // Load all divisions depending on the search criteria and page selected
        $scope.loadDivisionsSearch = function(page){
        	$scope.search.page = page - 1;
        	if ($scope.searchType === 'keyword') {
        		divisionService.keywordSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        	} else {
        		divisionService.advancedSearch($scope.search).then(function (data) {
            		$scope.searchResults = data;
            		$scope.searchResults.type = 'search';
            	});
        	}
        }

        $scope.getDivisionList = function(name){
            if(name && name.length > 0){
                return divisionService.loadListByName(name);
            }
        }

        $scope.onDivisionSelect = function (item) {
            $scope.division.parent = item;
        };
    },

}