package com.gamify.elearning.repository.projection;

import com.gamify.elearning.entity.Thumbnail;

import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "thumbnail", types = {Thumbnail.class})
public interface ThumbnailProjection {
    String getUrl();

    String getThumbnailId();

    Boolean getActive();

    Boolean getCustom();
}