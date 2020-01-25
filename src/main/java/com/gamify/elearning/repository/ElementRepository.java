package com.gamify.elearning.repository;

import java.util.List;

import com.gamify.elearning.entity.Element;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface ElementRepository extends BaseEntityRepository<Element, String> {
	
	List<Element> findElementsByLesson(String lessonId);
	
}
