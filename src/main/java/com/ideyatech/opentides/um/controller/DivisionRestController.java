package com.ideyatech.opentides.um.controller;

import java.util.HashMap;
import java.util.Map;

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

import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.repository.DivisionRepository;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.service.UserService;
import com.ideyatech.opentides.um.util.SecurityUtil;

/**
 * @author jpereira on 3/20/2017.
 */
@BasePathAwareController
@RequestMapping("/api/division")
public class DivisionRestController extends BaseRestController<Division> {

    @Autowired
    private UserRepository userRepository;
	
    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getDivisions() {
    		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
    			return ResponseEntity.ok(divisionRepository.findAll());
    		else {
    			// return only list of user's division
        		Map<String, Object> result = userService.getUserAccess(SecurityUtil.getJwtSessionUser().getUsername());
    			return ResponseEntity.ok(result.get("ORGUNITS"));
        	}    			
    }

    @RequestMapping(value = "/findByKey/{key}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getDivisionByKey(@PathVariable String key) {
        return ResponseEntity.ok(divisionRepository.findByKey(key));
    }

    /** Getting all the divisions for the report references **/
    @RequestMapping(value = "/findDivisionLocations", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getReportDivisionLocations() {
        Map<String, Object> result = new HashMap<>();
        for (Object obj : divisionRepository.findAll()) {
            Division division = (Division) obj;
            result.put(division.getKey(), division.getName());
        }

        return ResponseEntity.ok(result);
    }
    
    @Transactional
    @RequestMapping(value = "/division-search/{type}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> divisionSearch(@RequestParam("page")int page, @RequestParam("size")int size,
    							@RequestBody HashMap<String, Object> body, @PathVariable String type) {
    	long startTime = System.currentTimeMillis();
    	Pageable pageable = new PageRequest(page, size);
    	Page<Division> divisions = divisionRepository.search(type, body, pageable);
    	long endTime   = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	result.put("results", divisions);
    	result.put("searchTime", totalTime);
    	return ResponseEntity.ok(result);
    }
}

