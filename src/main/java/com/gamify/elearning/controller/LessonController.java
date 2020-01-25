package com.gamify.elearning.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gamify.elearning.repository.*;
import com.gamify.elearning.repository.projection.LessonProjection;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gamify.elearning.dto.CourseDTO;
import com.gamify.elearning.dto.ElementDTO;
import com.gamify.elearning.dto.LessonDTO;
import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.Lesson;
import com.ideyatech.opentides.core.entity.MessageResponse;
import com.ideyatech.opentides.core.util.CrudUtil;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.util.SecurityUtil;
import com.ideyatech.opentides.um.validator.CourseValidator;

/**
 * 
 * @author johanna@ideyatech.com
 *
 */
@BasePathAwareController
//@RestController
@RequestMapping("/api/lesson")
public class LessonController extends BaseRestController<Lesson> {

	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ElementRepository elementRepository;
	
	@Autowired
	private ELearningUserRepository elearningUserRepository;

	@Autowired
	CourseValidator courseValidator;

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourses() {
		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
			return ResponseEntity.ok(lessonRepository.findAll());
		else {
			// return only list of user's division
			List<Lesson> result = (List<Lesson>) lessonRepository.findAll();
			List<LessonProjection> projected = new ArrayList<>();
            for (Lesson lesson : result) {
                projected.add(projectionFactory.createProjection(LessonProjection.class, lesson));
            }
			return ResponseEntity.ok(result);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getLessonById(@PathVariable String id) {
		return ResponseEntity.ok(lessonRepository.findOne(id));
	}
	
	@RequestMapping(value = "/{id}/elements", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getElementsOfLesson(@PathVariable String id) {
		List<Element> elements = elementRepository.findElementsByLesson(id);
		return ResponseEntity.ok(elements);
	}

	@PostMapping("add")
	public @ResponseBody ResponseEntity<?> addLesson(@RequestBody Lesson lesson, BindingResult bindingResult,
			HttpServletRequest request) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);
		Lesson success = null;
		if(lesson.getId() == null || lesson.getId().isEmpty()) {
			Course course = courseRepository.findOne(lesson.getCourseId());
			if(course == null) {
				return ResponseEntity.badRequest().body("This lesson does not seem to be belonging to a course.");
			}
			if (bindingResult.hasErrors()) {
				List<MessageResponse> messageResponses = CrudUtil.convertErrorMessage(bindingResult, request.getLocale(),
						messageSource);
				return ResponseEntity.badRequest().body(messageResponses);
			}
			lesson.setCourse(course);
			lesson.setUser(user);
			course.setNumOfLessons(course.getNumOfLessons() + 1);
			lesson.setOrdinal(course.getNumOfLessons());
			lesson.setNumOfElements(0);
			lesson.setNumOfQuizzes(0);
			lesson.setNumOfVideos(0);
			success = lessonRepository.save(lesson);
			courseRepository.save(course);
			if (success != null) {
				return ResponseEntity.ok(projectionFactory.createProjection(LessonProjection.class, success));
			}
		} else {
			Lesson l = lessonRepository.findOne(lesson.getId());
			if(l != null) {
				l.setTitle(lesson.getTitle());
				l.setDescription(lesson.getDescription());
				l.setTags(lesson.getTags());
				success = lessonRepository.save(l);
				if(success != null) {
					return ResponseEntity.ok(projectionFactory.createProjection(LessonProjection.class, success));
				}
			}
		}
		return new ResponseEntity<String>("Could not save lesson.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/{id}/delete")
	public @ResponseBody ResponseEntity<?> deleteLesson(@PathVariable String id, HttpServletRequest httpRequest) {
		Lesson lesson = lessonRepository.findOne(id);
		if (lesson == null) {
			return new ResponseEntity<String>("The lesson you are trying to delete does not exist.",
					HttpStatus.BAD_REQUEST);
		}
		// return ResponseEntity.ok("Successfully deleted lesson.");
		Course course = lesson.getCourse();
		int oldCount = course.getNumOfLessons();
		course.setNumOfLessons(oldCount-1);
		course.getLessons().remove(lesson);
		List<Lesson> toSave = new ArrayList<>();
		for(int i = 0; i < course.getLessons().size(); i++) {
			Lesson theLesson = course.getLessons().get(i);
			lesson.setOrdinal(i+1);
			toSave.add(theLesson);
        }
		lessonRepository.save(toSave);
		courseRepository.save(course);
		lesson.setDeleted(true);
		lessonRepository.save(lesson);
		return ResponseEntity.ok("Successfully deleted lesson.");
	}
	
	@PostMapping("/elements/ordering")
	public @ResponseBody ResponseEntity<?> updateElementOrdering(@RequestBody CourseDTO courseDto, HttpServletRequest httpRequest) {
		for(LessonDTO lessonDto: courseDto.getLessons()) {
			for(ElementDTO elementDto: lessonDto.getElements()) {
				Element element = elementRepository.findOne(elementDto.getId());
				if(element == null) continue;
				else {
					element.setOrdinal(elementDto.getOrdinal());
					elementRepository.save(element);
				}
			}
			Lesson lesson = lessonRepository.findOne(lessonDto.getId());
			if(lesson != null) {
				lesson.setOrdinal(lessonDto.getOrdinal());
				lesson.setNumOfElements(lessonDto.getNumOfElements());
				lessonRepository.save(lesson);
			}
		}
		return ResponseEntity.ok("Successfully saved lesson ordering.");

	}
	
//	@GetMapping("/{id}/total-watch-time")
//	public @ResponseBody ResponseEntity<?> getTotalWatchTime(@PathVariable("id") String id, HttpServletRequest httpRequest) {
//		Lesson lesson = lessonRepository.findOne(id);
//		if(lesson == null) {
//			return new ResponseEntity<String>("Could not find specified lesson.", HttpStatus.BAD_REQUEST);
//		}
//		List<Video> videos = videoRepository.findVideosByLesson(id);
//		PeriodFormatter hoursMinutesSecondsFormatter =
//			     new PeriodFormatterBuilder().appendHours().appendSeparator(":")
//			            .appendMinutes().appendSeparator(":").appendSeconds().toFormatter();
//		Period total = hoursMinutesSecondsFormatter.parseMutablePeriod("00:00:00").toPeriod();
//		String totalLength = "";
//		for(Video v: videos) {
//			if(v.getDuration() != null && !v.getDuration().isEmpty()) {
//				Period temp = hoursMinutesSecondsFormatter.parseMutablePeriod(v.getDuration()).toPeriod();
//				total = total.plus(temp);
//				totalLength = total.toString(hoursMinutesSecondsFormatter);
//			}
//		}
//		System.out.println(totalLength);
//		return ResponseEntity.ok(totalLength);
//	}

}
