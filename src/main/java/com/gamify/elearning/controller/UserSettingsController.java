package com.gamify.elearning.controller;

import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.UserSettings;
import com.gamify.elearning.repository.*;
import com.ideyatech.opentides.core.entity.MessageResponse;
import com.ideyatech.opentides.core.util.CrudUtil;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@BasePathAwareController
//@RestController
@RequestMapping("/api/settings")
public class UserSettingsController extends BaseRestController<UserSettings> {

    @Autowired
	private ELearningUserRepository elearningUserRepository;

    @Autowired
	private UserSettingsRepository userSettingsRepository;

	@GetMapping("/user")
	public @ResponseBody ResponseEntity<?> getUserSettings(HttpServletRequest request) {
        String userId = SecurityUtil.getJwtSessionUser().getId();

		UserSettings settings = userSettingsRepository.findSettingsByUser(userId);

        return ResponseEntity.ok(settings);
	}

	@PostMapping("/user/add")
	public @ResponseBody ResponseEntity<?> createUserSettings(@RequestBody UserSettings settings, HttpServletRequest request) {
		String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);

		UserSettings success = null;
		if(settings.getId() == null || settings.getId().isEmpty()) {
			settings.setUser(user);
			success = userSettingsRepository.save(settings);
			if (success != null) {
				return ResponseEntity.ok(success);
			}
		} else {
			UserSettings userSettings = userSettingsRepository.findOne(settings.getId());
			if(userSettings != null) {
				userSettings.setThemeName(settings.getThemeName());
				success = userSettingsRepository.save(userSettings);
				if(success != null) {
					return ResponseEntity.ok(success);
				}
			}
		}
		return new ResponseEntity<String>("Could not save user settings.", HttpStatus.INTERNAL_SERVER_ERROR);

	}
}