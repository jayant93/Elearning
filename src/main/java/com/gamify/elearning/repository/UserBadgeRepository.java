package com.gamify.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.Badge;
import com.gamify.elearning.entity.UserBadge;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface UserBadgeRepository extends BaseEntityRepository<UserBadge, String> {

    @Query(value="select userBadge from UserBadge userBadge where userBadge.course.id = :courseId and userBadge.user.id = :id")
    List<UserBadge> getByCourseAndUser(@Param("id") String userId, @Param("courseId") String courseId);

	@Query(value="select userBadge.badge from UserBadge userBadge where userBadge.user.id = :id")
	List<Badge> getBadgesByUser(@Param("id") String id);
}
