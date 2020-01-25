package com.gamify.elearning.repository.projection;

import java.util.List;

import com.gamify.elearning.entity.Lesson;

import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "lesson", types = {Lesson.class})
public interface LessonProjection {

    String getId();

    String getTitle();

    String getDescription();

    List<ElementProjection> getElements();

    Integer getNumOfElements();

    Integer getNumOfVideos();

    Integer getNumOfQuizzes();

    Integer getOrdinal();
}