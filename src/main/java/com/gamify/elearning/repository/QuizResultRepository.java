package com.gamify.elearning.repository;

import com.gamify.elearning.entity.ELearningUser;
import com.gamify.elearning.entity.QuizResult;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface QuizResultRepository extends BaseEntityRepository<QuizResult, String> {

    @Query(value="select count(result) from QuizResult result where result.quiz.id = :id")
    int countEntries(@Param("id") String quizId);

    @Query(value="select result from QuizResult result where result.quiz.id = :id")
    List<QuizResult> getResultsByQuizId(@Param("id") String quizId, Pageable pageable);

    @Query(value="select result from QuizResult result where result.quiz.id = :id and result.user.id = :userId")
    List<QuizResult> getResultsByQuizAndUser(@Param("id") String quizId, @Param("userId") String userId);

    @Query(value="select result from QuizResult result " +
                "inner join result.quiz quiz " +
                "inner join result.quiz.lesson lesson " +
                "inner join result.quiz.lesson.course course " +
                "where course.id = :id AND result.user.id = :userId")
    List<QuizResult> getResultsByCourse(@Param("id") String courseId, @Param("userId") String userId);

    @Query(value="select result.user, max(result.dateTaken) from QuizResult result " +
                "inner join result.quiz quiz " +
                "inner join result.quiz.lesson lesson " +
                "inner join result.quiz.lesson.course course " +
                "where course.user.id = :userId " +
                "group by result.user " +
                "order by max(result.dateTaken) desc")
    List<ELearningUser> getRecentQuizTakersOfMyCourses(@Param("userId") String userId, Pageable pageable);

    @Query(value="select count(result) from QuizResult result where result.quiz.id = :id and result.passed = true")
    int countPassedQuizzes(@Param("id") String quizId);

    @Query(value="select count(result) from QuizResult result where result.quiz.id = :id and result.passed = false")
    int countFailedQuizzes(@Param("id") String quizId);
}