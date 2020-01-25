package com.gamify.elearning.repository.projection;

import com.gamify.elearning.entity.Quiz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "quiz", types = {Quiz.class})
public interface QuizProjection extends ElementProjection {
    String getId();

    @Value("#{ target.getLesson().getId() }")
    String getLessonId();

    String getElementType();

    Integer getOrdinal();

    String getTitle();
    
    String getDescription();

    String getTags();

    Double getPassingRate();

}