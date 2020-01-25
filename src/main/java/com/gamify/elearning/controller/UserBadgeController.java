package com.gamify.elearning.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamify.elearning.entity.Badge;
import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.UserBadge;
import com.gamify.elearning.repository.BadgeRepository;
import com.gamify.elearning.repository.CourseRepository;
import com.gamify.elearning.repository.ELearningUserRepository;
import com.gamify.elearning.repository.UserBadgeRepository;
import com.gamify.elearning.service.ElearningFileUploadService;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.util.SecurityUtil;

/**
 * 
 * @author johanna@ideyatech.com
 *
 */
@BasePathAwareController
//@RestController
@RequestMapping("/api/achievements")
public class UserBadgeController extends BaseRestController<Badge> {
	
	@Autowired
	private BadgeRepository badgeRepository;

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ELearningUserRepository eLearningUserRepository;

	@Autowired
	private UserBadgeRepository userBadgeRepository;

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getBadges() {
		List<Badge> result = (List<Badge>) badgeRepository.findAll();
		return ResponseEntity.ok(result);
	}

	@GetMapping("/findAll/count")
	public @ResponseBody ResponseEntity<?> getAllBadgesCount() {
		return ResponseEntity.ok(badgeRepository.countAll());
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getBadgesByUser(@PathVariable String id) {
		if(id.isEmpty()) {
			id = SecurityUtil.getJwtSessionUser().getId();
		}
		System.out.println(id);
		List<Badge> badges = userBadgeRepository.getBadgesByUser(id);
		return ResponseEntity.ok(badges);
	}
}
