package com.ideyatech.opentides.um.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamify.elearning.entity.Company;
import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.repository.CompanyRepository;
import com.gamify.elearning.repository.CourseRepository;
import com.gamify.elearning.repository.ELearningUserRepository;
import com.gamify.elearning.repository.projection.CourseProjection;
import com.gamify.elearning.service.ElearningFileUploadService;
import com.ideyatech.opentides.core.entity.MessageResponse;
import com.ideyatech.opentides.core.entity.StringList;
import com.ideyatech.opentides.core.entity.Template;
import com.ideyatech.opentides.core.exception.InvalidImplementationException;
import com.ideyatech.opentides.core.repository.AuditLogRepository;
import com.ideyatech.opentides.core.repository.TemplateRepository;
import com.ideyatech.opentides.core.security.JwtAuthenticatedUser;
import com.ideyatech.opentides.core.service.MailingService;
import com.ideyatech.opentides.core.util.CrudUtil;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.BaseUserCustomValue;
import com.ideyatech.opentides.um.entity.SessionUser;
import com.ideyatech.opentides.um.entity.UserCredential;
import com.ideyatech.opentides.um.entity.UserCustomValuesHelper;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.event.ChangePasswordEvent;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import com.ideyatech.opentides.um.repository.UserCustomFieldRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.repository.cb.UserCbRepository;
import com.ideyatech.opentides.um.repository.cb.UserGroupCbRepository;
import com.ideyatech.opentides.um.repository.jpa.ApplicationJpaRepository;
import com.ideyatech.opentides.um.repository.mongo.UserMongoRepository;
import com.ideyatech.opentides.um.repository.projections.BaseUserProjection;
import com.ideyatech.opentides.um.repository.projections.UserDropdownProjection;
import com.ideyatech.opentides.um.service.DivisionService;
import com.ideyatech.opentides.um.service.UserService;
import com.ideyatech.opentides.um.util.SecurityUtil;
import com.ideyatech.opentides.um.validator.UserValidator;

/**
 * Created by Gino on 8/31/2016.
 */

