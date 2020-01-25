package com.gamify.elearning.repository.projection;

import java.util.List;

import com.gamify.elearning.entity.Video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "video", types = {Video.class})
public interface VideoProjection2 extends ElementProjection {
    String getId();

    @Value("#{ target.getLesson().getId() }")
    String getLessonId();

    String getElementType();

    Integer getOrdinal();

    String getTitle();

    String getDescription();

    String getTags();

    Integer getDurationInSec();

    String getFileName();

    @Value("#{ target.getRestrictionType().getKey() }")
    String getRestrictionTypeKey();

    @Value("#{ target.getRestrictionType().getValue() }")
    String getRestrictionTypeValue();

    String getVimeoId();

    List<ThumbnailProjection> getThumbnails();

}