package com.ideyatech.opentides.um.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gamify.elearning.dto.FileupodeDto;
import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.Unit;
import com.gamify.elearning.vimeo.Vimeo;
import com.gamify.elearning.vimeo.VimeoException;
import com.gamify.elearning.vimeo.VimeoResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.ideyatech.opentides.um.response.CourseResponse;
import com.ideyatech.opentides.um.response.UploadFileResponse;
import com.ideyatech.opentides.um.service.UnitFileStorageService;
import com.ideyatech.opentides.um.validator.VideoValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
public class FileuplodeController {

	 private static final Logger logger = LoggerFactory.getLogger(FileuplodeController.class);
	 
	 
        @Autowired
		VideoValidator videoValidator;
        
        
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

		private static final String CLIENT_SECRETS = "/client_secret.json";
		private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube",
				"https://www.googleapis.com/auth/youtube.upload", "https://www.googleapis.com/auth/youtube.force-ssl");

		private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		private final List<String> acceptedExtensions = Arrays.asList("mkv", "avi", "mp4", "wav", "mov", "mpeg");

		private static String refreshToken = "";

		private static GoogleAuthorizationCodeFlow flow;

		private static String CREDENTIALS_DIRECTORY = "oauth-credentials";
	 
	 @Autowired
	 UnitFileStorageService unitservice;
	 
	 @PostMapping("/uploadFile")
	    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,@RequestParam String id/*,@ModelAttribute @Valid FileupodeDto filedto*/) throws Exception {
	       // String dbFile = unitservice.storeFile(file,filedto);
	        String dbFile = unitservice.storeFile(file,id);
	        String address = "/images/";
	        String destination = System.getProperty("user.dir");
	        System.out.println(destination);
			String fullPath = destination + "/webapps" + address;
			System.out.println("path" + fullPath);
			
			
	        String request = "http://ec2-3-84-154-41.compute-1.amazonaws.com:8080";
			// String request ="http://localhost:8080/home/vijay/Desktop/elerning12-01-20/op07/archlatform/webapps";
	        String fileDownloadUri = ServletUriComponentsBuilder.fromUriString(request)
	       // String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/images/")
	        		//.path(fullPath)
	                .path(dbFile)
	                .toUriString();
             System.out.println(fileDownloadUri);
	        return new UploadFileResponse(dbFile, fileDownloadUri,
	                file.getContentType(), file.getSize());
	    }
	
	 @GetMapping("/GetUnitFile")
	 public String getUnitFile(String id) {
		 String Image = null;
		 Image = unitservice.getUnitFile(id);
		 return Image;
	 }
	 
	// @PostMapping("/uploadFile")
	 public String DailyPulseQuestionsUpload(@RequestParam("file") MultipartFile file) throws IOException {

	 String uploadStatus = null;

	 uploadStatus = file.getOriginalFilename();
	 //creating a temporary file for saving the file so that
	 // it can be converted to file by picking it up from that location
	 String path = System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename();
	 File convFile = new File(path);
	 file.transferTo(convFile);

	// uploadService.Upload(convFile);

	 return path;
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
		public File convert(MultipartFile file) throws IOException {
			File convFile = new File(uploadPath, file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			return convFile;
		}
		
		 @PostMapping("/changeDescription")
		 public @ResponseBody ResponseEntity<?>uploadDescription(@RequestBody  FileupodeDto filedto) throws Exception {
			 String dbFile = unitservice.storedescreption(filedto);	
			
			 CourseResponse response = new CourseResponse();
			    response.setAppCode("10000");
		        response.setMessage("all data of course");
		        response.setCode(200);
		        response.setStatus("success"); 
		        
		        
			    //return response;
		        return new ResponseEntity<>(response, HttpStatus.OK);
            }
}
