package com.gamify.elearning.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import com.gamify.elearning.dto.CourseDTO;
import com.gamify.elearning.dto.CourseDTO2;
import com.gamify.elearning.entity.Badge;
import com.gamify.elearning.entity.Choice;
import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.CourseProgress;
import com.gamify.elearning.entity.Curriculum;
import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.ElementProgress;
import com.gamify.elearning.entity.Lesson;
import com.gamify.elearning.entity.PreviewVideo;
import com.gamify.elearning.entity.Question;
import com.gamify.elearning.entity.Quiz;
import com.gamify.elearning.entity.QuizResult;
import com.gamify.elearning.entity.Unit;
import com.gamify.elearning.entity.Video;
import com.gamify.elearning.entity.VideoProgress;
import com.gamify.elearning.repository.BadgeRepository;
import com.gamify.elearning.repository.CourseProgressRepository;
import com.gamify.elearning.repository.CourseRepository;
import com.gamify.elearning.repository.ELearningUserRepository;
import com.gamify.elearning.repository.ElementProgressRepository;
import com.gamify.elearning.repository.ElementRepository;
import com.gamify.elearning.repository.LessonRepository;
import com.gamify.elearning.repository.PreviewVideoRepository;
import com.gamify.elearning.repository.QuizResultRepository;
import com.gamify.elearning.repository.UserCourseRepository;
import com.gamify.elearning.repository.VideoProgressRepository;
import com.gamify.elearning.repository.VideoRepository;
import com.gamify.elearning.repository.jpa.CurriculumRepository;
import com.gamify.elearning.repository.projection.CourseProjection;
import com.gamify.elearning.repository.projection.CourseTitle;
import com.gamify.elearning.repository.projection.CourseunitProjection;
import com.gamify.elearning.repository.projection.LatestCourseProjection;
import com.gamify.elearning.repository.projection.SearchCourseProjection;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.repository.jpa.ApplicationJpaRepository;
import com.ideyatech.opentides.um.response.CourseCreate;
import com.ideyatech.opentides.um.response.CourseCreationResponse;
import com.ideyatech.opentides.um.response.CourseResponse;
import com.ideyatech.opentides.um.util.SecurityUtil;
import com.ideyatech.opentides.um.validator.CourseValidator;

/**
 * 
 * @author johanna@ideyatech.com
 *
 */
@BasePathAwareController
//@RestController
@RequestMapping("/api/course")
public class CourseController extends BaseRestController<Course> {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private PreviewVideoRepository previewVideoRepository;
	
	@Autowired
	private BadgeRepository badgeRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LessonRepository lessonRepository;

	@Autowired
	private UserCourseRepository userCourseRepository;
	
	@Autowired
	private ELearningUserRepository elearningUserRepository;

	@Autowired
	CourseValidator courseValidator;

	@Autowired
	private CourseProgressRepository courseProgressRepository;

	@Autowired
	private VideoProgressRepository videoProgressRepository;

	@Autowired
	private ElementRepository elementRepository;

	@Autowired
	private QuizResultRepository quizResultRepository;
	
	@Autowired
	private ElementProgressRepository elementProgressRepository;
	
	
	@Autowired
	CourseRepository courseRepo;
	
	@Autowired
	CurriculumRepository curriculumrepo;
	
	
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourses() {
		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
			return ResponseEntity.ok(courseRepository.findAll());
		else {
			List<Course> result = (List<Course>) courseRepository.getAllNotDeletedCourses();
			List<CourseTitle > projected = new ArrayList<>();
            for (Course course : result) {
                projected.add(projectionFactory.createProjection(CourseTitle .class, course));
            }
			return ResponseEntity.ok(projected);
		}
	}

	@GetMapping("/findAll/count")
	public @ResponseBody ResponseEntity<?> getAllCoursesCount() {
        return ResponseEntity.ok(courseRepository.countAllNotDeletedCourses());
	}

	@GetMapping("/findAll/count-exclude-owned")
	public @ResponseBody ResponseEntity<?> getAllCoursesCountExcludeOwned() {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		return ResponseEntity.ok(courseRepository.countAllExceptOwned(userId));
	}

