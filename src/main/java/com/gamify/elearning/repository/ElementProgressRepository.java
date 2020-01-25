package com.gamify.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.ElementProgress;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface ElementProgressRepository extends BaseEntityRepository<ElementProgress, String> {

	@Query(value = "select elementProgress from ElementProgress elementProgress where elementProgress.courseProgress.id = :courseProgressId and elementProgress.element.id = :elementId and elementProgress.courseProgress.user.id= :userId")
	ElementProgress getElementProgressByCourseProgressElementAndUser(@Param("courseProgressId") String courseProgressId, @Param("elementId") String elementId, @Param("userId") String userId);

	@Query(value = "select elementProgress from ElementProgress elementProgress where elementProgress.element.id = :elementId")
	List<ElementProgress> findByElementId(@Param("elementId") String elementId);
}
