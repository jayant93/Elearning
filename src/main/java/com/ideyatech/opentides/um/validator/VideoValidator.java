package com.ideyatech.opentides.um.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.gamify.elearning.entity.Video;
import com.gamify.elearning.repository.VideoRepository;
import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.core.util.ValidatorUtil;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.repository.ApplicationRepository;

/**
 * Validator for User object
 *
 * @author Gino
 */
@Component
public class VideoValidator implements Validator {
	
	@Autowired
	private VideoRepository videoRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Video.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
    	
    	Video video = (Video) target;
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(e, "title", 
    			"error.required", new Object[]{"Title"},"Title is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "description",
                "error.required", new Object[]{"Description"},"Description is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "tags",
                "error.required.at-least-one", new Object[]{"Tags"},"At least one tag is required.");
        
        // ValidationUtils.rejectIfEmptyOrWhitespace(e, "youtubeId",
        //         "error.required.video", new Object[]{"YoutubeId"},"There is no video attached.");

    }

}
