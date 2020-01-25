package com.gamify.elearning.repository.projection;

import com.gamify.elearning.entity.Element;
import com.gamify.elearning.entity.Quiz;
import com.gamify.elearning.entity.Video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "element", types = {Element.class, Quiz.class, Video.class})
public interface ElementProjection {
    String getId();

    @Value("#{ target.getLesson().getId() }")
    String getLessonId();

    String getElementType();

    Integer getOrdinal();

    String getTitle();

    String getDescription();

    String getTags();

    @Value("#{ target.getClass().getSimpleName().equals(\"Video\") ? target.getDurationInSec() : 0}")
    Integer getDurationInSec();

    @Value("#{ target.getClass().getSimpleName().equals(\"Quiz\") ? target.getQuestions().size() : 0}")
    Integer getTotalItems();

    @Value("#{ target.getClass().getSimpleName().equals(\"Video\") ? target.getRestrictionType().getKey() : null}")
    String getRestrictionTypeKey();

    @Value("#{ target.getClass().getSimpleName().equals(\"Video\") ? target.getRestrictionType().getValue() : null}")
    String getRestrictionTypeValue();
    
    @Value("#{ target.getClass().getSimpleName().equals(\"Video\") ? target.getVimeoId() : null}")
    String getVimeoId();

    @Value("#{ target.getClass().getSimpleName().equals(\"Video\") ? target.getFileName() : null}")
    String getFileName();
}