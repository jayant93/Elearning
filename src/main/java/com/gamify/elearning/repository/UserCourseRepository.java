package com.gamify.elearning.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.UserCourse;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UserCourseRepository<ID extends Serializable> 
            extends BaseEntityRepository<UserCourse, ID>{

    @Query(value="select usercourse from UserCourse usercourse where usercourse.user.id = :id and usercourse.course.deleted = false")
    List<UserCourse> findByUser(@Param("id") String userId);

    @Query(value="select usercourse from UserCourse usercourse where usercourse.user.id = :id and usercourse.course.id = :courseId " +
            "and usercourse.course.deleted = false")
    List<UserCourse> getUserCourseByUserAndCourse(@Param("id") String userId, @Param("courseId") String courseId);

    @Query(value="select count(usercourse) from UserCourse usercourse where usercourse.user.id = :id and usercourse.course.deleted = false")
    int countCoursesByUser(@Param("id") String userId);

    @Query(value="select usercourse from UserCourse usercourse where usercourse.user.id = :id and usercourse.course.deleted = false")
    List<UserCourse> findByUser(@Param("id") String userId, Pageable pageable);

    @Query(value="select count(usercourse) from UserCourse usercourse where usercourse.user.id = :id and usercourse.course.deleted = false")
    int countTakenCourses(@Param("id") String userId);

    @Query(value="select usercourse.user, max(usercourse.dateTaken) from UserCourse usercourse " +
                "inner join usercourse.course course " +
                "where course.user.id = :userId " +
                "group by usercourse.user " +
                "order by max(usercourse.dateTaken) desc ")
    List<ELearningUser> getRecentTakersOfMyCourses(@Param("userId") String userId, Pageable pageable);

    @Query(value="select count(usercourse) from UserCourse usercourse " +
                "inner join usercourse.course course " +
                "where course.user.id = :userId and course.deleted = false")
    int getMyCourseTakersCount(@Param("userId") String userId);

    @Query(value="select uc.course as course, count(uc.course) as takers from UserCourse uc " + 
                "where uc.course.deleted = false " +
                "group by uc.course")
    List<Map<String, Object>> findPopularCourses(Pageable pageable);

    @Query(value="select uc.course as course, count(uc.course) as takers from UserCourse uc group by uc.course")
    List<Map<String, Object>> getAllPopularCourses(); 

    @Query(value="select uc.course as course, count(uc.course) as takers from UserCourse uc " +
                "where uc.course.deleted = false " +
                "group by uc.course")
    List<Map<String, Object>> getAllPopularCoursesCnt();
}