package com.gamify.elearning.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.Lesson;
import com.gamify.elearning.repository.ElementRepository;
import com.gamify.elearning.repository.LessonRepository;
import com.gamify.elearning.service.ElementService;
import com.ideyatech.opentides.um.repository.UserRepository;

/**
 * @author Gino
 */
@Service
public class ElementServiceImpl implements ElementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private ElementRepository elementRepository;

	@Override
	public Element associateElementToLesson(String elementId, String lessonId) {
		Lesson lesson = lessonRepository.findOne(lessonId);
		Element element = elementRepository.findOne(elementId);
		if(lesson != null && element != null) {
			element.setLesson(lesson);
			element.setOrdinal(lesson.getNumOfElements());
			Element updated = elementRepository.save(element);
			lesson.setNumOfElements(lesson.getNumOfElements() + 1);
			lessonRepository.save(lesson);
			return updated;
		}
		return null;
	}

 
}