@BasePathAwareController
@RequestMapping(value = "/api/user")
public class UserController extends BaseRestController<BaseUser> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DivisionService divisionService;
    
    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RepositoryEntityLinks entityLinks;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ApplicationEventPublisher publisher;
    
    @Autowired
    private MailingService mailingService;

    @Autowired
    private TemplateRepository templateRepository;
    
	@Value("${mail.server.domain}")
	private String host;
	
	@Value("${mail.server.port}")
	private String port;
	
	@Value("${mail.email-template}")
	private String emailTemplate;
	
	@Value("${mail.reset-email-template}")
	private String resetEmailTemplate;

	@Value("${upload.path}")
	private String uploadPath;
	
    @Autowired
    private UserCustomFieldRepository userCustomFieldRepository;
    
    @Autowired
	private ELearningUserRepository eLearningUserRepository;
    
    @Autowired
    private ElearningFileUploadService elearningFileUploadService;

    @Autowired
    private CompanyRepository companyRepository;
    
    
	@Autowired
	private CourseRepository courseRepository;
	
    
	@Autowired
	ApplicationJpaRepository applicationrepo;
	
    /**
     *
     * @param baseUser
     * @return
     */
    @Transactional
    @RequestMapping(value = {"/register", "/registerByUser"}, method = RequestMethod.POST, consumes="application/json")
    public @ResponseBody ResponseEntity<?> registerUser(
            @RequestBody ELearningUser eLearningUser, BindingResult bindingResult,
            @RequestHeader(value = "OT4-APP-SECRET") String appSecret,
            @RequestParam(value = "fromMobile", required=false, defaultValue = "false") boolean fromMobile,
            HttpServletRequest request) {
    	
    	LOGGER.info("Registering User");
    
    	
        Application application = applicationRepository.findByAppSecret(appSecret);

        LOGGER.debug("Request URI : {}", request.getRequestURI());
        if(request.getRequestURI().contains("/registerByUser")) {
            eLearningUser.getCredential().setEnabled(false);
        }

         if(request.getRequestURI().contains("/registerByUser")) {
            eLearningUser.getCredential().setEnabled(false);
        }

        String companyName = eLearningUser.getCompanyTitle();
        String websiteUrl = eLearningUser.getWebsiteUrl();
        List<Company> companies = companyRepository.findCompanyWithSimilarName(companyName);
        
        if(companies.size() > 0) {
            eLearningUser.setElearningCompany(companies.get(0));
        } else {
            Company c = new Company();
            c.setName(companyName);
            c.setWebsiteUrl(websiteUrl);
            Company success = companyRepository.save(c);
            eLearningUser.setElearningCompany(success);
        }

      eLearningUser.setPasswordRule(application.getPasswordRules().buildCharacterRules());
        UserCredential credential = eLearningUser.getCredential();

        //Specific for couchbase implementation
        if(eLearningUser.getUserGroupIds() != null && userGroupRepository instanceof UserGroupCbRepository) {
            for(String id : eLearningUser.getUserGroupIds()) {
                UserGroup userGroup = (UserGroup)userGroupRepository.findOne(id);
                eLearningUser.addGroup(userGroup);
            }
        }

        UserGroup regularUserGroup = userGroupRepository.findByName("Regular");
        if(regularUserGroup != null) {
            eLearningUser.addGroup(regularUserGroup);
        }

        userValidator.validate(eLearningUser, bindingResult);
        if(bindingResult.hasErrors()) {
            List<MessageResponse> messageResponses =
                    CrudUtil.convertErrorMessage(bindingResult, request.getLocale(), messageSource);
            return ResponseEntity.badRequest().body(messageResponses);
        }

        String password;
        if(!StringUtil.isEmpty(credential.getNewPassword())) {
            password = credential.getNewPassword();
        } else {
            password = JwtUtil.generateSecretKey(8);
        }

        /**
         * This means the user registered using his Facebook credentials.
         * Creates fake password via encoded facebookUserId.
         * - AJ
         */
        if(!StringUtil.isEmpty(eLearningUser.getFacebookUserId()) && eLearningUser.getFacebookUserId().equals(password)) {
        	password = "lX7oOrFoU7owJ05qZ2vM";
        }

        if(!StringUtil.isEmpty(eLearningUser.getGoogleUserId())) {
            password = "mKcMmwN9nVmQG7MABuU7";
        }
        
        String encodedPass = passwordEncoder.encode(password);
        eLearningUser.getCredential().setPassword(encodedPass);
        
        if(application.getSendActivationEmail() != null && application.getSendActivationEmail()) {
            //Generate random activation key...
        	String randomActivationKey = "";
        	if(fromMobile == false) {
        		randomActivationKey = JwtUtil.generateSecretKey(32);
        	} else {
        		randomActivationKey = JwtUtil.generateSecretKey(4);
        	}
            eLearningUser.setActivationVerificationKey(randomActivationKey);
            //TODO generate email here
            Map<String, Object> user = new HashMap<String, Object>();
            user.put("name", eLearningUser.getCompleteName());
            user.put("verificationKey", eLearningUser.getActivationVerificationKey());
            
            try {
                mailingService.sendEmail(application.getName(), application.getEmailAddress(), new String[]{eLearningUser.getEmailAddress()}, 
        				"Please verify your account", emailTemplate, user);
                LOGGER.info("Sent verification code to user.");
            } catch (Exception e) {
				LOGGER.debug("Error in sending email to user. The error found was: " + e);
			}
        }
        eLearningUser.setApplication(application);
        eLearningUser.setTacAcceptedTs(new Date());
        eLearningUser.setArchived(false);

        publisher.publishEvent(new BeforeCreateEvent(eLearningUser));
        eLearningUserRepository.save(eLearningUser);
        publisher.publishEvent(new AfterCreateEvent(eLearningUser));
        /*Resource<BaseUser> resource = new Resource<>(eLearningUser);
        resource.add(entityLinks.linkToSingleResource(BaseUser.class, (eLearningUser.getId() != null)?eLearningUser.getId():eLearningUser.getNosqlId()));*/
        
        return ResponseEntity.ok("Successfully created user!");
    }

	/**
     * Activate a user
     * @param id
     * @return
     */
    @Transactional
    @RequestMapping(value = "/activate/{id}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> activateUser(@PathVariable String id) {
        return enableDisableUser(id, true);
    }
    
    @Transactional
    @RequestMapping(value = "/user-search/{type}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> divisionSearch(@RequestParam("page")int page, @RequestParam("size")int size,
    							@RequestBody HashMap<String, Object> body, @PathVariable String type) {
    	long startTime = System.currentTimeMillis();
    	Pageable pageable = new PageRequest(page, size);
    	Page<BaseUser> users = userRepository.search(type, body, pageable);
    	long endTime   = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	result.put("results", users);
    	result.put("searchTime", totalTime);
    	return ResponseEntity.ok(result);
    }        
    
    /**
     * Activate a user given verification key
     * @param randomActivationKey the activation key to check if the user activation is valid
     * @return
     */
    @Transactional
    @RequestMapping(value = "/activateByUser/{randomActivationKey}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> activateByUser(@PathVariable String randomActivationKey) {
        Map<String, Object> response = new HashMap<>();
        BaseUser user = userRepository.findByActivationVerificationKey(randomActivationKey);
        if(user == null) {
            response.put("message", "Invalid activation.");
            return ResponseEntity.badRequest().body(response);
        }
        user.getCredential().setEnabled(true);
        userRepository.save(user);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    /**
     * Disable a user
     * @param id
     * @return
     */
    @Transactional
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> disableUser(@PathVariable String id) {
        return enableDisableUser(id, false);
    }

    @Transactional
    @RequestMapping(value = "/archive/{id}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> archiveUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        BaseUser user = (BaseUser)userRepository.findOne(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        userRepository.archiveUser(id);
        logUserChange("User archived", user);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    @Transactional
    @RequestMapping(value = "/unarchive/{id}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> unarchiveUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        BaseUser user = (BaseUser)userRepository.findOne(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        userRepository.unarchiveUser(id);
        logUserChange("User unarchived", user);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    /**
     * Request for password reset.
     * @param usernameOrEmail
     *
     * @return
     */
    @Transactional
    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> requestForPasswordReset(
    		@RequestHeader(value = "OT4-APP-SECRET") String appSecret,
            @RequestParam(value = "u") String usernameOrEmail,
            @RequestParam(value = "fromMobile", required=false, defaultValue = "false") boolean fromMobile,
            @RequestParam(value = "f", required = false) String formLink,
            HttpServletRequest request) {
    	
    	Application application = applicationRepository.findByAppSecret(appSecret);

        Map<String, Object> response = new HashMap<>();
        BaseUser user = userRepository.findByUsernameOrEmail(usernameOrEmail);

        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

      //Generate random activation key...
    	String resetPasswordKey = "";
    	if(fromMobile == false) {
    		resetPasswordKey = JwtUtil.generateSecretKey(32);
    	} else {
    		resetPasswordKey = JwtUtil.generateSecretKey(4);
    	}
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("name", user.getCompleteName());
        recipient.put("resetPasswordKey", resetPasswordKey);
        if(formLink != null)
            recipient.put("resetPasswordUrl", formLink);

        user.setResetPasswordKey(resetPasswordKey);
        userRepository.save(user);
        
        try {
            Template template = templateRepository.findByKey("TEMPLATE_FORGOT_PASSWORD");
            if(template != null) {
                mailingService.sendEmail(application.getName(), application.getEmailAddress(), new String[]{user.getEmailAddress()},
                        "Reset Password", "TEMPLATE_FORGOT_PASSWORD", template.getBody(), recipient);
            } else {
                mailingService.sendEmail(application.getName(), application.getEmailAddress(), new String[]{user.getEmailAddress()},
                        "Reset Password", resetEmailTemplate, recipient);
            }
            LOGGER.info("Sent reset key to user.");
        } catch (Exception e) {
			LOGGER.debug("Error in sending email to user. The error found was: " + e);
		}

        //Audit Logging...
        String ipAddress = request.getRemoteAddr();
        auditLogRepository.logEvent(
            String.format("Requested password reset from IP address %s for user %s", ipAddress, usernameOrEmail), user);

        return ResponseEntity.ok(response);
    }
    
    @Transactional
    @RequestMapping(value = "/change-password/{id}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> changePassword(@PathVariable String id,
    		@RequestBody Map<String, String> body) {
    	
    	String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");

        BaseUser user = findUser(id);
        Map<String, Object> response = new HashMap<>();
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        if(!newPassword.equals(confirmPassword)) {
            response.put("message", "Passwords don't match.");
            return ResponseEntity.badRequest().body(response);
        }
        if(!SecurityUtil.currentUserHasPermission("CHANGE_USER_PASSWORD")) {
            if (!passwordEncoder.matches(currentPassword, user.getCredential().getPassword())) {
                response.put("message", "Wrong current password.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        Object userObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JwtAuthenticatedUser authenticatedUser = (JwtAuthenticatedUser)userObj;
        String idToCompare;
        if(com.ideyatech.opentides.um.Application.getEntityIdType().equals(String.class)) {
            idToCompare = authenticatedUser.getNosqlId();
        } else {
            idToCompare = "" + authenticatedUser.getId();
        }
        if(!id.equals(idToCompare) && !SecurityUtil.currentUserHasPermission("CHANGE_USER_PASSWORD")) {
            response.put("message", "Request is invalid.");
            return ResponseEntity.badRequest().body(response);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.getCredential().setPassword(encodedPassword);
        userRepository.save(user);
        publisher.publishEvent(new ChangePasswordEvent(user, newPassword));

        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/check-user-exists", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> checkIfUserExists(@RequestBody HashMap<String, Object> params) {
        String email = (String) params.get("email");

        BaseUser baseUser = userRepository.findByEmailAddress(email);
        if (baseUser == null) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(true);
    }

    @RequestMapping(value = "/check-fb-user-exists", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> checkIfFbUserExists(@RequestBody HashMap<String, Object> params) {
        String email = (String) params.get("email");
        String facebookUserId = (String) params.get("facebookUserId");

        BaseUser baseUser = userRepository.findByEmailAddressAndFacebookUserId(email, null);
        if (baseUser == null) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(true);
    }

	/*
	 * @Transactional
	 * 
	 * @RequestMapping(value = "/change-password/{id}", method = RequestMethod.POST)
	 * public @ResponseBody ResponseEntity<?> changePassword(@PathVariable String
	 * id,
	 * 
	 * @RequestParam(value = "cur", required = false) String currentPassword,
	 * 
	 * @RequestParam("new") String newPassword,
	 * 
	 * @RequestParam("con") String confirmPassword) {
	 * 
	 * System.out.println("Passwords: " + currentPassword + " " + newPassword + " "
	 * + confirmPassword);
	 * 
	 * BaseUser user = findUser(id); Map<String, Object> response = new HashMap<>();
	 * if(user == null) { response.put("message", "User not found."); return
	 * ResponseEntity.badRequest().body(response); }
	 * if(!newPassword.equals(confirmPassword)) { response.put("message",
	 * "Passwords don't match."); return ResponseEntity.badRequest().body(response);
	 * } if(!SecurityUtil.currentUserHasPermission("CHANGE_USER_PASSWORD")) { if
	 * (!passwordEncoder.matches(currentPassword,
	 * user.getCredential().getPassword())) { response.put("message",
	 * "Wrong current password."); return
	 * ResponseEntity.badRequest().body(response); } }
	 * 
	 * Object userObj =
	 * SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	 * JwtAuthenticatedUser authenticatedUser = (JwtAuthenticatedUser)userObj;
	 * String idToCompare;
	 * if(com.ideyatech.opentides.um.Application.getEntityIdType().equals(String.
	 * class)) { idToCompare = authenticatedUser.getNosqlId(); } else { idToCompare
	 * = "" + authenticatedUser.getId(); } if(!id.equals(idToCompare) &&
	 * !SecurityUtil.currentUserHasPermission("CHANGE_USER_PASSWORD")) {
	 * response.put("message", "Request is invalid."); return
	 * ResponseEntity.badRequest().body(response); }
	 * 
	 * String encodedPassword = passwordEncoder.encode(newPassword);
	 * user.getCredential().setPassword(encodedPassword); userRepository.save(user);
	 * publisher.publishEvent(new ChangePasswordEvent(user, newPassword));
	 * 
	 * response.put("message", ""); return ResponseEntity.ok(response); }
	 */

    @Transactional
    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> resetPassword(
            @RequestParam String newPassword,
            @RequestParam String resetPasswordKey,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = userRepository.findByResetPasswordKey(resetPasswordKey);
        String encodedPassword = passwordEncoder.encode(newPassword);

        if(user == null) {
            response.put("message", "Your request to reset password is invalid.");
            return ResponseEntity.badRequest().body(response);
        }

        Application application = user.getApplication();
        if(application != null && application.getPasswordRules() != null) {
            Integer maxTimes = application.getPasswordRules().getPasswordRepeat();
            if (maxTimes != null && maxTimes > 0) {
                if (!userService.isPasswordCanBeReused(user, encodedPassword, maxTimes)) {
                    response.put("message", String.format("You cannot reuse your last %d passwords.", maxTimes));
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }

        user.setResetPasswordKey(null);
        user.getCredential().setPassword(encodedPassword);
        userRepository.save(user);

        //Audit Logging...
        String ipAddress = request.getRemoteAddr();
        SessionUser sessionUser = SecurityUtil.getSessionUser();
        StringBuilder sb = new StringBuilder("Password successfully changed from IP " + ipAddress);
        if(sessionUser != null) {
            user.setAuditUserId(sessionUser.getId());
            user.setAuditUsername(sessionUser.getUsername());
            sb.append("by user ")
                .append(sessionUser.getUsername());
        } else {
            sb.append("using reset password key ")
                .append(resetPasswordKey);
        }
        auditLogRepository.logEvent(sb.toString(), user);

        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    @Transactional
    @RequestMapping(value = "/updateBySelectedProperty/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateBySelectedProperty(@PathVariable String id, @RequestBody HashMap<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        ELearningUser user = eLearningUserRepository.findOne(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean emailUpdated = params.get("emailAddress") != null;
        String emailParam = (String) params.get("emailAddress");
        Long emailCount = this.userRepository.countByEmailAddress(emailParam);
        boolean emailModified = !(user.getEmailAddress().equals(emailParam));
        boolean emailUnique =  (emailCount == 0L);
        
        if ((emailUpdated && ( emailModified && !emailUnique))) {
        	response.put("message", "Email address already exists.");
        	return ResponseEntity.badRequest().body(response);
        }

        boolean companyUpdated = params.get("companyName") != null;

        if (companyUpdated) {
            String companyName = (String) params.get("companyName");
            String websiteUrl = (String) params.get("websiteUrl");
            List<Company> companies = companyRepository.findCompanyWithSimilarName(companyName);
            if (companies.size() > 0) {
                user.setElearningCompany(companies.get(0));
            } else {
                Company company = new Company();
                company.setName(companyName);
                company.setWebsiteUrl(websiteUrl);
                Company success = companyRepository.save(company);
                user.setElearningCompany(success);
            }
        }
     
        try {
            CrudUtil.copyPropertiesFromMap(user, params, BaseUser.class);
//            CrudUtil.copyPropertiesFromMap(user.getCredential() , params, UserCredential.class);
			/*
			 * if (user.getCredential().getStatus().equals("Pending")) {
			 * user.getCredential().setPassword(passwordEncoder.encode(user.getCredential().
			 * getTempPassword())); }
			 */
            
            this.userRepository.save(user);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        response.put("message", "Fields updated");
        return ResponseEntity.ok(response);
    }

    /**
     * Update custom values of a user.
     *
     * @see com.ideyatech.opentides.core.entity.CustomField
     * @param id
     * @param list
     * @return
     */
    @Transactional
    @RequestMapping(value = "/updateCustomValues/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateCustomValues(@PathVariable String id,
                                                 @RequestBody UserCustomValuesHelper.UserCustomValuesHelperList list) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, BaseUserCustomValue> customValues = user.getCustomValuesMap();
        if(customValues == null) {
            customValues = new HashMap<>();
            user.setCustomValuesMap(customValues);
        }
        for(UserCustomValuesHelper valuesHelper : list) {
            String key = valuesHelper.getCustomFieldKey();
            BaseUserCustomValue cvs = customValues.get(key);
            if(cvs == null) {
                cvs = new BaseUserCustomValue();
                cvs.setCustomFieldKey(key);
                cvs.setCustomField(userCustomFieldRepository.findByKey(key));
                customValues.put(key, cvs);
            }
            cvs.setValue(valuesHelper.getValue());
        }
        userRepository.save(user);

        response.put("customFields", customValues);
        return ResponseEntity.ok(response);
    }

    /**
     * Add this user group to the current user
     *
     * @param id
     * @param groupNames
     * @return
     */
    @Transactional
    @RequestMapping(value = "/updateUserGroups/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserGroups(@PathVariable String id,
                                           @RequestBody StringList groupNames) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

        user.getGroups().clear();
        for(String name : groupNames) {
            UserGroup ug = userGroupRepository.findByName(name);
            if(ug != null) {
                user.addGroup(ug);
            }
        }
        userRepository.save(user);
        response.put("message", "ok");
        return ResponseEntity.ok(response);
    }

    /**
     * Update user group to the current user
     *
     * @param id
     * @param groupNames
     * @return
     */
    @Transactional
    @RequestMapping(value = "/addUserGroup/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> addUserGroups(@PathVariable String id,
                                           @RequestBody StringList groupNames) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        for(String name : groupNames) {
            UserGroup ug = userGroupRepository.findByName(name);
            if(ug != null) {
                user.addGroup(ug);
            }
        }
        userRepository.save(user);
        response.put("message", "ok");
        return ResponseEntity.ok(response);
    }


    /**
     * Update user groups
     * @param id
     * @param addedGroups
     * @param removedGroups
     * @param request
     * @return
     */
    @Transactional
    @RequestMapping(value = "/updateUserGroup/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserGroup(@PathVariable String id,
                                                @RequestParam(required = false) StringList addedGroups,
                                                @RequestParam(required = false) StringList removedGroups, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

        if(addedGroups != null){
            for(String name : addedGroups) {
                UserGroup ug = userGroupRepository.findByName(name);
                if(ug != null) {
                    user.addGroup(ug);
                }
            }
        }

        if(removedGroups != null){
            for(String name : removedGroups) {
                UserGroup ug = userGroupRepository.findByName(name);
                if(ug != null)
                    user.removeGroupByName(name);

            }
        }

        userRepository.save(user);

        response.put("message", "ok");
        response.put("data", user.getGroups());

        return ResponseEntity.ok(response);
    }


    /**
     *
     * @param id
     * @param baseUser
     * @return
     */
    @Transactional
    @RequestMapping(value = "/updateUserDivisions/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateDivisions(@PathVariable String id,
                                             @RequestBody BaseUser baseUser) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

        user.setDivisions(baseUser.getDivisions());
        user.setHomeDepartment(baseUser.getHomeDepartment());
        user.setHomeSection(baseUser.getHomeSection());
        userRepository.save(user);

        response.put("message", "ok");
        response.put("data", user.getGroups());

        return ResponseEntity.ok(response);
    }

    /**
     * Set the group of this user to the specified group only. Use this if your application only
     * supports one group to one user
     *
     * @param id
     * @param groupName
     * @return
     */
    @Transactional
    @RequestMapping(value = "/setSingleGroup/{id}/{groupName}", method = RequestMethod.POST)
    public ResponseEntity<?> setSingleGroup(@PathVariable String id, @PathVariable String groupName) {

        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        UserGroup ug = userGroupRepository.findByName(groupName);
        if(ug == null) {
            response.put("message", "Group not found.");
            return ResponseEntity.badRequest().body(response);
        }
        Set<UserGroup> ugs = new HashSet<>();
        ugs.add(ug);
        user.setGroups(ugs);
        userRepository.save(user);
        response.put("message", "ok");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/findByUsernames", method = RequestMethod.GET)
    public ResponseEntity<?> findByUsernames(@RequestParam StringList usernames) {

        if(userRepository instanceof UserCbRepository) {
            UserCbRepository userCbRepository = (UserCbRepository)userRepository;
            List<BaseUser> users = userCbRepository.findByUsernames(usernames.toArray(new String[]{}));
            List<BaseUserProjection> projected = new ArrayList<>();
            for (BaseUser user : users) {
                projected.add(projectionFactory.createProjection(BaseUserProjection.class, user));
            }
            return ResponseEntity.ok(projected);
        }if(userRepository instanceof UserMongoRepository) {
            UserMongoRepository userMongoRepository = (UserMongoRepository)userRepository;
            List<BaseUser> users = userMongoRepository.findByUsernames(usernames.toArray(new String[]{}));
            List<BaseUserProjection> projected = new ArrayList<>();
            for (BaseUser user : users) {
                projected.add(projectionFactory.createProjection(BaseUserProjection.class, user));
            }
            return ResponseEntity.ok(projected);
        }
        else {
            throw new RuntimeException("Not yet implemented for JPA");
        }
    }

    @RequestMapping(value = "/findByOrgUnit", method = RequestMethod.GET)
    public ResponseEntity<?> findByOrgUnit(@RequestParam String orgUnit) {
        List<BaseUser> users = userRepository.findByOrgUnit(orgUnit);
        List<UserDropdownProjection> projected = new ArrayList<>();
        for (BaseUser user : users) {
            projected.add(projectionFactory.createProjection(UserDropdownProjection.class, user));
        }
        return ResponseEntity.ok(projected);
    }

    @RequestMapping(value = "/findByAccessRights", method = RequestMethod.GET)
    public ResponseEntity<?> findByAccessRights() {
        JwtAuthenticatedUser currentUser = SecurityUtil.getJwtSessionUser();
        List<UserDropdownProjection> projected = new ArrayList<>();

        /*Access All Users*/
        if (SecurityUtil.currentUserHasPermission("ACCESS_ALL")) {
            Sort sort = new Sort(Sort.Direction.ASC, "firstName");
            Iterable<BaseUser> allUsers = userRepository.findAll(sort);
            for (BaseUser user : allUsers) {
                projected.add(projectionFactory.createProjection(UserDropdownProjection.class, user));
            }
        }
        /*Access All Users under Current User Divisions and Home Department*/
        else if (SecurityUtil.currentUserHasPermission("ACCESS_DIVISION")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> accessFilter = userService.getUserAccess(currentUser.getUsername());
                String orgUnitStringArray = objectMapper.writeValueAsString(accessFilter.get("ORGUNIT_KEYS"));
                List<BaseUser> orgUnitsUsers = userRepository.findByOrgUnitList(orgUnitStringArray);
                for (BaseUser user : orgUnitsUsers) {
                    projected.add(projectionFactory.createProjection(UserDropdownProjection.class, user));
                }
            } catch (JsonProcessingException e) {
                LOGGER.error("Error encountered while processing org units", e);
            }
        }
        /*Access Own Data only*/
        else if (SecurityUtil.currentUserHasPermission("ACCESS_OWN")) {
            BaseUser ownUser = findUser(currentUser.getNosqlId());
            if(ownUser != null){
                projected.add(projectionFactory.createProjection(UserDropdownProjection.class, ownUser));
            }
        }

        return ResponseEntity.ok(projected);
    }
    
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public ResponseEntity<?> findall() {
		
    	return ResponseEntity.ok(userRepository.findAll());
	}

    @RequestMapping(value = "/findByIds", method = RequestMethod.GET)
    public ResponseEntity<?> findByIds(@RequestParam StringList ids) {
        List<BaseUserProjection> projected = new ArrayList<>();
        for(String id : ids) {
            projected.add(projectionFactory.createProjection(BaseUserProjection.class, findUser(id)));
        }
        return ResponseEntity.ok(projected);
    }
    

    /**
     * Returns current user profile
     * 
     * @param appSecret
     * @param request
     * @return
     */
    @RequestMapping(value = "/profile/current-user", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> currentUser(@RequestHeader(value = "OT4-APP-SECRET") String appSecret,
    		HttpServletRequest request) {

    	String id = SecurityUtil.getJwtSessionUser().getId();
    	
        BaseUserProjection user = projectionFactory.createProjection(BaseUserProjection.class,
        		findUser(id.toString()));
        
        return ResponseEntity.ok(user);
    }

    @PostMapping("/profile/upload-photo")
    public @ResponseBody ResponseEntity<?> uploadProfilePhoto(@RequestHeader(value = "OT4-APP-SECRET") String appSecret, @RequestParam("file") MultipartFile multipartFile,
    		HttpServletRequest request) {
    	File file;
		try {
			file = convert(multipartFile);
	    	String url = elearningFileUploadService.upload("user/profile-image/" + UUID.randomUUID(), file);
	    	if(url == null || url.isEmpty()) {
	            return ResponseEntity.badRequest().body("No photo found.");
	        }
	    	JwtAuthenticatedUser currentUser = SecurityUtil.getJwtSessionUser();
	    	ELearningUser user = eLearningUserRepository.findOne(currentUser.getId());
	    	user.setProfilePhotoUrl(url);
	    	ELearningUser success = eLearningUserRepository.save(user);
	    	return ResponseEntity.ok(success);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Could not change profile image.");
		}
    }
    
    @PostMapping("/profile/save-photo")
    public @ResponseBody ResponseEntity<?> saveProfilePhoto(@RequestHeader(value = "OT4-APP-SECRET") String appSecret,  @RequestBody HashMap<String, Object> params,
    		HttpServletRequest request) {
    	String profilePhotoUrl = params.get("profilePhotoUrl").toString();
    	if(profilePhotoUrl == null || profilePhotoUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("No photo found.");
        }
    	JwtAuthenticatedUser currentUser = SecurityUtil.getJwtSessionUser();
    	ELearningUser user = eLearningUserRepository.findOne(currentUser.getId());
    	user.setProfilePhotoUrl(profilePhotoUrl);
    	ELearningUser success = eLearningUserRepository.save(user);
    	return ResponseEntity.ok(success);
    }

    @GetMapping("/count")
    public @ResponseBody ResponseEntity<?> countUsers() {
        return ResponseEntity.ok(eLearningUserRepository.count());
    }

    @GetMapping("/countpermonth")
    public @ResponseBody ResponseEntity<?> countUsersPerMonth() {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.MONTH, -12);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        Date monthsAgo12 = cal.getTime();

        List<Map<String, Object>> usersPerMonth = eLearningUserRepository.getUserCountPerMonth(monthsAgo12, today);

        return ResponseEntity.ok(usersPerMonth);
    }

    /**
     * Enable or disable a user
     *
     * @param id ID of the user
     * @param enable true to enable, false otherwise
     * @return
     */
    private ResponseEntity<?> enableDisableUser(String id, boolean enable) {
        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(id);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        user.getCredential().setEnabled(enable);
        userRepository.save(user);

        logUserChange(String.format("%s user", enable ? "Enabled" : "Disabled"), user);

        response.put("message", "");
        return ResponseEntity.ok(response);
    }


    /**
     * Check if user have permission with given auths
     * If match all is true then will only return true if user have permission to all
     * @param auths comma separated array of auths
     * @param matchAll
     * @return
     */
    @RequestMapping(value = "/hasAuthorities", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> hasAuthorities(
            @RequestParam String auths, @RequestParam(required = false) Boolean matchAll) {
        Map<String, Object> result = new HashMap<>();
        String [] authsArr = auths.split(",");

        if(matchAll != null && matchAll) {
            boolean hasAuth = true;
            for(String auth : authsArr) {
                if(!SecurityUtil.currentUserHasPermission(auth)) {
                    hasAuth = false;
                    break;
                }
            }
            result.put("hasAccess", hasAuth);
        } else {
            for(String auth : authsArr) {
                if(SecurityUtil.currentUserHasPermission(auth)) {
                    result.put(auth, true);
                }
            }
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * Retrieves the ACCESS rights of the current user.
     * @return
     */
    @RequestMapping(value = "/getAccessFilter", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccessFilter() {
    		JwtAuthenticatedUser user = SecurityUtil.getJwtSessionUser();
    		Map<String, Object> result = userService.getUserAccess(user.getUsername());
    		// remove ORGUNITS and use only ORGUNIT_KEYS
    		Map<String, Object> result2 = new HashMap<String, Object>();
    		for (String key: result.keySet()) {
    			if (!"ORGUNITS".equals(key))
    				result2.put(key, result.get(key));
    		}
       	return ResponseEntity.ok(result2);
    }

    /**
     * Retrieves the ACCESS rights of the current user.
     * This does not include the child division.
     * @return
     */
    @RequestMapping(value = "/getAccessFilterNoChildDivision", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getAccessFilterNoChildDivision() {
    		JwtAuthenticatedUser user = SecurityUtil.getJwtSessionUser();
    		Map<String, Object> result = userService.getUserAccessNoChildDivision(user.getUsername());
    		// remove ORGUNITS and use only ORGUNIT_KEYS
    		Map<String, Object> result2 = new HashMap<String, Object>();
    		for (String key: result.keySet()) {
    			if (!"ORGUNITS".equals(key))
    				result2.put(key, result.get(key));
    		}
       	return ResponseEntity.ok(result2);
    }

    @RequestMapping(value = "/customValue/{userId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> updateCustomValue(@PathVariable String userId,
                                             @RequestParam(value = "k") String key,
                                             @RequestParam(value = "v", required = false) String value) {
        Map<String, Object> response = new HashMap<>();
        BaseUser user = findUser(userId);
        if(user == null) {
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
        if(StringUtil.isEmpty(value)) {
            user.removeCustomValue(key);
        } else {
            user.addCustomValue(key, value);
        }
        userRepository.save(user);

        response.put("customValues", user.getCustomValuesMap());
        return ResponseEntity.ok(response);
    }

    private BaseUser findUser(String id) {
        BaseUser user;
        //TODO very ugly implementation. Find a better way.
        if(com.ideyatech.opentides.um.Application.getEntityIdType().equals(String.class)) {
            user = (BaseUser) userRepository.findOne(id);
        } else {
            throw new InvalidImplementationException(String.format("ID type %s not yet supported",
                    com.ideyatech.opentides.um.Application.getEntityIdType().toString()));
        }
        return  user;
    }

    private void logUserChange(String message, BaseUser user) {
        SessionUser sessionUser = SecurityUtil.getSessionUser();
        if (sessionUser != null) {
            user.setAuditUserId(sessionUser.getId());
            user.setAuditUsername(sessionUser.getUsername());
        }
        auditLogRepository.logEvent(message, user);
    }
    
    public File convert(MultipartFile file) throws IOException {
		File convFile = new File(uploadPath + "/" + file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseById(@PathVariable String id) {
		Course course = null;
		//List<Course> courseList = courseRepository.getCourseByIdNotDeleted(id);
		List<Course> courseList = courseRepository.findbyid(id);
		if(courseList.size() > 0) course = courseList.get(0);
        CourseProjection success = projectionFactory.createProjection(CourseProjection.class, course);
		return ResponseEntity.ok(success);
	}
    
    @RequestMapping(value = "userId/{id}", method = RequestMethod.GET)
   	public @ResponseBody ResponseEntity<?> getCourseByuserid(@PathVariable String id) {
   		Course course = null;
   		//List<Course> courseList = courseRepository.getCourseByIdNotDeleted(id);
   		List<Course> courseList = courseRepository.findbyuserid(id);
   		if(courseList.size() > 0) course = courseList.get(0);
           CourseProjection success = projectionFactory.createProjection(CourseProjection.class, course);
   		   return ResponseEntity.ok(success);
   	}
    
    @PostMapping("adminApp")
	public @ResponseBody ResponseEntity<?> admin(@RequestBody Application course, BindingResult bindingResult,
			HttpServletRequest request) {
		Application appObj=	 applicationrepo.save(course);
				 return new ResponseEntity<>(appObj, HttpStatus.OK);
		
	}
    
    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {
       return principal.getName();
    }
}
