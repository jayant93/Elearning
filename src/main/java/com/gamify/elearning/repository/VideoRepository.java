package com.gamify.elearning.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gamify.elearning.entity.Video;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends BaseEntityRepository<Video, String> {
	
	List<Video> findAllUnassociatedVideos();
	
	List<Video> findVideosByLesson(String id);

	@Query(value="select count(video) as count, year(video.createDate) as year, month(video.createDate) as month from Video video " +
			"where video.createDate between :startDate and :endDate " +
			"group by year(video.createDate), month(video.createDate) " +
			"order by year asc, month asc")
	List<Map<String, Object>> getVideoCountPerMonth(@Param("startDate") Date start, @Param("endDate") Date end);
	
	@Query(value="SELECT v.id, COUNT(uv.id) AS totalViews " + 
			"FROM user_video_stats uv " + 
			"LEFT JOIN video v " + 
			"ON uv.video_id = v.id " + 
			"GROUP BY v.id", nativeQuery = true)
	List<Object[]> getTotalViewsPerVideo();

	@Query(value="select count(video) as count from Video video where video.lesson.course.deleted = false " +
			"and video.lesson.deleted = false and video.deleted = false")
	long getTotalVideosActive();
}
