package com.gamify.elearning.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.Element;
import com.gamify.elearning.repository.ElementRepository;

public interface ElementJpaRepository extends ElementRepository {
	
	@Query(value="select e from Element e where e.lesson.id = :id order by e.ordinal")
	List<Element> findElementsByLesson(@Param("id") String lessonId);

}