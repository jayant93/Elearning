package com.gamify.elearning.controller;

import com.gamify.elearning.dto.ThumbnailDTO;
import com.gamify.elearning.dto.VideoDTO;
import com.gamify.elearning.entity.*;
import com.gamify.elearning.repository.*;
import com.gamify.elearning.repository.projection.ThumbnailProjection;
import com.gamify.elearning.repository.projection.VideoProjection;
import com.gamify.elearning.repository.projection.VideoProjection2;
import com.gamify.elearning.service.ElearningFileUploadService;
import com.gamify.elearning.service.ElementService;
import com.gamify.elearning.vimeo.Vimeo;
import com.gamify.elearning.vimeo.VimeoException;
import com.gamify.elearning.vimeo.VimeoResponse;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailSetResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.ideyatech.opentides.core.entity.SystemCodes;
import com.ideyatech.opentides.core.repository.SystemCodesRepository;
import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.service.UserService;
import com.ideyatech.opentides.um.util.SecurityUtil;
import com.ideyatech.opentides.um.validator.VideoValidator;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author johanna@ideyatech.com
 *
 */
@BasePathAwareController
//@RestController
@RequestMapping("/api/video")
public class VideoController extends BaseRestController<Video> {
	
	@Value("${youtube.redirectUri}")
    private String youtubeRedirectUri;
	
	@Value("${youtube.appName}")
    private static String youtubeAppName;
	
	@Value("${upload.path}")
	private String uploadPath;

	@Value("${vimeo.api.access.token}")
	private String vimeoAccessToken;

	@Value("${vimeo.embed.allowed.domain}")
	private String vimeoEmbedAllowedDomain;

	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private PreviewVideoRepository previewVideoRepository;
	
	@Autowired
	private ElementService elementService;
	
	@Autowired
	private ElementRepository elementRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private ThumbnailRepository thumbnailRepository;

	@Autowired
	private ElearningFileUploadService elearningFileUploadService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SystemCodesRepository systemCodesRepository;

	@Autowired
	private ELearningUserRepository elearningUserRepository;
	
	@Autowired
	VideoValidator videoValidator;

