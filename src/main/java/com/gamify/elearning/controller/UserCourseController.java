package com.gamify.elearning.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.Lesson;
import com.gamify.elearning.entity.UserCourse;
import com.gamify.elearning.entity.Video;
import com.gamify.elearning.repository.CourseProgressRepository;
import com.gamify.elearning.repository.CourseRepository;
import com.gamify.elearning.repository.ELearningUserRepository;
import com.gamify.elearning.repository.UserCourseRepository;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@BasePathAwareController
//@RestController
@RequestMapping("/api/usercourse")
public class UserCourseController extends BaseRestController<UserCourse> {
    
    @Autowired
	private UserCourseRepository<String> userCourseRepository;

    @Autowired
	private CourseRepository courseRepository;

    @Autowired
	private ELearningUserRepository elearningUserRepository;

	@Autowired
	private CourseProgressRepository courseProgressRepository;

    @PostMapping("/{id}/take")
    public @ResponseBody ResponseEntity<?> takeCourse(@PathVariable String id, HttpServletRequest httpRequest) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);

		Course course = courseRepository.findOne(id);
		if (course == null) {
			return new ResponseEntity<String>("The course you are trying to take does not exist.",
					HttpStatus.BAD_REQUEST);
		}

		if (course.getUser().getId().equals(userId)) {
			return ResponseEntity.ok("You cannot take your own course.");
		}

		// Allow only 1 enrolment to the course
		List<UserCourse> userCourses = userCourseRepository.getUserCourseByUserAndCourse(userId, course.getId());
		if (userCourses.size() < 1) {
			UserCourse userCourse = new UserCourse();
			userCourse.setUser(user);
			userCourse.setCourse(course);
			userCourse.setDateTaken(new Date());
			UserCourse result = null;
			result = userCourseRepository.save(userCourse);
			if (result != null) {
				return ResponseEntity.ok("Successfully created user course relationship.");
			}
		} else {
            return ResponseEntity.ok("Resuming user course.");
		}

		return new ResponseEntity<String>("Could not take course.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/{id}/check-taken")
	public @ResponseBody ResponseEntity<?> checkIfCourseTaken(@PathVariable String id) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		List<UserCourse> enrolment = userCourseRepository.getUserCourseByUserAndCourse(userId, id);
		return ResponseEntity.ok( enrolment.size() > 0 );
	}
    
    // @GetMapping("/coursestaken")
    // public @ResponseBody ResponseEntity<?> getCoursesOfUser() {
    //     String userId = SecurityUtil.getJwtSessionUser().getId();
    //     List<UserCourse> userCourses = userCourseRepository.findByUser(userId);
	// 	return ResponseEntity.ok(userCourses);
	// }

	@GetMapping("/coursetakers/count")
    public @ResponseBody ResponseEntity<?> getCourseTakersCount() {
        String userId = SecurityUtil.getJwtSessionUser().getId();
        int count = userCourseRepository.getMyCourseTakersCount(userId);
		return ResponseEntity.ok(count);
	}

	@GetMapping("/unfinished/count")
    public @ResponseBody ResponseEntity<?> getUnfinishedCoursesCount() {
        String userId = SecurityUtil.getJwtSessionUser().getId();
		int count = courseProgressRepository.getUnfinishedCoursesCount(userId);
		return ResponseEntity.ok(count);
	}

	@GetMapping("/recent/takers")
	public @ResponseBody ResponseEntity<?> getRecentTakers() {
        String userId = SecurityUtil.getJwtSessionUser().getId();
		List<ELearningUser> recentTakers = userCourseRepository.getRecentTakersOfMyCourses(userId, new PageRequest(0, 5));
		
		List<Map<String, Object>> takersList = new ArrayList<>();
		for (ELearningUser user: recentTakers) {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("id", user.getId());
			userMap.put("completeName", user.getShortenedName());
			userMap.put("profilePhotoUrl", user.getProfilePhotoUrl());
			takersList.add(userMap);
		}

		return ResponseEntity.ok(takersList);
	}

	@GetMapping("/coursestaken/{page}/{size}")
	public @ResponseBody ResponseEntity<?> getCoursesOfUserByPage(@PathVariable Integer page, @PathVariable Integer size) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
		List<UserCourse> userCourses = userCourseRepository.findByUser(userId, new PageRequest(page, size));
		
		List<Map<String, Object>> coursesList = new ArrayList<>();
		for (UserCourse userCourse: userCourses) {
			Map<String, Object> courseMap = new HashMap<>();
			Course course = userCourse.getCourse();
			courseMap.put("id", course.getId());
			courseMap.put("title", course.getTitle());
			courseMap.put("description", course.getDescription());
			courseMap.put("lessonCount", course.getNumOfLessons());

			Integer completionTime = 0;
			for (Lesson lesson: course.getLessons()) {
				for (Element elem: lesson.getElements()) {
					if (elem instanceof Video) {
						Integer duration = ((Video) elem).getDurationInSec();
						if (duration != null) {
							completionTime += duration;
						}
					}
				}
			}
			courseMap.put("completionTime", completionTime);

			coursesList.add(courseMap);
		}

		return ResponseEntity.ok(coursesList);
	}

	@GetMapping("/coursestaken/count")
	public @ResponseBody ResponseEntity<?> getTakenCoursesCount() {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		int count = userCourseRepository.countTakenCourses(userId);
		return ResponseEntity.ok(count);
	}
}