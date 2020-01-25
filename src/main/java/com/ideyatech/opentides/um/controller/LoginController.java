package com.ideyatech.opentides.um.controller;

import com.gamify.elearning.dto.UserDTO;
import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.repository.ELearningUserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.ideyatech.opentides.core.entity.user.TokenStore;
import com.ideyatech.opentides.core.entity.webhook.SecurityEvent;
import com.ideyatech.opentides.core.security.JwtAuthenticationToken;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserCredential;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;

import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.request.GooglelogRequest;
import com.ideyatech.opentides.um.response.CustomResponse;
import com.ideyatech.opentides.um.response.GenericResponse;
import com.ideyatech.opentides.um.security.UsernamePasswordAuthenticationProvider;
import com.ideyatech.opentides.um.service.GoogleService;

import io.jsonwebtoken.Claims;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller that will handle logging in of a user
 *
 * @author Gino
 */
@RestController
public class LoginController {

	private static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private ELearningUserRepository eLearningUserRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private SimpMessagingTemplate webSocket;

	@Autowired
	private UsernamePasswordAuthenticationProvider authenticationProvider;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private GoogleService googleService;

	@Value("${google.webclient.id}")
	private String googleWebClientId;

	@Value("${google.oauthclient.id}")
	private String googleOAuthClientId;

	private static NetHttpTransport transport = new NetHttpTransport();
	private static JacksonFactory jsonFactory = new JacksonFactory();

	@Transactional
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> login(@RequestParam("username") String username,
			@RequestParam("password") String password, @RequestHeader(value = "OT4-APP-SECRET") String appSecret,
			HttpServletRequest request) {

		// String encodedPassword = passwordEncoder.encode(password);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken("", appSecret, username, password);
		Authentication authentication = authenticationProvider.authenticate(authenticationToken);
		JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) authentication;
		String token = authenticatedToken.getToken();

		Map<String, Object> response = new HashMap<>();

		response.put("token", token);
		response.put("firstName", authenticatedToken.getFirstName());
		response.put("lastName", authenticatedToken.getLastName());
		response.put("id", authenticatedToken.getId());

		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/api/login/google", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> googleLogin(@RequestParam("googleIdToken") String googleIdToken,
			@RequestParam("googleUserId") String googleUserId,
			@RequestHeader(value = "OT4-APP-SECRET") String appSecret, HttpServletRequest request) throws Exception {
		Map<String, Object> response = new HashMap<>();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Arrays.asList(googleWebClientId, googleOAuthClientId)).build();
         System.out.println("gooogleid verifier "+verifier );
		GoogleIdToken idToken = verifier.verify(googleIdToken);
		System.out.println("token"+idToken);
		GoogleIdToken.Payload payload;
		if (idToken != null) {
			payload = idToken.getPayload();

			// Print user identifier
			String userId = payload.getSubject();

			// Get profile information from payload
			String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");

			BaseUser googleUser = userRepository.findByGoogleUserId(googleUserId);

			if (googleUser == null) {
				// Register
				response.put("firstName", givenName);
				response.put("lastName", familyName);
				response.put("email", email);
				response.put("newUser", true);
				return ResponseEntity.ok(response);
			} else {
				JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken("", appSecret,
						payload.getEmail(), "mKcMmwN9nVmQG7MABuU7");

				Authentication authentication = authenticationProvider.authenticate(authenticationToken);
				JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) authentication;

				String token = authenticatedToken.getToken();
				response.put("token", token);
				response.put("firstName", authenticatedToken.getFirstName());
				response.put("lastName", authenticatedToken.getLastName());
				response.put("id", authenticatedToken.getId());
				response.put("email", email);

				return ResponseEntity.ok(response);
			}

		} else {
			response.put("errorCode", "invalid-id-token");
			response.put("message", "Invalid Google ID token");
			return ResponseEntity.badRequest().body(response);
		}

	}

