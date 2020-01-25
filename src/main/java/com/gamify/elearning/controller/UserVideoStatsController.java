package com.gamify.elearning.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.Lesson;
import com.gamify.elearning.entity.UserVideoStats;
import com.gamify.elearning.entity.Video;
import com.gamify.elearning.repository.ElementRepository;
import com.gamify.elearning.repository.LessonRepository;
import com.gamify.elearning.repository.UserVideoStatsRepository;
import com.gamify.elearning.repository.VideoRepository;
import com.gamify.elearning.service.ElementService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailSetResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.ideyatech.opentides.core.entity.SystemCodes;
import com.ideyatech.opentides.core.repository.SystemCodesRepository;
import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.service.UserService;
import com.ideyatech.opentides.um.util.SecurityUtil;
import com.ideyatech.opentides.um.validator.VideoValidator;

/**
 * 
 * @author johanna@ideyatech.com
 *
 */
@BasePathAwareController
//@RestController
@RequestMapping("/api/stats/video")
public class UserVideoStatsController extends BaseRestController<Video> {

	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private ElementService elementService;
	
	@Autowired
	private ElementRepository elementRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private UserVideoStatsRepository userVideoStatsRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SystemCodesRepository systemCodesRepository;

	@Autowired
	VideoValidator videoValidator;

	private static final String CLIENT_SECRETS = "/client_secret.json";
	private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube",
			"https://www.googleapis.com/auth/youtube.upload", "https://www.googleapis.com/auth/youtube.force-ssl");

	private static final String APPLICATION_NAME = "E-Learning";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private final List<String> acceptedExtensions = Arrays.asList("mkv", "avi", "mp4", "wav", "mov", "mpeg");

	private static String refreshToken = "";

	private static GoogleAuthorizationCodeFlow flow;

	private static String CREDENTIALS_DIRECTORY = "oauth-credentials";

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDivisions() {
		if (SecurityUtil.currentUserHasPermission("ACCESS_ALL"))
			return ResponseEntity.ok(videoRepository.findAll());
		else {
			// return only list of user's division
			List<Video> result = (List<Video>) videoRepository.findAll();
			return ResponseEntity.ok(result);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getUserVideoStatsById(@PathVariable String id) {
		return ResponseEntity.ok(userVideoStatsRepository.findOne(id));
	}

	@PostMapping("add")
	public @ResponseBody ResponseEntity<?> addUserVideoStats(@RequestBody UserVideoStats userVideoStats, BindingResult bindingResult,
			HttpServletRequest request) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		BaseUser user = (BaseUser) userRepository.findOne(userId);
//		videoValidator.validate(video, bindingResult);
//		if (bindingResult.hasErrors()) {
//			List<MessageResponse> messageResponses = CrudUtil.convertErrorMessage(bindingResult, request.getLocale(),
//					messageSource);
//			return ResponseEntity.badRequest().body(messageResponses);
//		}
		if(userVideoStats.getId() == null || userVideoStats.getId().isEmpty()) {
			Video video = videoRepository.findOne(userVideoStats.getVideoId());
			if(video != null) {
				userVideoStats.setVideo(video);
				userVideoStats.setUser(user);
				userVideoStats.setViewCount(1);
				userVideoStats.setDateLastViewed(new Date());
				userVideoStats.setCanViewVideo(true);

				//video.getRestrictionType().getKey()
				// String temp = "VIDEO_RESTRICTION_TYPE_VIEW_TWICE";
				switch(video.getRestrictionType().getKey()) {
					case "VIDEO_RESTRICTION_TYPE_VIEW_ONCE":
						userVideoStats.setCanViewVideo(false);
						break;
					case "VIDEO_RESTRICTION_TYPE_VIEW_TWICE":
						userVideoStats.setCanViewVideo(true);
						break;
				}
				
				UserVideoStats success = userVideoStatsRepository.save(userVideoStats);
				if (success != null) {
					return ResponseEntity.ok(success);
				}
			}
		} else {
			UserVideoStats currentUserVideoStats = userVideoStatsRepository.findOne(userVideoStats.getId());
			if(currentUserVideoStats != null) {
				Video video = videoRepository.findOne(userVideoStats.getVideoId());
				currentUserVideoStats.setVideo(video);
				// currentUserVideoStats.setLastPlayedStatus(userVideoStats.getLastPlayedStatus());
				// if(userVideoStats.getLastPlayedStatus().equals("ENDED") || (userVideoStats.getPercentViewed() != null && userVideoStats.getPercentViewed() > 74)) {
				currentUserVideoStats.setViewCount(currentUserVideoStats.getViewCount()+1);
				// }
				//video.getRestrictionType().getKey()
				// String temp = "VIDEO_RESTRICTION_TYPE_VIEW_TWICE";
				switch(video.getRestrictionType().getKey()) {
					case "VIDEO_RESTRICTION_TYPE_VIEW_ONCE":
						if(currentUserVideoStats.getViewCount() > 0) {
							currentUserVideoStats.setCanViewVideo(false);
						}
						break;
					case "VIDEO_RESTRICTION_TYPE_VIEW_TWICE":
						if(currentUserVideoStats.getViewCount() > 1) {
							currentUserVideoStats.setCanViewVideo(false);
						} 
						break;
				}
				UserVideoStats success = userVideoStatsRepository.save(currentUserVideoStats);
				if (success != null) {
					return ResponseEntity.ok(success);
				}
			}
		}
		return new ResponseEntity<String>("Could not save user video stats.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value = "/check-video-status", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getUserVideoStatsById(@RequestParam String videoId, HttpServletRequest httpRequest) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
//		List<UserVideoStats> userVideoStatsList = userVideoStatsRepository.findVideoStatsByUser(userId, videoId, new Date());
		List<UserVideoStats> userVideoStatsList = userVideoStatsRepository.findVideoStatsByUser(userId, videoId);
		if(userVideoStatsList.size() > 0) {
			return ResponseEntity.ok(userVideoStatsList.get(0));
		}
		return ResponseEntity.ok(null);
	}

}
