package com.gamify.elearning.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamify.elearning.entity.CourseProgress;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

public interface CourseProgressRepository extends BaseEntityRepository<CourseProgress, String>{

	@Query(value = "select courseProgress from CourseProgress courseProgress where courseProgress.course.id = :courseId and courseProgress.user.id = :userId")
	CourseProgress getCourseProgressByCourseAndUser(@Param("courseId") String courseId, @Param("userId") String userId);

	@Query(value = "select count(courseProgress) from CourseProgress courseProgress where courseProgress.user.id = :id and courseProgress.finished = false")
	int getUnfinishedCoursesCount(@Param("id") String userId);

	@Query(value = "select count(courseProgress) from CourseProgress courseProgress where courseProgress.course.id = :courseId")
	int countCourseProgressById(@Param("courseId") String courseId);

}
