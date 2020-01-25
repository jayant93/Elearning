package com.gamify.elearning.repository.jpa;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.Video;
import com.gamify.elearning.repository.VideoRepository;

public interface VideoJpaRepository extends VideoRepository {

	@Query(value="select v from Video v where v.lesson = null")
	List<Video> findAllUnassociatedVideos();
	
	@Query(value="select v from Video v where v.lesson.id = :id")
	List<Video> findVideosByLesson(@Param(value = "id") String id);
	
}
