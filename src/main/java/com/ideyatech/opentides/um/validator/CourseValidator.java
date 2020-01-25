package com.ideyatech.opentides.um.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.Video;
import com.gamify.elearning.repository.CourseRepository;
import com.ideyatech.opentides.um.repository.ApplicationRepository;

/**
 * Validator for Course object
 *
 * @author johanna@ideyatech.com
 */
@Component
public class CourseValidator implements Validator {
	
	@Autowired
	private CourseRepository courseRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Video.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
    	
    	Course course = (Course) target;
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(e, "title", 
    			"error.required", new Object[]{"Title"},"Title is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "description",
                "error.required", new Object[]{"Description"},"Description is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "tags",
                "error.required.at-least-one", new Object[]{"Tags"},"At least one tag is required.");

    }

}
