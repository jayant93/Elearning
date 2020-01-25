package com.gamify.elearning.repository.jpa;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.UserVideoStats;
import com.gamify.elearning.repository.UserVideoStatsRepository;

public interface UserVideoStatsJpaRepository extends UserVideoStatsRepository {
	
	@Query(value="select u from UserVideoStats u where u.user.id = :userId "
			+ "and u.video.id = :videoId and date(u.dateLastViewed) = date(:date) order by u.dateLastViewed desc")
	List<UserVideoStats> findVideoStatsByUser(@Param("userId") String userId, @Param("videoId") String videoId,  @Param("date") Date date);
	
	@Query(value="select u from UserVideoStats u where u.user.id = :userId "
			+ "and u.video.id = :videoId order by u.dateLastViewed desc")
	List<UserVideoStats> findVideoStatsByUser(@Param("userId") String userId, @Param("videoId") String videoId);

	@Query(value="select count(u) from UserVideoStats u where u.user.id = :userId and u.video.id = :videoId")
	long countUserVideoStatsByVideoAndUser(@Param("userId") String userId, @Param("videoId") String videoId);
	
}
