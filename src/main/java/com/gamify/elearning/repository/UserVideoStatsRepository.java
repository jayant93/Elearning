package com.gamify.elearning.repository;

import java.util.Date;
import java.util.List;

import com.gamify.elearning.entity.UserVideoStats;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface UserVideoStatsRepository extends BaseEntityRepository<UserVideoStats, String> {
	
	List<UserVideoStats> findVideoStatsByUser(String userId, String videoId, Date date);
	
	List<UserVideoStats> findVideoStatsByUser(String userId, String videoId);
	
	long countUserVideoStatsByVideoAndUser(String userId, String videoId);

}
