package com.ideyatech.opentides.um.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.dto.Claim;
import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.repository.AuthorityRepository;
import com.ideyatech.opentides.um.util.BeanUtil;

/**
 *
 * @author Gino
 */
@BasePathAwareController
@RequestMapping(value = "/api/authority")
public class AuthorityRestController extends BaseRestController<Authority> {

	@Autowired
	private AuthorityRepository authorityRepository;

	@RequestMapping(value = "/updateAll", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<?> saveAuthorities(@RequestBody Authority.Authorities authorities) {
		Iterable<Authority> auths = authorityRepository.findAll();
		authorityRepository.delete(auths);
		for (Authority authority : authorities) {
			authorityRepository.save(authority);
		}
		return ResponseEntity.ok(authorities);
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAuthorities() {
        return ResponseEntity.ok(authorityRepository.findAll());
    }

	@RequestMapping(value = "/findAllInTree", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAuthoritiesInTree() {

		List<Authority> roots = this.authorityRepository.findRootAuthz();
		List<Claim> claims = new LinkedList<>();
		for (Authority authz : roots) {
			Claim claim = new Claim();
			BeanUtil.copyProperties(authz, claim);
			populateChildren(claims, claim);
		}

		return ResponseEntity.ok(claims);
	}

	private void populateChildren(List<Claim> claims, Claim claim) {
		List<Authority> childrenAuthz = this.authorityRepository.findChildAuthz(claim.getKey());
		List<Claim> children = BeanUtil.copyPropertiesToList(Claim.class, childrenAuthz);
		
		boolean isNotRoot = (claim.getParent() == null);
		boolean hasChild = (children != null && !children.isEmpty());
		
		if (hasChild) {
			for (Claim child : children) {
				populateChildren(claims, child);
			}
			claim.setChildren(children);
		}
		
		if (isNotRoot) {
			claims.add(claim);	
		} 
		
	}

	@GetMapping(value="create-authz")
	public @ResponseBody ResponseEntity<?> createAuthorities() {
		String permissions[][] = {
			{"04.00.00.00", "MANAGE_COURSE", "Manage Course"},
				{"04.01.00.00", "CREATE_COURSE", "Create Course"},
				{"04.02.00.00", "EDIT_COURSE", "Edit Course"},
    			{"04.03.00.00", "DELETE_COURSE", "Delete Course"},
    			{"04.04.00.00", "VIEW_COURSE", "View Course"},
    			{"04.04.00.00", "ARRANGE_ELEMENTS", "Arrange Elements"},
    			{"04.05.00.00", "TAKE_COURSE", "Take Course"},
    			{"04.06.00.00", "VIEW_COURSE_RESULTS", "View Course Results"},

			{"05.00.00.00", "MANAGE_QUIZ", "Manage Quiz"},
    			{"05.01.00.00", "CREATE_QUIZ", "Create Quiz"},
    			{"05.02.00.00", "EDIT_QUIZ", "Edit Quiz"},
    			{"05.03.00.00", "DELETE_QUIZ", "Delete Quiz"},
				{"05.04.00.00", "VIEW_QUIZ", "View Quiz"},
				{"05.05.00.00", "TAKE_QUIZ", "Take Quiz"},

			{"06.00.00.00", "MANAGE_VIDEO", "Manage Video"},
				{"06.01.00.00", "CREATE_VIDEO", "Create Video"},
				{"06.02.00.00", "EDIT_VIDEO", "Edit Video"},
				{"06.03.00.00", "DELETE_VIDEO", "Delete Video"},
				{"06.04.00.00", "WATCH_VIDEO", "Watch Video"},
				{"06.05.00.00", "VIEW_VIDEO", "View Video"},

			{"07.00.00.00", "MANAGE_LESSON", "Manage Lesson"},
				{"07.01.00.00", "CREATE_LESSON", "Create Lesson"},
				{"07.02.00.00", "EDIT_LESSON", "Edit Lesson"},
				{"07.03.00.00", "DELETE_LESSON", "Delete Lesson"},
				{"07.04.00.00", "VIEW_LESSON", "View Lesson"},

			{"08.00.00.00", "VIEW_HOME", "View Home"},
				{"08.01.00.00", "VIEW_CHARTS", "View Charts"},
				{"08.02.00.00", "VIEW_DATA_STATS", "View Data Stats"},
				{"08.03.00.00", "VIEW_TEACHER_STATS", "View Teacher Stats"},
				{"08.04.00.00", "VIEW_UNFINISHED_COURSES", "View Unfinished Courses"},
				{"08.05.00.00", "REGULAR_HOME_COURSE_VIEW", "Regular Home Course View"},
				{"08.06.00.00", "CLIENT_HOME_COURSE_VIEW", "Client Home Course View"},

			{"09.00.00.00", "VIEW_COURSE_FILTERS", "View Course Filters"},
				{"09.01.00.00", "HOME_VIEW_RECENT_COURSES", "View Recent Courses (Home)"},
				{"09.02.00.00", "HOME_VIEW_POPULAR_COURSES", "View Popular Courses (Home)"},
				{"09.03.00.00", "HOME_VIEW_STARTED_COURSES", "View Started Courses (Home)"},
				{"09.04.00.00", "MYCOURSES_VIEW_STARTED_COURSES", "View Started Courses (My Courses)"},
				{"09.05.00.00", "MYCOURSES_VIEW_CREATED_COURSES", "View Created Courses (My Courses)"},
				{"09.06.00.00", "MYCOURSES_VIEW_POPULAR_COURSES", "View Popular Courses (My Courses)"}
		};

		for (int i = 0; i < permissions.length; i++) {
			String level = permissions[i][0];
			String key = permissions[i][1];
			String title = permissions[i][2];
			Authority auth = authorityRepository.findByKey(key);
			if (auth == null) {
				Authority newAuth = new Authority();
				newAuth.setKey(key);
				newAuth.setLevel(level);
				newAuth.setTitle(title);
				authorityRepository.save(newAuth);
			}
		}

		return ResponseEntity.ok("OK");
	}
}
