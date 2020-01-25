package com.ideyatech.opentides.um.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gamify.elearning.dto.UserGroupAuthsDTO;
import com.gamify.elearning.dto.UserGroupDTO;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.entity.UserAuthority;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.UserAuthorityRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;

/**
 * Created by Gino on 8/25/2016.
 */
@BasePathAwareController
@RequestMapping("/api/usergroup")
public class UserGroupRestController extends BaseRestController<UserGroup> {

	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private UserAuthorityRepository<String> userAuthorityRepository;
    
    @Transactional
    @RequestMapping(value = "/usergroup-search/{type}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> divisionSearch(@RequestParam("page")int page, @RequestParam("size")int size,
    							@RequestBody HashMap<String, Object> body, @PathVariable String type) {
    	long startTime = System.currentTimeMillis();
    	Pageable pageable = new PageRequest(page, size);
    	Page<Division> usergroups = userGroupRepository.search(type, body, pageable);
    	long endTime   = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	result.put("results", usergroups);
    	result.put("searchTime", totalTime);
    	return ResponseEntity.ok(result);
    }
    
    @Transactional
    @RequestMapping(value = "/usergroup-create", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> createUserGroup(@RequestBody UserGroupDTO userGroupDto){
    	UserGroup userGroup = new UserGroup();
    	userGroup.setName(userGroupDto.getName());
    	userGroup.setDescription(userGroupDto.getDescription());
    	userGroup.setKey(userGroupDto.getKey());
    	userGroupRepository.save(userGroup);
    	return ResponseEntity.ok("Saved user Group");
    	
    }
    
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> findUserGroup(@PathVariable("id")String id){
    	UserGroup userGroup = (UserGroup) userGroupRepository.findOne(id);
    	if(userGroup == null){
    		return ResponseEntity.ok("Not Found");
    	}
    	return ResponseEntity.ok(userGroup);
    }
    
    @Transactional
    @RequestMapping(value = "/usergroup-update", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> updateUserGroup(@RequestBody UserGroupDTO userGroupDto){
    	UserGroup userGroup = (UserGroup) userGroupRepository.findOne(userGroupDto.getId());
    	userGroup.setName(userGroupDto.getName());
    	userGroup.setDescription(userGroupDto.getDescription());
    	userGroup.setKey(userGroupDto.getKey());
    	userGroupRepository.save(userGroup);
    	return ResponseEntity.ok("Saved user Group");
    	
    }
    
    @RequestMapping(value = "/usergroup-auths", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> updateUserGroupAuths(@RequestBody UserGroupAuthsDTO userGroupAuthsDTO){
    	UserGroup userGroup = (UserGroup) userGroupRepository.findOne(userGroupAuthsDTO.getId());
    	List<String> toDeleteIds = new ArrayList<>();
    	List<String> auths = userGroup.getAuthorityNames();
    	for (String auth : userGroupAuthsDTO.getAuths()) {
    		if(!userGroup.getAuthorityNames().contains(auth)){
    			userGroup.addAuthority(new UserAuthority(userGroup, auth));
    		}
		}
		Iterator authIterator = userGroup.getAuthorities().iterator();

    	while(authIterator.hasNext()){
    		UserAuthority ua = (UserAuthority) authIterator.next();
			if(!userGroupAuthsDTO.getAuths().contains(ua.getAuthority())){
				try {
					authIterator.remove();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				userAuthorityRepository.delete(ua.getId());
			}
    	}
    	userGroupRepository.save(userGroup);
    	
    	return ResponseEntity.ok("Saved user Group");
    	
    }
}