	@GetMapping("/find/{page}/{size}")
	public @ResponseBody ResponseEntity<?> getRecentCourses(@PathVariable Integer page, @PathVariable Integer size) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		List<Course> courses = courseRepository.findAllExceptOwned(userId, 
						new PageRequest(page, size, new Sort(Sort.Direction.DESC, "createDate")));
		List<CourseProjection> projected = new ArrayList<>();
		for (Course course : courses) {
            projected.add(projectionFactory.createProjection(CourseProjection.class, course));
        }

		return ResponseEntity.ok(projected);
	}

	@GetMapping("/findpopular/{page}/{size}")
	public @ResponseBody ResponseEntity<?> getPopularCourses(@PathVariable Integer page, @PathVariable Integer size) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		List<Map<String, Object>> courses = userCourseRepository.findPopularCourses(new PageRequest(page, size));
		List<Map<String, Object>> projected = new ArrayList<>();
		for (Map<String, Object> map: courses) {
			Course course = (Course) map.get("course");
			CourseProjection proj = projectionFactory.createProjection(CourseProjection.class, course);
			Map<String, Object> newMap = new HashMap<>();
			newMap.put("course", proj);
			newMap.put("takers", map.get("takers"));
			projected.add(newMap);
		}
		return ResponseEntity.ok(projected);
	}

	@GetMapping("/findpopular/count")
	public @ResponseBody ResponseEntity<?> getPopularCoursesCount() {
		List<Map<String, Object>> records = userCourseRepository.getAllPopularCoursesCnt();
		return ResponseEntity.ok(records.size());
	}

	@GetMapping("/findowned/{page}/{size}")
	public @ResponseBody ResponseEntity<?> getOwnedCourses(@PathVariable Integer page, @PathVariable Integer size) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		List<Course> courses = courseRepository.getOwnedCourses(userId, new PageRequest(page, size));
		
		List<CourseProjection> projected = new ArrayList<>();
        for (Course course : courses) {
            projected.add(projectionFactory.createProjection(CourseProjection.class, course));
        }

		return ResponseEntity.ok(projected);
	}

	@GetMapping("/findowned/count")
	public @ResponseBody ResponseEntity<?> getOwnedCoursesCount() {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		return ResponseEntity.ok(courseRepository.getOwnedCoursesCount(userId));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseById(@PathVariable String id) {
		Course course = null;
		List<Course> courseList = courseRepository.getCourseByIdNotDeleted(id);
		if(courseList.size() > 0) course = courseList.get(0);
        CourseProjection success = projectionFactory.createProjection(CourseProjection.class, course);
		return ResponseEntity.ok(success);
	}

	@RequestMapping(value = "/progress/percent/{courseId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProgressPercent(@PathVariable String courseId) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		CourseProgress courseProgress = courseProgressRepository.getCourseProgressByCourseAndUser(courseId, userId);
		if (courseProgress == null) {
			return ResponseEntity.ok(0);
		} else {
			int totalNumElem = getTotalNumElem(courseRepository.findOne(courseId));
			
			List<ElementProgress> elemProgress = courseProgress.getElementLogs();
			int countFinished = 0;
			for (ElementProgress elemProg: elemProgress) {
				if (elemProg.isCompleted()) {
					countFinished++;
				}
			}
			float percentFinished = (float) countFinished / (float) totalNumElem * 100f;
			return ResponseEntity.ok(Math.round(percentFinished));
		}
	}

	private int getTotalNumElem(Course course) {
		int total = 0;
		for (Lesson lesson: course.getLessons()) {
			total += lesson.getNumOfElements();
		}
		return total;
	}
	
	@RequestMapping(value = "/progress/{courseId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProgress(@PathVariable String courseId){
		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);
		CourseProgress courseProgress = getOrCreateCourseProgress(user, courseId);

		Map<String, Object> courseProgMap = new HashMap<>();

		courseProgMap.put("courseId", courseProgress.getCourse().getId());

		
		List<Map<String, Object>> elemProgList = new ArrayList<>();
		if (courseProgress.getElementLogs() != null) {
			for (ElementProgress elemProg: courseProgress.getElementLogs()) {
				Map<String, Object> elemProgMap = new HashMap<>();
				elemProgMap.put("id", elemProg.getElement().getId());
				elemProgMap.put("completed", elemProg.isCompleted());
				if (elemProg.getElement() instanceof Quiz) {
					List<QuizResult> results = quizResultRepository.getResultsByQuizAndUser(elemProg.getElement().getId(), user.getId());
					if (results.size() > 0) {
						QuizResult result = results.get(0);
						elemProgMap.put("score", result.getScore());
						elemProgMap.put("totalItems", result.getTotalItems());
						elemProgMap.put("passed", result.getPassed());
					}
				}
				elemProgList.add(elemProgMap);
			}
		}

		courseProgMap.put("elements", elemProgList);

		return ResponseEntity.ok(courseProgMap);
	}
	
	private CourseProgress getOrCreateCourseProgress(ELearningUser user, String courseId){
		CourseProgress courseProgress = courseProgressRepository.getCourseProgressByCourseAndUser(courseId, user.getId());
		if(courseProgress == null){
			courseProgress = new CourseProgress();
			courseProgress.setCourse(courseRepository.findOne(courseId));
			courseProgress.setUser(user);
			courseProgress = courseProgressRepository.save(courseProgress);
		}
		return courseProgress;
	}

	@GetMapping("/progress/{courseId}/{elementId}")
	public @ResponseBody ResponseEntity<?> recordElementProgress(@PathVariable String courseId, @PathVariable String elementId){

		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);
		CourseProgress courseProgress = getOrCreateCourseProgress(user, courseId);
		Element element = elementRepository.findOne(elementId);
		if(element instanceof Video) {
			VideoProgress videoProg = videoProgressRepository.getVideoProgressByCourseProgressElementAndUser(courseProgress.getId(), element.getId(), user.getId());
			if(videoProg == null){
				videoProg = new VideoProgress();
				videoProg.setUser(user);
				videoProg.setCourseProgress(courseProgress);
				videoProg.setElement(element);
			}
			videoProg.setCompleted(true);
			videoProgressRepository.save(videoProg);
		} else if(element instanceof Quiz) {
			ElementProgress quizElemProgress = elementProgressRepository.getElementProgressByCourseProgressElementAndUser(courseProgress.getId(), element.getId(), user.getId());
			if (quizElemProgress == null) {
				quizElemProgress = new ElementProgress();
				quizElemProgress.setUser(user);
				quizElemProgress.setCourseProgress(courseProgress);
				quizElemProgress.setElement(element);
			}
			quizElemProgress.setCompleted(true);
			elementProgressRepository.save(quizElemProgress);
		}

		if (!courseProgress.getFinished()) {
			courseProgress = getOrCreateCourseProgress(user, courseId); // Get again so that the newly saved element is included
			boolean finishedAll = true;
			for (ElementProgress elemProg: courseProgress.getElementLogs()) {
				if (!elemProg.isCompleted()) {
					finishedAll = false;
				}
			}
			courseProgress.setFinished(finishedAll);
			courseProgressRepository.save(courseProgress);
		}
		
		return ResponseEntity.ok("Successfully Recorded Element Progress");
	}
	
	@PostMapping("addcourse")
	public @ResponseBody ResponseEntity<?> addCourse(@RequestBody CourseDTO2 courseDto2, BindingResult bindingResult,
			HttpServletRequest request) {
		
		//String userId = SecurityUtil.getJwtSessionUser().getId();
		//ELearningUser user = elearningUserRepository.findOne(userId);
      
		
//		 courseValidator.validate(courseDto2, bindingResult);
//		 if (bindingResult.hasErrors()) {
//		 	List<MessageResponse> messageResponses = CrudUtil.convertErrorMessage(bindingResult, request.getLocale(),
//		 			messageSource);
//		 	return ResponseEntity.badRequest().body(messageResponses);
//		 }
		
//313ebe3-19da-4af6-8a30-ce999c4febc9		
		
		Course course = null;

		if(courseDto2.getId() == null || courseDto2.getId().isEmpty()) {
			course = new Course();
			course.setNumOfLessons(0);
			//course.setUser(user);
			String vimeoId = courseDto2.getVimeoId();
			String fileName = courseDto2.getFileName();
			if(courseDto2.getBadgeId() != null && !courseDto2.getBadgeId().isEmpty()) {
				Badge badge = badgeRepository.findOne(courseDto2.getBadgeId());
				if(badge != null) {
					Set<Badge> badges = new HashSet<>();
			        badges.add(badge);
			        course.setBadges(badges);
				}
			}
			course.setTitle(courseDto2.getTitle());
			course.setDescription(courseDto2.getDescription());
			course.setTags(courseDto2.getTags());
			
			course = courseRepository.save(course);
			//previewVideoRepository.save(course);
			//op
			
			//course.setCurriculum(curriculum);
			Curriculum curriculam = new Curriculum();
			List<Curriculum> currlist = new ArrayList<Curriculum>();
			List<Unit> unitlist = new ArrayList<Unit>();
			Unit unit  = new Unit();
		//	System.out.println("size "+courseDto2.getCategories().size());
			
//			for(int i=0;i<courseDto2.getCategories().size();i++) {
//				curriculam.setClassName(courseDto2.getCategories().get(i).getClassName());
//				for(int j=0;i<courseDto2.getCategories().get(j).getUnit().size();j++) {
//					unit.setUnitName(courseDto2.getCategories().get(i).getUnit().get(j).getUnitName());
//					unitlist.add(unit);
//					curriculam.setUnit(unitlist);
//					}
//				currlist.add(curriculam);
//				course.setCurriculum(currlist);	
//			}
		//	course = courseRepository.save(course);
			//courseRepository.save((Iterable<S>) courseDto2);
		    //course =	courseRepo.save(course);
			// if(previewVideo != null /*&& video.getYoutubeId() != null && !video.getYoutubeId().isEmpty()*/) {
			// 	previewVideo.setCourse(course);mes
			// 	previewVideoRepository.save(previewVideo);
			// }

			
			if (vimeoId != null && !vimeoId.isEmpty()) {
				PreviewVideo newPreview = new PreviewVideo();
				newPreview.setVimeoId(vimeoId);
				newPreview.setCourse(course);
				newPreview.setFileName(fileName);
				previewVideoRepository.save(newPreview);
			}
			
			if (course != null) {
				return ResponseEntity.ok(projectionFactory.createProjection(CourseProjection.class, course));
				//return new ResponseEntity<>(course, HttpStatus.OK);
			}
		} else {
			course = courseRepository.findOne(courseDto2.getId());
			if(course != null) {
				course.setTitle(courseDto2.getTitle());
				course.setDescription(courseDto2.getDescription());
				course.setTags(courseDto2.getTags());
				if(courseDto2.getBadgeId() != null && !courseDto2.getBadgeId().isEmpty()) {
					Badge badge = badgeRepository.findOne(courseDto2.getBadgeId());
					if(badge != null) {
						Set<Badge> badges = new HashSet<>();
				        badges.add(badge);
				        course.setBadges(badges);
					}
				}
			}
			
			String vimeoId = courseDto2.getVimeoId();
			String fileName = courseDto2.getFileName();
			PreviewVideo previewVideo = previewVideoRepository.findByCourseId(course.getId());
			if (previewVideo == null) {
				previewVideo = new PreviewVideo();
			}
			if(previewVideo.getVimeoId() == null || !previewVideo.getVimeoId().equals(vimeoId)) {
				previewVideo.setVimeoId(vimeoId);
				previewVideo.setCourse(course);
				previewVideo.setFileName(fileName);
				previewVideoRepository.save(previewVideo);
			}

			course = courseRepository.save(course);
			if (course != null) {
				return ResponseEntity.ok(projectionFactory.createProjection(CourseProjection.class, course));
				
					//	return new ResponseEntity<>(course, HttpStatus.OK);
			}
		}

		return new ResponseEntity<String>("Could not save course.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

//	@PostMapping("add")
//	public @ResponseBody ResponseEntity<?> addCourse(@RequestBody CourseDTO2 courseDto2, BindingResult bindingResult,
//			HttpServletRequest request) {
//		
//		
//		
//		ELearningUser user = elearningUserRepository.findOne(courseDto2.getId());
//		//BaseUser user
//		
//
//		
//		Course course = null;
//		CourseResponse response = new CourseResponse();
//
//		//to check lessn and class are repeted
//		List<Curriculum> classno =null;
//
//		
//		
//		if(courseDto2.getId() == null || courseDto2.getId().isEmpty()) {
//			course = new Course();
//			course.setNumOfLessons(0);
//			course.setUser(user);
//			String vimeoId = courseDto2.getVimeoId();
//			String fileName = courseDto2.getFileName();
//			if(courseDto2.getBadgeId() != null && !courseDto2.getBadgeId().isEmpty()) {
//				Badge badge = badgeRepository.findOne(courseDto2.getBadgeId());
//				if(badge != null) {
//					Set<Badge> badges = new HashSet<>();
//			        badges.add(badge);
//			        course.setBadges(badges);
//				}
//			}
//			
//			course.setTitle(courseDto2.getTitle());
//			course.setDescription(courseDto2.getDescription());
//			course.setCourseBio(courseDto2.getCourseBio());
//			course.setTags(courseDto2.getTags());
//			course = courseRepository.save(course);
//			
//			//create class
//			Curriculum categeries = new Curriculum();
//		//	List<CourseCategory> list = new ArrayList<CourseCategory>();
//			
//			for(int i=0;i<courseDto2.getCategories().size();i++) {
////		
////				categeries.setCategoryName(courseDto2.getCategaries().get(i).getCategoryName());
////				categeries.setClassno(courseDto2.getCategaries().get(i).getClassno());
////				categeries.setUnit(courseDto2.getCategaries().get(i).getUnit());
//		        categeries.setCourse(course);
//		        categariesrepo.save(categeries);
//			}
//			// if(previewVideo != null /*&& video.getYoutubeId() != null && !video.getYoutubeId().isEmpty()*/) {
//			// 	previewVideo.setCourse(course);mes
//			// 	previewVideoRepository.save(previewVideo);
//			// }
//
//			if (vimeoId != null && !vimeoId.isEmpty()) {
//				PreviewVideo newPreview = new PreviewVideo();
//				newPreview.setVimeoId(vimeoId);
//				newPreview.setCourse(course);
//				newPreview.setFileName(fileName);
//				previewVideoRepository.save(newPreview);
//			}
//			
//			
//			if (course != null) {
//				
//				response.setCode(200);
//				response.setMessage("course creted Successfully ");
//				response.setStatus("Success");
//				response.setData(course);
//				//return ResponseEntity.ok(projectionFactory.createProjection(CourseProjection.class, response));
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//		} else {
//			course = courseRepository.findOne(courseDto2.getId());
//			if(course != null) {
//				course.setTitle(courseDto2.getTitle());
//				course.setDescription(courseDto2.getDescription());
//				course.setTags(courseDto2.getTags());
//				//create class and lesson
//				Carriculam categeries = new Carriculam();
//
//				for(int i=0;i<courseDto2.getCategaries().size();i++) {
////			
////					categeries.setCategoryName(courseDto2.getCategaries().get(i).getCategoryName());
////					categeries.setClassno(courseDto2.getCategaries().get(i).getClassno());
////					categeries.setUnit(courseDto2.getCategaries().get(i).getUnit());
//			        categeries.setCourse(course);
//			        categariesrepo.save(categeries);
//			        
//				}
//				
//				
//				
//				if(courseDto2.getBadgeId() != null && !courseDto2.getBadgeId().isEmpty()) {
//					Badge badge = badgeRepository.findOne(courseDto2.getBadgeId());
//					if(badge != null) {
//						Set<Badge> badges = new HashSet<>();
//				        badges.add(badge);
//				        course.setBadges(badges);
//					}
//				}
//			}
//			
//			String vimeoId = courseDto2.getVimeoId();
//			String fileName = courseDto2.getFileName();
//			
//			
//		
//			
//
//			PreviewVideo previewVideo = previewVideoRepository.findByCourseId(course.getId());
//			if (previewVideo == null) {
//				previewVideo = new PreviewVideo();
//			}
//			if(previewVideo.getVimeoId() == null || !previewVideo.getVimeoId().equals(vimeoId)) {
//				previewVideo.setVimeoId(vimeoId);
//				previewVideo.setCourse(course);
//				previewVideo.setFileName(fileName);
//				previewVideoRepository.save(previewVideo);
//			}
//
//			course = courseRepository.save(course);
//			if (course != null) {
//				response.setCode(200);
//				response.setMessage("unit created Successfully ");
//				response.setStatus("Success");
//				//response.setData(course);
//				//return ResponseEntity.ok(projectionFactory.createProjection(CourseProjection.class, course));
//				return ResponseEntity.ok(response);
//			    //return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//		}
//
//		return new ResponseEntity<String>("Could not save course.", HttpStatus.INTERNAL_SERVER_ERROR);
//	}
	@PostMapping("/{id}/delete")
	public @ResponseBody ResponseEntity<?> deleteCourse(@PathVariable String id, HttpServletRequest httpRequest) {
		Course course = courseRepository.findOne(id);
		if (course == null) {
			return new ResponseEntity<String>("The course you are trying to delete does not exist.",
					HttpStatus.BAD_REQUEST);
		}
		course.setDeleted(true);
		courseRepository.save(course);
		return ResponseEntity.ok("Successfully deleted course.");

	}

//	@GetMapping("/search/{keyword}")
	public @ResponseBody ResponseEntity<?> search(@PathVariable String keyword, HttpServletRequest httpRequest) {
		Date startTime = new Date();
		int maxResults = 12;
		List<Course> courses = courseRepository.searchCourse(keyword, new PageRequest(0, maxResults));
		int maxSuggestions = 6;
		List<String> suggestions = new ArrayList<>();
		for (Course course: courses) {
			String[] tags = course.getTags().split(",");
			if (maxSuggestions <= 0) {
				break;
			}
			for (String tag: tags) {
				if (tag.contains(keyword)) {
					if (maxSuggestions-- > 0) {
						String tagLower = tag.toLowerCase();
						if (!suggestions.contains(tagLower)) {
							suggestions.add(tagLower);
						}
					}
					else {
						break;
					}
				}
			}
		}

		int maxCourses = 6;
		List<SearchCourseProjection> projected = new ArrayList<>();
        for (Course course : courses) {
			if (maxCourses-- > 0) {
				projected.add(projectionFactory.createProjection(SearchCourseProjection.class, course));
			} else if (maxSuggestions-- > 0) {
				projected.add(projectionFactory.createProjection(SearchCourseProjection.class, course));
			}
		}

		Map<String, List<? extends Object>> resultsAndSuggestions = new HashMap<>();
		resultsAndSuggestions.put("courses", projected);
		resultsAndSuggestions.put("suggestions", suggestions);

		Date endTime = new Date();
		System.out.println(endTime.getTime()-startTime.getTime());

		return ResponseEntity.ok(resultsAndSuggestions);
	}

	@GetMapping("/search/{keyword}")
	public @ResponseBody ResponseEntity<?> search2(@PathVariable String keyword, HttpServletRequest httpRequest) {
		Date startTime = new Date();
		int maxResults = 12;
		int maxSuggestions = 6;
		List<Course> courses = courseRepository.searchCourse(keyword, new PageRequest(0, maxResults));

		List<String> suggestions = new ArrayList<>();
		for (Course course: courses) {
			System.out.println(course.getTitle());
			String[] tags = course.getTags().split(",");
			if (maxSuggestions <= 0) {
				break;
			}
			for (String tag: tags) {
				if (tag.contains(keyword)) {
					if (maxSuggestions-- > 0) {
						String tagLower = tag.toLowerCase();
						if (!suggestions.contains(tagLower)) {
							suggestions.add(tagLower);
						}
					}
					else {
						break;
					}
				}
			}
		}

		int maxCourses = 6;
		List<SearchCourseProjection> projected = new ArrayList<>();
		for (Course course : courses) {
			if (maxCourses-- > 0 || maxSuggestions-- >0) {
				projected.add(projectionFactory.createProjection(SearchCourseProjection.class, course));
			} else break;
		}

		Map<String, List<? extends Object>> resultsAndSuggestions = new HashMap<>();
		resultsAndSuggestions.put("courses", projected);
		resultsAndSuggestions.put("suggestions", suggestions);

		Date endTime = new Date();
		System.out.println(endTime.getTime()-startTime.getTime());

		return ResponseEntity.ok(resultsAndSuggestions);
	}

	@GetMapping("/full-search/{keyword}")
	public @ResponseBody ResponseEntity<?> fullSearch(@PathVariable String keyword, HttpServletRequest httpRequest) {
		int maxResults = 12;
		List<Course> courses = courseRepository.searchCourse(keyword, new PageRequest(0, maxResults));
		int maxSuggestions = 6;
		List<String> suggestions = new ArrayList<>();
		for (Course course: courses) {
			String[] tags = course.getTags().split(",");
			if (maxSuggestions <= 0) {
				break;
			}
			for (String tag: tags) {
				if (tag.contains(keyword)) {
					if (maxSuggestions-- > 0) {
						String tagLower = tag.toLowerCase();
						if (!suggestions.contains(tagLower)) {
							suggestions.add(tagLower);
						}
					}
					else {
						break;
					}
				}
			}
		}

		int maxCourses = 6;
		List<SearchCourseProjection> projected = new ArrayList<>();
        for (Course course : courses) {
			if (maxCourses-- > 0) {
				projected.add(projectionFactory.createProjection(SearchCourseProjection.class, course));
			} else if (maxSuggestions-- > 0) {
				projected.add(projectionFactory.createProjection(SearchCourseProjection.class, course));
			}
		}
		
		Map<String, List<? extends Object>> resultsAndSuggestions = new HashMap<>();
		resultsAndSuggestions.put("courses", projected);
		resultsAndSuggestions.put("suggestions", suggestions);

		return ResponseEntity.ok(resultsAndSuggestions);
	}

	@GetMapping("/temp/fix-data")
	public @ResponseBody ResponseEntity<?> fixData() {
		List<Course> courses = (List<Course>) courseRepository.findAll();
		for (Course course: courses) {
			for (int i = 0; i < course.getLessons().size(); i++) {
				Lesson lesson = course.getLessons().get(i);
				lesson.setOrdinal(i+1);
				lesson.setNumOfElements(lesson.getElements().size());
				int quizCount = 0;
				int videoCount = 0;
				for (int j = 0; j < lesson.getElements().size(); j++) {
					Element elem = lesson.getElements().get(j);
					elem.setOrdinal(j+1);
					if (elem instanceof Quiz) {
						Quiz quiz = (Quiz) elem;
						for (int k = 0; k < quiz.getQuestions().size(); k++) {
							Question question = quiz.getQuestions().get(k);
							question.setOrdinal(k+1);
							for (int l = 0; l < question.getChoices().size(); l++) {
								Choice choice = question.getChoices().get(l);
								choice.setOrdinal(l+1);
							}
						}
						quizCount++;
					} else if (elem instanceof Video) {
						videoCount++;
					}
				}
				lesson.setNumOfQuizzes(quizCount);
				lesson.setNumOfVideos(videoCount);
			}
		}
		return ResponseEntity.ok("OK");
	}

	@PostMapping("add")
	public @ResponseBody ResponseEntity<?> addCourseNew(@RequestBody Course course, BindingResult bindingResult,
			HttpServletRequest request) {
		
		CourseResponse response = new CourseResponse();
		
		Course courseObj = courseRepo.save(course);
		//courseRepository.save(course);
//				for(int i=0;i<courseObj.getCurriculum().size();i++) {
//					courseObj.getCurriculum().get(i).setCourse(courseObj);
//					//courseObj = courseRepo.save(course);
//					for(int j=0;j<courseObj.getCurriculum().get(i).getUnit().size();j++) {
//						courseObj.getCurriculum().get(i).getUnit().get(j).setCurriculum(courseObj.getCurriculum().get(i));
//					}
//					courseObj = courseRepo.save(course);
//				}
		CourseCreationResponse cresponse = new CourseCreationResponse();
		
		if(courseObj != null) {
			cresponse.setCourseid(courseObj.getId());
			cresponse.setCourseBio(courseObj.getCourseBio());
			cresponse.setDescription(courseObj.getDescription());
			cresponse.setTitle(courseObj.getTitle());
			cresponse.setTags(courseObj.getTags());
			
			for(int i=0;i<courseObj.getCurriculum().size();i++) {
				//cresponse.setCourseid(courseObj.getCurriculum().get(i).getId());
				cresponse.setClassName(courseObj.getCurriculum().get(i).getClassName());
				cresponse.setUnit(courseObj.getCurriculum().get(i).getUnit().get(i).getUnitName());
				cresponse.setUnitname(courseObj.getCurriculum().get(i).getUnit().get(i).getUnitName());
				response.setData(cresponse);
				
				
			}
		}
		response.setCode(200);
		response.setMessage("course created Successfully");
		response.setStatus("success");
			
		//response.setData(courseObj);
		//response.setData(courseObj.getId(),
				//courseObj.getCurriculum().get(i).);
		
		//return ResponseEntity.ok("OK");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
//	 @GetMapping("/posts")
//	    public Page<Course> getAllPosts(Pageable pageable) {
//	        return courseRepo.findAll(pageable);
//	    }
	            
	@GetMapping("/listdata")
	public @ResponseBody List<Course> getData() {
    // List<Course> courses = (List<Course>) courseRepository.findAll();
		 List<Course> courses = (List<Course>) courseRepository.findbydatewise();
//		courseService.getCourseData()
	return courses;
		
//		 ModelMapper mapper = new ModelMapper();
//		 mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
//		 CourseCreate course = new  CourseCreate();
//		 System.out.println("course size "+courses.size());
//		 for(int i=0;i<courses.size();i++) {
//		 course.setId(courses.get(i).getId());
//		 course.setCourseBio(courses.get(i).getCourseBio());
//		 course.setTags(courses.get(i).getTags());
//		 course.setDescription(courses.get(i).getDescription());
//		 course.setTitle(courses.get(i).getTitle());
//		 course.setCourseCategaries(courses.get(i).getCourseCategaries());
//		 course.setCurriculum(courses.get(i).getCurriculum());
//		
//		 }
		
		
	}
	
	//@RequestMapping(value = "id", method = RequestMethod.GET)
	// @RequestMapping(name = "/coursedata/", method = RequestMethod.GET)
	@RequestMapping(value = "Info/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> CourseById(@PathVariable("id") String id) {
		//Course course = null;
		//List<Course> courseList = courseRepository.getCourseByIdNotDeleted(id);
		//if(courseList.size() > 0) course = courseList.get(0);
        //  CourseProjection success = projectionFactory.createProjection(CourseProjection.class, course);
		//return ResponseEntity.ok(courseList);
		//return new ResponseEntity<>(success, HttpStatus.OK);
		CourseResponse response = new CourseResponse();
		//List<Course> classunit = (List<Course>) courseRepository.findAll();
		
		List<Course> classunit = courseRepository.findbyid(id);
		
		if(classunit!=null) {
		List<CourseProjection> projected = new ArrayList<>();
        //for (Course course : classunit) {
		for(Course course : classunit) {
          projected.add(projectionFactory.createProjection(CourseProjection.class, course));
          response.setData(projected);
        }
        response.setAppCode("10000");
        response.setMessage("all data of course");
        response.setCode(200);
        response.setStatus("success");
		return new ResponseEntity<>(response, HttpStatus.OK);
		}else {
			
			return new ResponseEntity<>("hi", HttpStatus.OK);	
		}
	}

	@RequestMapping(value = "findUnit", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> findclassByunit() {
	
		CourseResponse response = new CourseResponse();
		//List<Course> classunit = (List<Course>) courseRepository.findAll();
		List<Curriculum> classunit = curriculumrepo.findAll();
		
		if(classunit!=null) {
		List<CourseunitProjection> projected = new ArrayList<>();
        //for (Course course : classunit) {
		for(Curriculum course : classunit) {
          projected.add(projectionFactory.createProjection(CourseunitProjection.class, course));
          response.setData(projected);
        }
        response.setAppCode("10000");
        response.setMessage("all data of unit");
        response.setCode(200);
        response.setStatus("success");
		return new ResponseEntity<>(response, HttpStatus.OK);
		}else {
			
			return new ResponseEntity<>("null", HttpStatus.OK);	
		}
		
	}
	
	@GetMapping("latestcourse")
	public @ResponseBody ResponseEntity<?> newCourse(){
		CourseResponse response = new CourseResponse();
      List<Course> classunit = courseRepository.findnewcourse();
		
		if(classunit!=null) {
		List<LatestCourseProjection> projected = new ArrayList<>();
        //for (Course course : classunit) {
		for(Course course : classunit) {
          projected.add(projectionFactory.createProjection(LatestCourseProjection.class, course));
          response.setData(projected);
        }
        response.setAppCode("10000");
        response.setMessage("all data of course");
        response.setCode(200);
        response.setStatus("success");
		return new ResponseEntity<>(response, HttpStatus.OK);
		}else {
			
			return new ResponseEntity<>("hi", HttpStatus.OK);	
		}
		
		
	}
	
	
	
}
