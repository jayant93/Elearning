package com.gamify.elearning.repository.projection;

import com.gamify.elearning.entity.Badge;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "badge", types = {Badge.class})
public interface BadgeProjection {
    String getId();

    String getImageUrl();

    String getTitle();
}