package com.gamify.elearning.repository.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.gamify.elearning.entity.Curriculum;



@Projection(name = "curriculum", types = {Curriculum.class})
public interface CurriculumProjection {

	 String getclassName();
	 List<UnitProjection> getunit();
}