//    @RequestMapping(value = "/api/login/fb", method = {RequestMethod.POST})
//    public @ResponseBody
//    ResponseEntity<?> facebookLogin(
//            @RequestParam("facebookUserId") String facebookUserId,
//            @RequestParam("accessToken") String facebookAccessToken,
//            @RequestHeader(value = "OT4-APP-SECRET") String appSecret,
//            HttpServletRequest request) throws Exception {
//
//        System.out.println("Check FB login");
//
//        BaseUser user = userRepository.findByFacebookUserId(facebookUserId);
//
//        Map<String, Object> response = new HashMap<>();
//
//        if (user == null) {
//
////            response.put("firstName", givenName);
////            response.put("lastName", familyName);
////            response.put("email", email);
////            response.put("newUser", true);
//            return ResponseEntity.ok("OK");
//
////	        response.put("errorCode", "no-user");
////	        response.put("message", "Facebook user not found in our system.");
////	        return ResponseEntity.badRequest().body(response);
//
//        } else {
//
//            Application application = applicationRepository.findByAppSecret(appSecret);
//
//            // TODO receive access token and check if access token is still valid
//            // Perhaps use https://graph.facebook.com/me?access_token=... to validate
//
//            RestTemplate restTemplate = new RestTemplate();
//            String fbAppId = application.getFbAppId();
//            String fbAppSecret = application.getFbAppSecret();
//            ResponseEntity<String> httpRes = restTemplate.exchange("https://graph.facebook.com/debug_token?input_token={facebookAccessToken}"
//                    + "&access_token={fbAppId}|{fbAppSecret}", HttpMethod.GET, null, String.class, facebookAccessToken, fbAppId, fbAppSecret);
//
//            System.out.println("Response body");
//            System.out.println(httpRes.getBody());
//
//            JSONObject obj = new JSONObject(httpRes.getBody());
//            System.out.println(obj.getJSONObject("data").get("is_valid"));
//
//            if (obj.getJSONObject("data") != null && (boolean) obj.getJSONObject("data").get("is_valid")) {
//
//                JwtAuthenticationToken authenticationToken =
//                        new JwtAuthenticationToken("", appSecret, user.getUsername(), "lX7oOrFoU7owJ05qZ2vM");
//
//                Authentication authentication = authenticationProvider.authenticate(authenticationToken);
//                JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) authentication;
//
//                String token = authenticatedToken.getToken();
//                response.put("token", token);
//                response.put("firstName", authenticatedToken.getFirstName());
//                response.put("lastName", authenticatedToken.getLastName());
//                response.put("id", authenticatedToken.getId());
//
//                return ResponseEntity.ok(response);
//            }
//            return ResponseEntity.badRequest().body("Facebook access token is invalid");
//
//        }
//
//    }

	// DIRECT LOGIN (EDIT DETAILS ONCE LOGGED IN)
	@RequestMapping(value = "/api/login/fb", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> facebookLogin(@RequestBody UserDTO userDTO,
			@RequestHeader(value = "OT4-APP-SECRET") String appSecret, HttpServletRequest request) throws Exception {

		BaseUser user = userRepository.findByFacebookUserId(userDTO.getFacebookUserId());

		Map<String, Object> response = new HashMap<>();

		if (user == null) {

			Application application = applicationRepository.findByAppSecret(appSecret);

			String facebookAccessToken = userDTO.getAuthToken();

			RestTemplate restTemplate = new RestTemplate();
			String fbAppId = application.getFbAppId();
			String fbAppSecret = application.getFbAppSecret();
			ResponseEntity<String> httpRes = restTemplate.exchange(
					"https://graph.facebook.com/debug_token?input_token={facebookAccessToken}"
							+ "&access_token={fbAppId}|{fbAppSecret}",
					HttpMethod.GET, null, String.class, facebookAccessToken, fbAppId, fbAppSecret);

			JSONObject obj = new JSONObject(httpRes.getBody());

			if (obj.getJSONObject("data") != null && (boolean) obj.getJSONObject("data").get("is_valid")) {

				ELearningUser eLearningUser = new ELearningUser();
				eLearningUser.setFirstName(userDTO.getFirstName());
				eLearningUser.setLastName(userDTO.getLastName());
				eLearningUser.setEmailAddress(userDTO.getEmailAddress());
				eLearningUser.setFacebookUserId(userDTO.getFacebookUserId());
				eLearningUser.setProfilePhotoUrl(userDTO.getProfilePhotoUrl());
				UserCredential userCredential = new UserCredential();
				userCredential.setUsername(userDTO.getEmailAddress());
				userCredential.setPassword(passwordEncoder.encode("lX7oOrFoU7owJ05qZ2vM"));
				eLearningUser.setCredential(userCredential);

				UserGroup regularUserGroup = userGroupRepository.findByName("Regular");
				if (regularUserGroup != null) {
					eLearningUser.addGroup(regularUserGroup);
				}

				ELearningUser success = eLearningUserRepository.save(eLearningUser);

				JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken("", appSecret,
						success.getEmailAddress(), "lX7oOrFoU7owJ05qZ2vM");

				Authentication authentication = authenticationProvider.authenticate(authenticationToken);
				JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) authentication;

				String token = authenticatedToken.getToken();
				response.put("token", token);
				response.put("firstName", authenticatedToken.getFirstName());
				response.put("lastName", authenticatedToken.getLastName());
				response.put("id", authenticatedToken.getId());

				return ResponseEntity.ok(response);

			}
		} else {
			JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken("", appSecret, user.getUsername(),
					"lX7oOrFoU7owJ05qZ2vM");

			Authentication authentication = authenticationProvider.authenticate(authenticationToken);
			JwtAuthenticationToken authenticatedToken = (JwtAuthenticationToken) authentication;

			String token = authenticatedToken.getToken();
			response.put("token", token);
			response.put("firstName", authenticatedToken.getFirstName());
			response.put("lastName", authenticatedToken.getLastName());
			response.put("id", authenticatedToken.getId());

			return ResponseEntity.ok(response);
		}
		return ResponseEntity.badRequest().body("There was an error while logging you in.");
	}

	@RequestMapping(value = "/api/login/fb", method = { RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> fbLogin(HttpServletRequest request) throws Exception {
		StringBuilder sb = new StringBuilder();
		// sb.append(token);
		return ResponseEntity.ok(sb.toString());
	}

	/**
	 * Get new token with new expiration date.
	 *
	 * @param appSecret
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/api/refreshToken", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> login(@RequestParam(required = false, value = "exp") Integer expiration,
			@RequestHeader(value = "OT4-APP-SECRET") String appSecret,
			@RequestHeader(value = "OT4-USER-TOKEN") String token) {

		// Generate Json Web TokenHistory
		String newToken = JwtUtil.refreshToken(appSecret, token, expiration != null ? expiration : 15 * 60);
		Map<String, Object> response = new HashMap<>();
		response.put("token", newToken);

		return ResponseEntity.ok(response);
	}

	/**
	 *
	 * @param appSecret
	 * @param userToken
	 * @return
	 */
	@RequestMapping(value = "/api/logout", method = RequestMethod.GET)
	public ResponseEntity<?> logout(@RequestHeader(value = "OT4-APP-SECRET", required = false) String appSecret,
			@RequestHeader(value = "OT4-USER-TOKEN", required = false) String userToken) {

		if (appSecret == null || userToken == null) {
			return ResponseEntity.ok("");
		}

		Application application = applicationRepository.findByAppSecret(appSecret);
		Claims claims = JwtUtil.parseToken(appSecret, userToken);
		LOGGER.debug("Adding token {} to blacklist", userToken);
		TokenStore.addSubject(userToken, claims.getExpiration());

		SecurityEvent event = new SecurityEvent();
		event.setEvent(SecurityEvent.EventType.LOGOUT);
		event.setSubject(claims.getSubject());
		event.setExpirationDate(claims.getExpiration());

		LOGGER.debug("Sending websocket call for logout event...");
		webSocket.convertAndSend("/topic/security-event/" + application.getLink(), event);
		LOGGER.debug("websocket call for logout event SENT...");

		return ResponseEntity.ok("");
	}

	/**
	 * For testing if token is still valid
	 *
	 * @return
	 */
	@RequestMapping(value = "/api/test")
	public @ResponseBody ResponseEntity<?> testConnection() {
		Map<String, Object> result = new HashMap<>();
		result.put("result", "OK");
		return ResponseEntity.ok(result);
	}

	@RequestMapping(value = "/api/google", method = { RequestMethod.POST })
	//public ResponseEntity googleLogin(@RequestBody GooglelogRequest googleuser) {
		public ResponseEntity<CustomResponse> googleLogin(@RequestBody GooglelogRequest googleuser) {
		//GenericResponse<GooglelogRequest> res = new GenericResponse<GooglelogRequest>();

		GooglelogRequest request = new GooglelogRequest();
		CustomResponse response = new CustomResponse();
		Optional<BaseUser> userdetail = null;
		try {
			
			String email = googleuser.getEmail();
			System.out.println(email);
			userdetail = googleService.userdetaile(email);
			// String usermail =null;
			if (userdetail.isPresent() == true) {

			    
		        userdetail.get().setFirstName(googleuser.getFirstname());
				System.out.println("user detail " + userdetail);
				userdetail.get().setFirstName(googleuser.getFirstname());
				userdetail.get().setGoogleUserId(googleuser.getGoogleUserid());
				userdetail.get().setLastName(googleuser.getLastname());
				System.out.println("image path-->"+googleuser.getGooglidToken());
				//userdetail.get().setProfilePhotoUrl(googleuser.getImage());
				userdetail.get().setProfilePhotoUrl(googleuser.getImage());
				System.out.println("image path-->"+googleuser.getImage());
				
				userdetail.get().setGoogleidToken(googleuser.getGooglidToken());
				System.out.println("id token -->"+googleuser.getGooglidToken());
				
				userdetail.get().setProvider(googleuser.getProvider());
				userdetail.get().setArchived(true);
				response.setCode(200);
				response.setMessage("user update Successfully");
				userRepository.save(userdetail.get());

				
			} else {
				BaseUser learning = googleService.loginbyGoogle(googleuser);
				//return new ResponseEntity<String>(HttpStatus.OK).ok("User Created Successfully");
				response.setCode(200);
				response.setMessage("user login Successfully");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//return new ResponseEntity<String>(HttpStatus.OK).ok("User Updated Successfully");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