	private static final String CLIENT_SECRETS = "/client_secret.json";
	private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube",
			"https://www.googleapis.com/auth/youtube.upload", "https://www.googleapis.com/auth/youtube.force-ssl");

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private final List<String> acceptedExtensions = Arrays.asList("mkv", "avi", "mp4", "wav", "mov", "mpeg");

	private static String refreshToken = "";

	private static GoogleAuthorizationCodeFlow flow;

	private static String CREDENTIALS_DIRECTORY = "oauth-credentials";

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVideos() {
		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
			return ResponseEntity.ok(videoRepository.findAll());
		else {
			List<Video> result = (List<Video>) videoRepository.findAll();
			return ResponseEntity.ok(result);
		}
	}

	@RequestMapping(value = "/findAll/count", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVideoCount() {
		long total = (Long) videoRepository.getTotalVideosActive();
		return ResponseEntity.ok(total);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVideoById(@PathVariable String id) {
		Video video = videoRepository.findOne(id);

		// Map<String, Object> videoMap = new HashMap<>();
		// videoMap.put("id", video.getId());
		// videoMap.put("title", video.getTitle());
		// videoMap.put("description", video.getDescription());
		// videoMap.put("tags", video.getTags());
		// videoMap.put("lessonNo", video.getLesson().getOrdinal());
		// videoMap.put("elementNo", video.getOrdinal());
		// videoMap.put("vimeoId", video.getVimeoId());
		// // videoMap.put("defaultThumbnailUrl", video.getDefaultThumbnailUrl());
		// videoMap.put("duration", video.getDurationInSec());
		// videoMap.put("restrictionType", video.getRestrictionType());
		
		// List<Map<String, Object>> thumbs = new ArrayList<>();
		// for (Thumbnail thumb: video.getThumbnails()) {
		// 	Map<String, Object> map = new HashMap<>();
		// 	map.put("url", thumb.getUrl());
		// 	map.put("active", thumb.getActive());
		// 	map.put("thumbnailId", thumb.getThumbnailId());
		// 	thumbs.add(map);
		// }
		// videoMap.put("thumbnails", thumbs);

		return ResponseEntity.ok(projectionFactory.createProjection(VideoProjection2.class, video));
	}

	@GetMapping("/get-thumbnails/{id}")
	public @ResponseBody ResponseEntity<?> getThumbnailsVideoById(@PathVariable String id) {
		List<Thumbnail> thumbnails = thumbnailRepository.findByVideoId(id);

		List<ThumbnailProjection> projected = new ArrayList<>();
		for (Thumbnail thumb : thumbnails) {
			projected.add(projectionFactory.createProjection(ThumbnailProjection.class, thumb));
		}

		return ResponseEntity.ok(projected);
	}

	@GetMapping("all/unassociated")
	public @ResponseBody ResponseEntity<?> getAllUnassociatedVideos() {
		return ResponseEntity.ok(videoRepository.findAllUnassociatedVideos());
	}

	@GetMapping("all/views")
	public @ResponseBody ResponseEntity<?> getAllViewsPerVideo(HttpServletRequest request) {
		List<Object[]> results = videoRepository.getTotalViewsPerVideo();
		return ResponseEntity.ok(results);
	}

	@PostMapping("add2")
	public @ResponseBody ResponseEntity<?> addVideo(@RequestBody VideoDTO videoDTO, BindingResult bindingResult, HttpServletRequest request) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);

		Video video = null;
		if (videoDTO.getId() != null) {
			video = videoRepository.findOne(videoDTO.getId());
		}

		boolean newVideo = false;
		Lesson lesson = lessonRepository.findOne(videoDTO.getLessonId());

		if (video == null) {
			video = new Video();
			List<Thumbnail> thumbs = new ArrayList<>();
			for (ThumbnailDTO thumbDTO : videoDTO.getThumbnails()) {
				Thumbnail newThumb = new Thumbnail();
				newThumb.setActive(thumbDTO.getActive());
				newThumb.setThumbnailId(thumbDTO.getThumbnailId());
				newThumb.setUrl(thumbDTO.getUrl());
				newThumb.setVideo(video);
				newThumb.setCustom(thumbDTO.getCustom());
				thumbs.add(newThumb);
			}
			video.setThumbnails(thumbs);
			newVideo = true;
			
			video.setLesson(lesson);
			lesson.setNumOfElements(lesson.getNumOfElements()+1);
			lesson.setNumOfVideos(lesson.getNumOfVideos()+1);
			video.setOrdinal(lesson.getNumOfElements());
		} else {
			// Delete missing
			for (int i = video.getThumbnails().size()-1; i >= 0; i--) {
				Thumbnail thumbnail = video.getThumbnails().get(i);
				thumbnail.setActive(false); // active will be set later from dto
				boolean foundThumbnail = false;
				for (ThumbnailDTO thumbnailDTO : videoDTO.getThumbnails()) {
					if(thumbnailDTO.getThumbnailId().equals(thumbnail.getThumbnailId())) {
						foundThumbnail = true;
					}
				}

				if (!foundThumbnail) {
					video.getThumbnails().remove(i);
					thumbnail.setVideo(null);
					thumbnailRepository.delete(thumbnail);
				}
			}

			for (ThumbnailDTO thumbnailDTO: videoDTO.getThumbnails()) {
				Thumbnail thumbnail = null;
				List<Thumbnail> list = thumbnailRepository.findByVideoIdAndThumbnailId(video.getId(), thumbnailDTO.getThumbnailId());
				if (list.size() > 0) {
					thumbnail = list.get(0);
				}
				if (thumbnail == null) {
					thumbnail = new Thumbnail();
				} 
				thumbnail.setVideo(video);
				thumbnail.setActive(thumbnailDTO.getActive());
				thumbnail.setUrl(thumbnailDTO.getUrl());
				thumbnail.setThumbnailId(thumbnailDTO.getThumbnailId());
				thumbnail.setCustom(thumbnailDTO.getCustom());
				thumbnailRepository.save(thumbnail);
			}

			if (!video.getLesson().getId().equals(videoDTO.getLessonId())) {
				Lesson oldLesson = video.getLesson();
				oldLesson.setNumOfElements(oldLesson.getNumOfElements()-1);
				oldLesson.setNumOfVideos(oldLesson.getNumOfVideos()-1);
				lessonRepository.save(oldLesson);
				Lesson newLesson = lessonRepository.findOne(videoDTO.getLessonId());
				newLesson.setNumOfElements(newLesson.getNumOfElements()+1);
				newLesson.setNumOfVideos(newLesson.getNumOfVideos()+1);
				lessonRepository.save(newLesson);
				video.setOrdinal(newLesson.getNumOfElements());
				video.setLesson(newLesson);
			}
		}
		
		video.setTitle(videoDTO.getTitle());
		video.setDescription(videoDTO.getDescription());
		video.setVimeoId(videoDTO.getVimeoId());
		SystemCodes restrictionType = systemCodesRepository.findByKey(videoDTO.getRestrictionType());
		video.setRestrictionType(restrictionType);
		video.setTags(videoDTO.getTags());
		video.setDurationInSec(videoDTO.getDuration());
		video.setFileName(videoDTO.getFileName());
		video.setUser(user);

		Video saved = videoRepository.save(video);
		thumbnailRepository.save(video.getThumbnails());
		if (saved != null) {
			if (newVideo) {
				lessonRepository.save(lesson);
			}
			return ResponseEntity.ok(projectionFactory.createProjection(VideoProjection.class, saved));
		}

		return new ResponseEntity<String>("Could not save video.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/{id}/delete2")
	public @ResponseBody ResponseEntity<?> deleteVideo2(@PathVariable String id, HttpServletRequest httpRequest) {
		List<Element> toSave = new ArrayList<>();
		// return ResponseEntity.ok("Successfully deleted video.");
		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<String>("The video you are trying to delete does not exist.",
					HttpStatus.BAD_REQUEST);
		}

		Lesson lesson = video.getLesson();
		lesson.setNumOfElements(lesson.getNumOfElements()-1);
		lesson.setNumOfVideos(lesson.getNumOfVideos()-1);
		lesson.getElements().remove(video);
		for(int i = 0; i < lesson.getElements().size(); i++) {
			Element element = lesson.getElements().get(i);
			element.setOrdinal(i+1);
			toSave.add(element);
		}
		elementRepository.save(toSave);
		lessonRepository.save(lesson);
		String vimeoId = video.getVimeoId();
		video.setDeleted(true);
		videoRepository.save(video);

		Vimeo vimeo = new Vimeo(vimeoAccessToken);
		try {
			vimeo.delete("/videos/" + vimeoId);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok("Successfully deleted video.");
	}

	@GetMapping("/youtube/{id}")
	public ResponseEntity<?> getThumbnails(@PathVariable("id") String id, HttpServletRequest httpRequest) {
		YouTube youtubeService;
		try {
			youtubeService = getService();
		} catch (Exception e3) {
			return new ResponseEntity<String>("Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		YouTube.Videos.List request;
		try {
			request = youtubeService.videos().list("snippet").setId(id);
			VideoListResponse videoList = request.execute();
			if (videoList != null && videoList.getItems().size() > 0) {
				return ResponseEntity.ok(videoList.getItems().get(0));
			}
			return ResponseEntity.ok(null);
		} catch (IOException e) {
			return new ResponseEntity<String>("Could not retrieve video details.", HttpStatus.BAD_REQUEST);
		}

	}
	
	// @GetMapping("/{id}/youtube")
	// public ResponseEntity<?> getYoutubeVideo(@PathVariable("id") String id, HttpServletRequest httpRequest) {
	// 	Video video = videoRepository.findOne(id);
	// 	if(video == null || video.getYoutubeId().isEmpty()) {
	// 		return new ResponseEntity<String>("Could not find specified video.", HttpStatus.BAD_REQUEST);
	// 	}
	// 	YouTube youtubeService;
	// 	try {
	// 		youtubeService = getService();
	// 	} catch (Exception e3) {
	// 		e3.printStackTrace();
	// 		return new ResponseEntity<String>("Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// 	YouTube.Videos.List request;
	// 	try {
	// 		request = youtubeService.videos().list("snippet,contentDetails").setId(video.getYoutubeId());
	// 		VideoListResponse videoList = request.execute();
	// 		if (videoList != null && videoList.getItems().size() > 0) {
	// 			String isoDate = videoList.getItems().get(0).getContentDetails().getDuration();
	// 			String duration = converttoHHMMSS(isoDate);
	// 			LocalTime t = LocalTime.parse(duration);
	// 			System.out.println(duration);
	// 			video.setDuration(duration);
	// 			Video success = videoRepository.save(video);
	// 			return ResponseEntity.ok(success);
	// 		}
	// 		return ResponseEntity.ok(null);
	// 	} catch (IOException e) {
	// 		return new ResponseEntity<String>("Could not retrieve video details.", HttpStatus.BAD_REQUEST);
	// 	}

	// }
	
	@PostMapping("/profile/upload-photo")
    public @ResponseBody ResponseEntity<?> uploadProfilePhoto(@RequestHeader(value = "OT4-APP-SECRET") String appSecret, @RequestParam("file") MultipartFile multipartFile,
    		HttpServletRequest request) {
    	File file;
		try {
			file = convert(multipartFile);
	    	String url = elearningFileUploadService.upload("user/profile-image/" + UUID.randomUUID(), file);
	        return ResponseEntity.ok(url);
		} catch (IOException e) {
			return ResponseEntity.badRequest().body("Could not change profile image.");
		}
    }
    
	
	@PostMapping("/{id}/thumbnails/upload")
	public ResponseEntity<?> uploadCustomThumbnail(@PathVariable("id") String id, @RequestParam("file") MultipartFile multipartFile, 
			HttpServletRequest httpRequest) throws MalformedURLException, IOException {
		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<String>("The video you are trying to delete does not exist.",
					HttpStatus.BAD_REQUEST);
		}
		YouTube youtubeService;
		try {
			youtubeService = getService();
		} catch (Exception e3) {
			return new ResponseEntity<String>("Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		File file = convert(multipartFile);
		
		InputStreamContent mediaContent = new InputStreamContent("image/png", new BufferedInputStream(new FileInputStream(file)));
		mediaContent.setLength(file.length());
		
		YouTube.Thumbnails.Set request;
		try {
			request = youtubeService.thumbnails().set(video.getYoutubeId(), mediaContent);
			ThumbnailSetResponse response = request.execute();
			if(response != null) {
				video.setDefaultThumbnailUrl(response.getItems().get(0).getDefault().getUrl());
				video.setSelectedThumbnail(3);
				videoRepository.save(video);
			}
			System.out.println(response);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			System.out.println("Could not change thumbnail");
			return new ResponseEntity<String>("Could not retrieve video details.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
//		return ResponseEntity.ok(video);
	}

	@PostMapping("/{id}/thumbnails/edit")
	public ResponseEntity<?> changeThumbnail(@PathVariable("id") String id,
			@RequestParam("thumbnailUrl") String thumbnailUrl, @RequestParam("selectedThumbnail") String selectedThumbnail, HttpServletRequest httpRequest)
			throws MalformedURLException, IOException {
		Video video = videoRepository.findOne(id);
		if (video == null) {
			return new ResponseEntity<String>("The video you are trying to delete does not exist.",
					HttpStatus.BAD_REQUEST);
		}
		YouTube youtubeService;
		try {
			youtubeService = getService();
		} catch (Exception e3) {
			return new ResponseEntity<String>("Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		InputStreamContent mediaContent = new InputStreamContent("image/png",
				new BufferedInputStream(new URL(thumbnailUrl).openStream()));
		mediaContent.setLength(2048L);

		YouTube.Thumbnails.Set request;
		try {
			request = youtubeService.thumbnails().set(video.getYoutubeId(), mediaContent);
			ThumbnailSetResponse response = request.execute();
			video.setDefaultThumbnailUrl(thumbnailUrl);
			video.setSelectedThumbnail(Integer.parseInt(selectedThumbnail));
			videoRepository.save(video);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			System.out.println("Could not change thumbnail");
			return new ResponseEntity<String>("Could not retrieve video details.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/countpermonth")
	public @ResponseBody ResponseEntity<?> countUploadsPerMonth() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.MONTH, -12);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 0);
		Date monthsAgo12 = cal.getTime();

		List<Map<String, Object>> usersPerMonth = videoRepository.getVideoCountPerMonth(monthsAgo12, today);
		String x = null;
		return ResponseEntity.ok(usersPerMonth);
	}

	@GetMapping("upload/progress")
	public @ResponseBody Map<String, Object> getUploadStatus(HttpServletRequest request, HttpSession session) {

		Map<String, Object> model = new HashMap<String, Object>();
		
		String message = (String) session.getAttribute("video-upload-progress");
		System.out.println(message);
		System.out.println(session.getId());
		if (message == null || StringUtil.isEmpty(message))
			model.put("message", "");
		else {
			model.put("message", message);
		}

		return model;
	}
	
	@PostMapping("upload/add-progress")
	public ResponseEntity<?> putProgress(HttpSession session) {
		session.setAttribute("video-upload-progress", "0.34");
		return ResponseEntity.ok("OK"); 
	}

	@PostMapping("/upload-vimeo")
	public @ResponseBody ResponseEntity<?> vimeoUpload(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest, HttpSession session) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		boolean isAcceptable = Arrays.stream(acceptedExtensions.toArray()).anyMatch(extension.toLowerCase()::equals);
		if (extension == null || !isAcceptable) {
			return new ResponseEntity<String>("File format is not accepted.", HttpStatus.BAD_REQUEST);
		}
		
		Vimeo vimeo = new Vimeo(vimeoAccessToken);
		
		String vimeoId = "";
		boolean upgradeTo1080 = false;
		File media = null;
		try {
			media = convert(file);
		} catch (IOException e2) {
			return new ResponseEntity<String>(e2.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			String videoEndPoint = vimeo.addVideo(media, upgradeTo1080);
			
			VimeoResponse info = vimeo.getVideoInfo(videoEndPoint);

			changeVideoSettings(vimeo, videoEndPoint);

			String uri = info.getJson().getString("uri");
			System.out.println(uri);
			vimeoId = uri.split("/")[2];
        } catch (IOException | VimeoException e) {
            e.printStackTrace();
			return new ResponseEntity<String>("Could not upload video.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			media.delete();
		}
		
		return ResponseEntity.ok(vimeoId);
	}

	private void changeVideoSettings(Vimeo vimeo, String endpoint) throws IOException {
		String name = "ripetime archy learning";
		String description = "archy learning video";
		String license = "";
		String privacyView = "disable";
		String privacyEmbed = "whitelist";
		boolean reviewLink = false;
		boolean downloadable = false;
		vimeo.updateVideoMetadata(endpoint, name, description, license, 
								privacyView, privacyEmbed, reviewLink, downloadable);

		vimeo.addVideoPrivacyDomain(endpoint, vimeoEmbedAllowedDomain);
	}

	@PostMapping("/upload-vimeo/progress")
	public @ResponseBody ResponseEntity<?> vimeoUploadWithProgress(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest, HttpSession session) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		boolean isAcceptable = Arrays.stream(acceptedExtensions.toArray()).anyMatch(extension.toLowerCase()::equals);
		if (extension == null || !isAcceptable) {
			return new ResponseEntity<String>("File format is not accepted.", HttpStatus.BAD_REQUEST);
		}
		
		Vimeo vimeo = new Vimeo(vimeoAccessToken);
		
		String vimeoId = "";
		boolean upgradeTo1080 = false;
		File media = null;
		try {
			media = convert(file);
		} catch (IOException e2) {
			return new ResponseEntity<String>(e2.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "tus");
			params.put("redirect_url", "");
			params.put("upgrade_to_1080", upgradeTo1080 ? "true" : "false");
			params.put("upload.approach", "tus");
			params.put("upload.size", Long.toString(media.length()));
			params.put("size", Long.toString(media.length()));
			VimeoResponse response = vimeo.beginUploadVideo(params);
			if (response.getStatusCode() == 200) {
				String uploadLink = response.getJson().getJSONObject("upload").getString("upload_link");
				VimeoResponse tusResp = vimeo.tusPatchApiRequest(uploadLink, 0, media);
				long uploadOffset = Long.parseLong(tusResp.getHeaders().getString("Upload-Offset"));
				String uri = response.getJson().getString("uri");
				vimeoId = uri.split("/")[2];
				System.out.println(tusResp);
				Map<String, Object> map = new HashMap<>();
				map.put("vimeoId", vimeoId);
				map.put("uploadLink", uploadLink);
				map.put("uploadOffset", uploadOffset);
				map.put("fileSize", media.length());
				map.put("uri", uri);
				return ResponseEntity.ok(map);
			}
        } catch (IOException e) {
            e.printStackTrace();
		}
		
		return new ResponseEntity<String>("Could not upload video.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/upload-vimeo/check-progress")
	public @ResponseBody ResponseEntity<?> vimeoUploadCheckProgress(@RequestParam("uploadLink") String uploadLink, 
										@RequestParam("uploadOffset") long prevUploadOffset, 
										@RequestParam("fileSize") long fileSize, 
										@RequestParam("uri") String uri, HttpServletRequest httpRequest, HttpSession session) {
		
		Vimeo vimeo = new Vimeo(vimeoAccessToken);

		try {
			VimeoResponse verifyResp = vimeo.tusHeadApiRequest(uploadLink);
			long uploadLength = Long.parseLong(verifyResp.getHeaders().getString("Upload-Length"));
			long headUploadOffset = Long.parseLong(verifyResp.getHeaders().getString("Upload-Offset"));
			// Check progress
			if (headUploadOffset == uploadLength) {
				vimeo.tusPatchApiRequest(uploadLink, headUploadOffset, null);
				changeVideoSettings(vimeo, uri);
				return ResponseEntity.ok(100);
			}

			// If not yet done then patch again and check progress
			VimeoResponse tusResp = vimeo.tusPatchApiRequest(uploadLink, prevUploadOffset, null);
			long uploadOffset = Long.parseLong(tusResp.getHeaders().getString("Upload-Offset"));
			if (fileSize == uploadOffset) {
				vimeo.tusPatchApiRequest(uploadLink, uploadOffset, null);
				changeVideoSettings(vimeo, uri);
				return ResponseEntity.ok(100);
			}
			return ResponseEntity.ok( ((float) uploadOffset / (float) fileSize * 100f) );
		} catch(IOException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<String>("Error in checking video upload progress.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/preview-vid/{courseId}")
	public @ResponseBody ResponseEntity<?> getPreviewVidByCourseId(@PathVariable("courseId") String courseId) {
		PreviewVideo preview = previewVideoRepository.findByCourseId(courseId);
		String vimeoId = "";
		String fileName = "";
		if (preview != null) {
			vimeoId = preview.getVimeoId();
			if(!StringUtil.isEmpty(preview.getFileName())) {
				fileName = preview.getFileName();
			} else {
				fileName = "";
			}
		}
        Map<String, String> map = new HashMap<>();
        map.put("vimeoId", vimeoId);
        map.put("fileName", fileName);
        return ResponseEntity.ok(map);
	}

	@PostMapping("/upload-preview-video") 
	public @ResponseBody ResponseEntity<?> vimeoUploadPreview(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest, HttpSession session) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		boolean isAcceptable = Arrays.stream(acceptedExtensions.toArray()).anyMatch(extension.toLowerCase()::equals);
		if (extension == null || !isAcceptable) {
			return new ResponseEntity<String>("File format is not accepted.", HttpStatus.BAD_REQUEST);
		}
		
		Vimeo vimeo = new Vimeo(vimeoAccessToken);
		
		String vimeoId = "";
		boolean upgradeTo1080 = false;
		File media = null;
		try {
			media = convert(file);
		} catch (IOException e2) {
			return new ResponseEntity<String>(e2.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			String videoEndPoint = vimeo.addVideo(media, upgradeTo1080);
			
			VimeoResponse info = vimeo.getVideoInfo(videoEndPoint);
//			String uri = (String) info.getJson().get("uri");
			String uri = info.getJson().getString("uri");
			System.out.println(uri);
			vimeoId = uri.split("/")[2];
        } catch (IOException | VimeoException e) {
            e.printStackTrace();
			return new ResponseEntity<String>("Could not upload video.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			media.delete();
		}
		
		return ResponseEntity.ok(vimeoId);
	}

	@PostMapping("/{vimeoId}/vimeo-thumbnails/upload")
	public ResponseEntity<?> uploadVimeoThumbnail(@PathVariable("vimeoId") String id, @RequestParam("file") MultipartFile multipartFile, 
			HttpServletRequest httpRequest) throws MalformedURLException, IOException {
		// Video video = videoRepository.findOne(id);

		File file = convert(multipartFile);
		
		Vimeo vimeo = new Vimeo(vimeoAccessToken);

		String thumbnailId = "";
		List<Thumbnail> thumbnails = thumbnailRepository.findByVimeoId(id);
		for (Thumbnail thumb: thumbnails) {
			if (thumb.getCustom()) {
				System.out.println("DELETING CURRENT CUSTOM THUMB");
				thumbnailId = thumb.getThumbnailId();
				thumbnailRepository.delete(thumb.getId());
			}
		}

		Map<String, Object> map = new HashMap<>();

		try {
			VimeoResponse thumbsInfo = vimeo.get("/videos/" + id + "/pictures");
			Map<String, String> params = new HashMap<>();
			JSONArray array = (JSONArray) thumbsInfo.getJson().get("data");
			for (int i = 0; i < array.length(); i++) {
				if (((JSONObject) array.get(i)).getBoolean("active")) {
					String uri = ((JSONObject) array.get(i)).getString("uri");
					params.put("active", "false");
					VimeoResponse res = vimeo.patch(uri, params);
					break;
				}
			}
			
			VimeoResponse deleteResp = vimeo.delete("/videos/" + id + "/pictures/" + thumbnailId);
			
			// VimeoResponse info = vimeo.get("/videos/" + id);
			VimeoResponse info = vimeo.post("/videos/" + id + "/pictures");
			String link = (String) info.getJson().getString("link");

			vimeo.uploadThumbnail(link, file);
			Map<String, String> params2 = new HashMap<>();
			params2.put("active", "true");
			vimeo.patch(info.getJson().getString("uri"), params2);
			
			map.put("url", info.getJson().getJSONArray("sizes").getJSONObject(0).getString("link"));
			map.put("active", true);
			String split[] = info.getJson().getString("uri").split("/");
			String thumbId = split[split.length - 1];
			map.put("thumbnailId", thumbId);
			map.put("custom", true);
		} catch(IOException e) {
            e.printStackTrace();
			return new ResponseEntity<String>("Could not upload thumbnail.", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
			file.delete();
		}

		return ResponseEntity.ok(map);

	}

	@GetMapping("/{vimeoId}/transcode-status")
	public @ResponseBody ResponseEntity<?> getTranscodeStatus(@PathVariable("vimeoId") String id, HttpServletRequest request, HttpSession session) {
		Vimeo vimeo = new Vimeo(vimeoAccessToken);

		try {
			VimeoResponse resp = vimeo.getTranscodeStatus(id);
			System.out.println(resp);
			Map<String, String> map = new HashMap<>();
			map.put("transcode", resp.getJson().getJSONObject("transcode").getString("status"));
			return ResponseEntity.ok(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<String>("Error checking for transcode status", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/{vimeoId}/create-thumbnails")
	public @ResponseBody ResponseEntity<?> createThumbnail(@PathVariable("vimeoId") String id, HttpServletRequest httpRequest, HttpSession session) {
		Vimeo vimeo = new Vimeo(vimeoAccessToken);

		try {
			Map<String, String> params = new HashMap<>();
			params.put("time", "30");
			params.put("active", Boolean.toString(false));
			VimeoResponse response1 = vimeo.createThumbnail(id, params);

			params.put("time", "60");
			params.put("active", Boolean.toString(false));
			VimeoResponse response2 = vimeo.createThumbnail(id, params);
			System.out.println();

			VimeoResponse thumbnailResponse = vimeo.get("/videos/" + id + "/pictures");
			JSONArray array = thumbnailResponse.getJson().getJSONArray("data");
			List<Map<String, Object>> thumbList = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				Map<String, Object> map = new HashMap<>();
				map.put("url", array.getJSONObject(i).getJSONArray("sizes").getJSONObject(1).getString("link"));
				map.put("active", array.getJSONObject(i).getBoolean("active"));
				String split[] = array.getJSONObject(i).getString("uri").split("/");
				String thumbId = split[split.length - 1];
				map.put("thumbnailId", thumbId);
				thumbList.add(map);
			}
			System.out.println();

			return ResponseEntity.ok(thumbList);
		} catch(IOException e) {
			e.printStackTrace();
		}


		return new ResponseEntity<String>("Error creating some of the thumbnails", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/{vimeoId}/change-thumbnail")
	public @ResponseBody ResponseEntity<?> changeThumbnail(@PathVariable("vimeoId") String vimeoId, @RequestParam("thumbId") String thumbId, 
													@RequestParam("videoId") String videoId, @RequestParam("custom") Boolean custom) {
		Vimeo vimeo = new Vimeo(vimeoAccessToken);

		try {
			// id = "358248387";
			VimeoResponse info = vimeo.get("/videos/" + vimeoId + "/pictures");
			System.out.println(info);

			// VimeoResponse info2 = vimeo.get("/videos/358248387/pictures/811820993");
			// System.out.println(info2);
			Map<String, String> params = new HashMap<>();
			JSONArray array = (JSONArray) info.getJson().get("data");
			System.out.println(array);
			for (int i = 0; i < array.length(); i++) {
				if (((JSONObject) array.get(i)).getBoolean("active")) {
					String uri = ((JSONObject) array.get(i)).getString("uri");
					params.put("active", "false");
					VimeoResponse res = vimeo.patch(uri, params);
					break;
				}
			}
			// String thumbId = "811787355";
			VimeoResponse test = vimeo.get("/videos/" + vimeoId + "/pictures/" + thumbId);
			// System.out.println(test);
			for (int i = 0; i < array.length(); i++) {
				String uri = ((JSONObject) array.get(i)).getString("uri");
				if (uri.contains(thumbId)) {
					params.put("active", "true");
					VimeoResponse res = vimeo.patch(uri, params);
					break;
				}
			}

			List<Thumbnail> thumbnails = thumbnailRepository.findByVideoId(vimeoId);
			for (Thumbnail thumbnail: thumbnails) {
				thumbnail.setActive(false);
			}
			thumbnailRepository.save(thumbnails);
			List<Thumbnail> thumb = thumbnailRepository.findByVideoIdAndThumbnailId(vimeoId, thumbId);
			if (thumb.size() > 0) {
				Thumbnail thumbnail = thumb.get(0);
				thumbnail.setActive(true);
				thumbnailRepository.save(thumbnail);
			} else {
				Thumbnail newThumb = new Thumbnail();
				Video video = videoRepository.findOne(videoId);
				newThumb.setVideo(video);
				newThumb.setCustom(custom);
				newThumb.setActive(true);
			}

			// VimeoResponse info2 = vimeo.get("/videos/" + id + "/pictures");
			// System.out.println(info2);
			
			return ResponseEntity.ok("OK");
		} catch(IOException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<String>("Error changing the thumbnail", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// @PostMapping("/upload")
	// public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest, HttpSession session)
	// 		throws IOException {
	// 	String extension = FilenameUtils.getExtension(file.getOriginalFilename());
	// 	boolean isAcceptable = Arrays.stream(acceptedExtensions.toArray()).anyMatch(extension.toLowerCase()::equals);
	// 	if (extension == null || !isAcceptable) {
	// 		return new ResponseEntity<String>("File format is not accepted.", HttpStatus.BAD_REQUEST);
	// 	}
	// 	YouTube youtubeService;
	// 	try {
	// 		youtubeService = getService();
	// 	} catch (Exception e3) {
	// 		e3.printStackTrace();
	// 		return new ResponseEntity<String>("Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}

	// 	com.google.api.services.youtube.model.Video video = new com.google.api.services.youtube.model.Video();

	// 	File media = null;
	// 	try {
	// 		media = convert(file);
	// 	} catch (IOException e2) {
	// 		return new ResponseEntity<String>(e2.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}

	// 	InputStreamContent mediaContent;
	// 	try {
	// 		mediaContent = new InputStreamContent("video/*", new BufferedInputStream(new FileInputStream(media)));
	// 	} catch (FileNotFoundException e1) {
	// 		return new ResponseEntity<String>(e1.getMessage(), HttpStatus.BAD_REQUEST);
	// 	}

	// 	mediaContent.setLength(media.length());
	// 	mediaContent.setLength(file.getSize());

	// 	VideoSnippet snippet = new VideoSnippet();
	// 	snippet.setTitle(media.getName());

	// 	VideoStatus status = new VideoStatus();
	// 	status.setPrivacyStatus("unlisted");
	// 	video.setStatus(status);
	// 	video.setSnippet(snippet);

	// 	YouTube.Videos.Insert request = null;
	// 	com.google.api.services.youtube.model.Video response = null;
	// 	try {
	// 		request = youtubeService.videos().insert("snippet,status", video, mediaContent);

	// 		MediaHttpUploader uploader = request.getMediaHttpUploader();

	// 		MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {

	// 			@Override
	// 			public void progressChanged(MediaHttpUploader uploader) throws IOException {
	// 				switch (uploader.getUploadState()) {
	// 					case INITIATION_STARTED:
	// 						System.out.println("Initiation Started");
	// 						break;
	// 					case INITIATION_COMPLETE:
	// 						System.out.println("Initiation Completed");
	// 						break;
	// 					case MEDIA_IN_PROGRESS:
	// 						if(httpRequest == null) {
	// 							System.out.println("No request found.");
	// 						} else {
	// 							System.out.println(httpRequest.getRequestURI());
	// 						}
	// 						System.out.println(httpRequest.getSession().getId());
	// 						httpRequest.getSession().setAttribute("video-upload-progress", uploader.getProgress());
	// 						System.out.println("Upload percentage: " + uploader.getProgress());
	// 						break;
	// 					case MEDIA_COMPLETE:
	// 						System.out.println("Upload Completed!");
	// 						break;
	// 					case NOT_STARTED:
	// 						System.out.println("Upload Not Started!");
	// 						break;
	// 				}
	// 			}
				
	// 		};

	// 		uploader.setProgressListener(progressListener);
	// 		response = request.execute();

	// 		return ResponseEntity.ok(response);
	// 	} catch (Exception e) {
	// 		return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// }

	@GetMapping("/authorize")
	public @ResponseBody ResponseEntity<?> exchangeToken(@RequestParam("code") String code,
			@RequestParam("scope") String scopes) throws Exception {
		System.out.println("Client ID: " + flow.getClientId());
		TokenResponse response = flow.newTokenRequest(code)
				.setRedirectUri(youtubeRedirectUri).execute();
		flow.createAndStoreCredential(response, "gamify-user");
		refreshToken = response.getRefreshToken();
		return ResponseEntity.ok("Successfully authorized" + refreshToken);
	}
	
	@PostMapping("/{id}/associate")
	public @ResponseBody ResponseEntity<?> associateVideoToLesson(@PathVariable("id") String videoId, @RequestParam String lessonId) {
		Video video = (Video) elementService.associateElementToLesson(videoId, lessonId);
		if(video == null) {
			return new ResponseEntity<String>("Could not add video to this lesson.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Lesson lesson = lessonRepository.findOne(lessonId);
		lesson.setNumOfVideos(lesson.getNumOfVideos() + 1);
		lessonRepository.save(lesson);
		return ResponseEntity.ok(video);
	}

	public static YouTube getService() throws Exception {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = authorize(httpTransport);
		return new YouTube.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(youtubeAppName)
				.build();
	}

	public static Credential authorize(final NetHttpTransport httpTransport) throws Exception {

		System.out.println("Start authorization chain.");
		System.out.println(refreshToken);
		
		// Load client secrets.
		InputStream in = VideoController.class.getResourceAsStream(CLIENT_SECRETS);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		System.out.println(System.getProperty("user.home"));

		FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(
				new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
		DataStore<StoredCredential> dataStore = fileDataStoreFactory.getDataStore("cred_ds");

		try {
			System.out.println("Step 1");
			if (!dataStore.isEmpty()) {	
				System.out.println("Step 2.a");
				boolean hasKey = dataStore.containsKey("gamify-user");
				System.out.println(hasKey);
				if(hasKey && dataStore.get("gamify-user").getRefreshToken() != null){
					System.out.println(dataStore.get("gamify-user").getRefreshToken());
				}
				Credential credential = new GoogleCredential.Builder().setJsonFactory(JSON_FACTORY)
					.setClientSecrets(clientSecrets).setTransport(httpTransport).build()
					.setRefreshToken(dataStore.get("gamify-user").getRefreshToken()); 
				if (credential != null
						&& (credential.getRefreshToken() != null || credential.getExpiresInSeconds() > 60)) {
					System.out.println("return credential");
					return credential;
				}
			}
		} catch (IOException e) {
			System.out.println("Step 2.b");
			e.printStackTrace();
			return null;
		}

		System.out.println("Step 3");

		// Build flow and trigger user authorization request.
		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
				.setCredentialDataStore(dataStore).setAccessType("offline").setApprovalPrompt("auto").build();
		Credential credential = null;

		System.out.println("Step 4");

		if (refreshToken.isEmpty()) {
			System.out.println("Step 5.a");
			credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		} else {
			System.out.println("Step 5.b");
			credential = new GoogleCredential.Builder().setJsonFactory(JSON_FACTORY).setTransport(httpTransport)
					.setClientSecrets(clientSecrets).build().setRefreshToken(refreshToken);
		}

		return credential;
	}

	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(uploadPath, file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
	
	private String converttoHHMMSS(String youtubetime) {

        String pattern = new String("^PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+))S?$");

        Pattern r = Pattern.compile(pattern);
        String result;

        Matcher m = r.matcher(youtubetime);
        if (m.find()) {
            String hh = m.group(1);
            String mm = m.group(2);
            String ss = m.group(3);
            mm = mm !=null?mm:"0";
            ss = ss !=null?ss:"0";
            result = String.format("%02d:%02d", Integer.parseInt(mm), Integer.parseInt(ss));

            if (hh != null) {
                result = hh + ":" + result;
            }
        } else {
            result = "00:00";
        }
        return result;
    }

}
