package com.gamify.elearning.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.VideoProgress;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface VideoProgressRepository extends BaseEntityRepository<VideoProgress, String> {

	@Query(value = "select videoProgress from VideoProgress videoProgress where videoProgress.courseProgress.id = :courseProgressId and videoProgress.element.id = :elementId and videoProgress.courseProgress.user.id= :userId")
	VideoProgress getVideoProgressByCourseProgressElementAndUser(@Param("courseProgressId") String courseProgressId, @Param("elementId") String elementId, @Param("userId") String userId);
}
