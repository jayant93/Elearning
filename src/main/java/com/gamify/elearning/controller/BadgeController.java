package com.gamify.elearning.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.gamify.elearning.repository.projection.BadgeProjection;
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
@RequestMapping("/api/badge")
public class BadgeController extends BaseRestController<Badge> {

	@Value("${upload.path}")
	private String uploadPath;
	
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
	
	@Autowired
	private ElearningFileUploadService elearningFileUploadService;

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getBadges() {
		List<Badge> badges = (List<Badge>) badgeRepository.findAll();
		List<BadgeProjection> projected = new ArrayList<>();
		for (Badge badge: badges) {
			projected.add(projectionFactory.createProjection(BadgeProjection.class, badge));
		}
		return ResponseEntity.ok(projected);
	}

	@GetMapping("/findAll/count")
	public @ResponseBody ResponseEntity<?> getAllBadgesCount() {
		return ResponseEntity.ok(badgeRepository.countAll());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getBadgeById(@PathVariable String id) {
		return ResponseEntity.ok(projectionFactory.createProjection(BadgeProjection.class, badgeRepository.findOne(id)));
	}

	@PostMapping("/add")
	public @ResponseBody ResponseEntity<?> addCourse(@RequestParam("multipartFile") MultipartFile multipartFile, @RequestParam("title") String title,
			HttpServletRequest request) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = eLearningUserRepository.findOne(userId);
		
		Badge success = null;
		Badge badge = new Badge();
		badge.setTitle(title);
		
		File file = null;
		try {
			System.out.println("Convert file");
			file = convert(multipartFile);
	    	String url = elearningFileUploadService.upload("badge/initial/" + file.getName(), file);
	        badge.setImageUrl(url);
	        success = badgeRepository.save(badge);
	        if(success != null) {
	        	return ResponseEntity.ok(projectionFactory.createProjection(BadgeProjection.class, success));
	        }
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Could not change save badge.");
		} finally {
			file.delete();
		}

		return new ResponseEntity<String>("Could not save badge.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(uploadPath + "/" + file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	@PostMapping("/reward/badge/{courseId}")
	public @ResponseBody ResponseEntity<?> giveBadge(@PathVariable String courseId) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = eLearningUserRepository.findOne(userId);
		Course course = courseRepository.findOne(courseId);

		List<UserBadge> userBadges = userBadgeRepository.getByCourseAndUser(userId, courseId);
		if (userBadges.size() == 0 && course.getBadges() != null && course.getBadges().size() > 0) {
			UserBadge userBadge = new UserBadge();
			userBadge.setCourse(course);
			userBadge.setUser(user);
			userBadge.setDateObtained(new Date());
			userBadge.setBadge((Badge)course.getBadges().toArray()[0]);
			UserBadge newBadge = userBadgeRepository.save(userBadge);
			if (newBadge != null) {
				return ResponseEntity.ok("Successfully rewarded badge to user.");
			}
		}
		else {
			return ResponseEntity.ok("User already has a badge for this course or course doesn't any badge");
		}

		return new ResponseEntity<String>("Could not reward badge to user.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/user/{id}")
	public @ResponseBody ResponseEntity<?> getBadgesByUser(@PathVariable String id) {
		List<Badge> badges = userBadgeRepository.getBadgesByUser(id);
		List<BadgeProjection> projected = new ArrayList<>();
		for (Badge badge: badges) {
			projected.add(projectionFactory.createProjection(BadgeProjection.class, badge));
		}
		return ResponseEntity.ok(projected);
	}

}
