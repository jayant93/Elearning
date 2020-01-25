package com.gamify.elearning.repository;

import com.gamify.elearning.entity.Quiz;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends BaseEntityRepository<Quiz, String> {

    @Query(value="select count(quiz) as count from Quiz quiz where quiz.lesson.course.deleted = false" +
            " and quiz.lesson.deleted = false and quiz.deleted = false")
	long getTotalQuizzesActive();

}
