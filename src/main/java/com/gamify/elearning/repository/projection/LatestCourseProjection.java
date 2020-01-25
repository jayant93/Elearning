package com.gamify.elearning.repository.projection;

import org.springframework.data.rest.core.config.Projection;

import com.gamify.elearning.entity.Course;

@Projection(name = "course", types = {Course.class})
public interface LatestCourseProjection {

	String getId();
}
