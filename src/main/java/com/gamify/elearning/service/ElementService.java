package com.gamify.elearning.service;

import com.gamify.elearning.entity.Element;

/**
 * @author johanna@ideyatech.com.
 */
public interface ElementService {

	Element associateElementToLesson(String elementId, String lessonId);

}
