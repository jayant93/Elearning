/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {

    userService : function ($q, $filter, Notification, baseService) {
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            initUser : function(){
                return {
                    credential : {
                        enabled : true
                    },
                    groups : [],
                    divisions : [],
                    _groups : [],
                    _addedGroups : [],
                    _removedGroups: [],
                    _userGroup: {}
                }
            },

            keywordSearch: function(searchData) {
            	var deferred = $q.defer();

            	var keyword = searchData.keyword;
            	var pagination = 'page=' + searchData.page + '&size=' + searchData.size;
            	
                baseService.post('/user/user-search/keyword?' + pagination,
                    {
                        data: {
                        	keyword : keyword
                        }
                    }
                ).then(function (result) {
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },
            
            advancedSearch: function(searchData) {
            	var deferred = $q.defer();

            	var advancedSearch = searchData.advanceSearchCriteria;
            	var pagination = 'page=' + searchData.page + '&size=' + searchData.size;
            	
                baseService.post('/user/user-search/advanced?' + pagination,
                    {
                        data: {
                        	criteria: advancedSearch
                        }
                    }
                ).then(function (result) {
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },
            
            save: function(user){
                var deferred = $q.defer();

                //TODO get password rules and implement validation
                baseService.post('/user/register',
                    {
                        data: user,
                        hideErrorNotif : true
                    }
                ).then(function (result) {
                    //Notify
                    Notification.success($filter('translate')('USER_SAVED'));

                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },

            update: function(user){
                var deferred = $q.defer();
                var updateGroups;
                var updateDivisions;

                if(user._groups.length <= 0){
                    deferred.reject(
                        { data:
                            {
                                message: $filter('translate')('REQUIRED_FIELD', {field: 'user group'}),
                                arguments: [ "Groups" ]
                            }
                        });
                }

                //Save usergroup if with added or remove user groups
                if(user._addedGroups.length > 0 || user._removedGroups.length > 0){
                    updateGroups = baseService.post('/user/updateUserGroup/' + user.nosqlId,
                        { params :
                            {
                                addedGroups : user._addedGroups,
                                removedGroups : user._removedGroups
                            },
                            hideErrorNotif : true
                        });
                }

                if(user.divisions){
                    updateDivisions = baseService.post('/user/updateUserDivisions/' + user.nosqlId,
                        {
                            data :
                                {
                                    divisions: user.divisions,
                                    homeDepartment: user.homeDepartment,
                                    homeSection: user.homeSection
                                },
                            hideErrorNotif : true
                        });
                }

                $q.all([updateGroups, updateDivisions])
                    .then(function(){
                        //Remove after save
                        user._addedGroups = [];
                        user._removedGroups = [];

                        // Set enabled based on user set status
                        if (user.credential.status == 'Active') {
                            user.credential.enabled = true;
                        } else {
                            user.credential.enabled = false;
                        }

                        // Arrange data for submission
                        var data = {
                            firstName : user.firstName,
                            lastName: user.lastName,
                            middleName: user.middleName,
                            emailAddress: user.emailAddress,
							enabled : user.credential.enabled,
                            status: user.credential.status,
                            tempPassword: user.credential.tempPassword
                        };

                        baseService.post('/user/updateBySelectedProperty/' + user.nosqlId, { data : data, hideErrorNotif : true })
                            .then(function (result) {
                                //Notify
                                Notification.success($filter('translate')('USER_UPDATED'));
                                deferred.resolve(result.data);
                            }, function(error){
                                Notification.error(error.data.message);
								deferred.reject(error);
						});
                    }, function(error){
                        deferred.reject(error);
                    });
                
                return deferred.promise;
            },

            loadUsers : function(searchForm, page){
                searchForm.p = page || 1;
                searchForm.orderOption = 'createDate';
                searchForm.orderFlow = 'desc';

                return baseService.findByExample('user', searchForm)
            },

            loadUser : function(id){
                var deferred = $q.defer();

                baseService.getResource('user/' + id).$get().then(function (result) {
                    //Split groups
                    result._groups = result
                        .displayGroups.replace(/^\s+|\s+$/g,"").split(/\s*,\s*/);

                    result.groups = result.groups || [];
                    result.divisions = result.divisions || [];

                    result._addedGroups = [];
                    result._removedGroups = [];
                    
                    deferred.resolve(result);
                });

                return deferred.promise;
            }
        });
    },

    authorityService: function($q, baseService){
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            getAll : function(){
                var deferred = $q.defer();

                baseService.findAll('/authority')
                    .then(function (result) {
                        deferred.resolve(result);
                    });

                return deferred.promise;
				
            }, getAllInTree : function(){
				var url = '/authority/findAllInTree';
                var deferred = $q.defer();
				
				baseService.get(url, { cache: true }).then(function (result) {
                    deferred.resolve(result);
                });
				                
				return deferred.promise;
            }
        });
    },

    userGroupService: function($q, $filter, Notification, baseService){
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            loadUserGroups : function(searchForm, page){
                searchForm.p = page || 1;
                searchForm.orderOption = 'createDate';
                searchForm.orderFlow = 'desc';

                return baseService.findByExample('usergroup', searchForm)
            },
            
            keywordSearch: function(searchData) {
            	var deferred = $q.defer();

            	var keyword = searchData.keyword;
            	var pagination = 'page=' + searchData.page + '&size=' + searchData.size;
            	
                baseService.post('/usergroup/usergroup-search/keyword?' + pagination,
                    {
                        data: {
                        	keyword : keyword
                        }
                    }
                ).then(function (result) {
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },
            
            advancedSearch: function(searchData) {
            	var deferred = $q.defer();

            	var advancedSearch = searchData.advanceSearchCriteria;
            	var pagination = 'page=' + searchData.page + '&size=' + searchData.size;

                baseService.post('/usergroup/usergroup-search/advanced?' + pagination,
                    {
                        data: {
                        	criteria: advancedSearch
                        }
                    }
                ).then(function (result) {
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },

            loadUserGroup : function(id){
                var deferred = $q.defer();

                baseService.getResource('/usergroup/' + id).$get().then(function (result) {
                    //Set new name
                    result.newName = result.name;

                    deferred.resolve(result);
                });

                return deferred.promise;
            },
            
            loadUserGroupByUser : function(id){
                return baseService.get("/user/" + id + "/groups");
            },

            saveUserGroup : function(userGroup){
                var deferred = $q.defer();

                var save = "";
                if(userGroup.nosqlId){
                    save = baseService.getResource('/usergroup/' + userGroup.nosqlId).$put();
                }else{
                	userGroup.name = userGroup.newName;
                    save = baseService.getResource('/usergroup').$post(userGroup);
                }

                save.then(function (result) {
                    //Notify
                    Notification.success($filter('translate')('USER_GROUP_SAVED'));

                    deferred.resolve(result.data);
                });

                return deferred.promise;
            },
        });

    },

    systemCodeService: function ($q, $httpParamSerializer, baseService) {
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            getByCategory: function (category) {
                var url = '/system-codes/search/findByCategory?category=' + category;
                var deferred = $q.defer();

                baseService.get(url, { cache: true }).then(function (result) {
                    deferred.resolve(result._embedded.systemCodeses);
                });

                return deferred.promise;
            },

            getByKey: function (key) {
                var url = '/system-codes/search/findByKey?key=' + key;
                var deferred = $q.defer();

                baseService.get(url, { cache: true }).then(function (systemCode) {
                    deferred.resolve(systemCode);
                });

                return deferred.promise;
            },

            saveSystemCode: function (systemCode) {
                var deferred = $q.defer();
                var key = systemCode.key;
                var url = '/system-codes/search/findByExample?key=' + key;

                baseService.getResource(url).$get().then(function (result) {
                    if (result.results.length > 0) {
                        deferred.reject("System Code already exists.");
                    }
                    else {
                        baseService.getResource('system-codes').$post(systemCode).then(function (result) {
                            deferred.resolve(result.data);
                        });
                    }
                });
                return deferred.promise;
            },

            searchSystemCode: function (searchForm) {
                var params = $httpParamSerializer(searchForm);

                return baseService.getResource('system-codes/search/findByExample?' + params).$get();
            },

            getSystemCode: function (id) {
                var deferred = $q.defer();

                baseService.getResource('system-codes/' + id).$get().then(function (result) {
                    var systemCode = {};
                    systemCode.nosqlId = result.nosqlId;
                    systemCode.category = result.category;
                    systemCode.value = result.value;
                    systemCode.key = result.key;
                    systemCode.numberValue = result.numberValue;
                    deferred.resolve(systemCode);
                });

                return deferred.promise;
            },

            updateSystemCode: function (systemCode) {
                var url = baseService.getBaseUrl() + '/system-codes/';
                return baseService.update(url, systemCode, systemCode.nosqlId);
            },

            deleteSystemCode: function (id) {
                var url = baseService.getBaseUrl() + '/system-codes/';
                return baseService.delete(url, id);
            },

            getAllCategories: function () {
                var url = '/system-codes/search/getAllCategories';
                var deferred = $q.defer();

                baseService.get(url, {cache: true}).then(function (systemCode) {
                    deferred.resolve(systemCode);
                });

                return deferred.promise;
            }
        });
    },

    divisionService: function($q, $filter, Notification, baseService){
        var extended = angular.extend({}, baseService);

        return angular.extend(extended, {
            loadAll : function(selectedDivisions){
                var deferred = $q.defer();

                selectedDivisions = selectedDivisions || [];

                //test
                baseService.findAll('/division')
                    .then(function(result){
                        var all = result;
                        var groupedBy = $filter('groupBy')(result, 'parent.key');
                        var parentList = [];

                        //Remove parents
                        angular.forEach(groupedBy,
                            function(value, key) {
                                if(key){
                                    //Remove parents to all
                                    var obj = $filter('filter')(result, { 'key' : key })[0];
                                    if(obj){
                                        parentList.push({ 'key' : key, 'value' : obj });
                                        all.splice(all.indexOf(obj), 1);
                                    }
                                }
                            });

                        //Final Groupings
                        var finalGroup = $filter('groupBy')(all, 'parent.key');

                        var groupList = [];
                        //For final group
                        angular.forEach(finalGroup,
                            function(value, key) {
                                //Add parent
                                var obj = $filter('filter')(parentList, { 'key' : key })[0];
                                if(!obj)
                                    obj = {};
                                else
                                    obj = obj.value;

                                obj.divisionGroup = true;
                                groupList.push(obj);

                                //Add contents
                                angular.forEach(value,
                                    function(v, k) {
                                        var division = selectedDivisions.filter(function(e) { return e.key === v.key; }).length;
                                        if(division > 0){
                                            v.ticked = true;
                                        }else{
                                            v.ticked = false;
                                        }

                                        groupList.push(v);
                                    });

                                //end group
                                 groupList.push({ divisionGroup: false });
                            });

                        deferred.resolve(groupList);
                    });

                return deferred.promise;
            },
            
            loadListByName : function(name){
                var deferred = $q.defer();

                baseService.get('/division/search/findByNameContaining?name=' + name)
                    .then(function(result){
                        deferred.resolve(result._embedded.divisions);
                    });

                return deferred.promise;
            },

            keywordSearch: function(searchData) {
            	var deferred = $q.defer();

            	var keyword = searchData.keyword;
            	var pagination = 'page=' + searchData.page + '&size=' + searchData.size;
            	
                baseService.post('/division/division-search/keyword?' + pagination,
                    {
                        data: {
                        	keyword : keyword
                        }
                    }
                ).then(function (result) {
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },
            
            advancedSearch: function(searchData) {
            	var deferred = $q.defer();

            	var advancedSearch = searchData.advanceSearchCriteria;
            	var pagination = 'page=' + searchData.page + '&size=' + searchData.size;
            	
                baseService.post('/division/division-search/advanced?' + pagination,
                    {
                        data: {
                        	criteria: advancedSearch
                        }
                    }
                ).then(function (result) {
                    deferred.resolve(result);
                }, function(reason){
                    deferred.reject(reason.data);
                });

                return deferred.promise;
            },
            
            loadList : function(searchForm, page){
                var deferred = $q.defer();

                searchForm.p = page || 1;
                searchForm.orderOption = 'name';
                searchForm.orderFlow = 'asc';

                baseService.findByExample('division', searchForm)
                    .then(function(result){
                        deferred.resolve(result);
                });

                return deferred.promise;
            },

            load : function(id){
                var deferred = $q.defer();

                baseService.getResource('/division/' + id).$get().then(function (result) {
                    //Set new name
                    result.newName = result.name;

                    deferred.resolve(result);
                });

                return deferred.promise;
            },

            save : function(division){
                var deferred = $q.defer();
                var message = division.nosqlId ?
                    $filter('translate')('DIVISION_UPDATED') :
                    $filter('translate')('DIVISION_SAVED');

               this.validate(division)
                   .then(function () {
                       var save = division.nosqlId ?
                           baseService.getResource('/division/' + division.nosqlId).$put() :
                           baseService.getResource('/division/').$post(division);
                       save
                           .then(function (result) {
                           //Notify
                           Notification.success(message);
                           deferred.resolve(result.data);
                       });
                    }, function (error) {
                       deferred.reject(error);
                    });

                return deferred.promise;
            },

            validate : function (division) {
                var deferred = $q.defer();

                //Remove parent if not valid
                if(division.parent && !division.parent.id){
                    division.parent = {};
                }

                //Validate Key
                baseService.findByExample('division', { "key" : division.key, "exactMatch" : true })
                    .then(function(result){
                        var divisions = result.results;
                        if(divisions.length > 1){
                            //Not Valid
                            deferred
                                .reject( { "message" : $filter('translate')('DIVISION_KEY_EXISTING') });
                            
                        }else if(divisions.length == 1){
                            //Not Valid
                            if(divisions[0].id != division.id){
                                deferred
                                    .reject( { "message" : $filter('translate')('DIVISION_KEY_EXISTING') });
                            }
                        }

                        deferred.resolve();
                    });
                
                return deferred.promise;
            }
        });

    },
}