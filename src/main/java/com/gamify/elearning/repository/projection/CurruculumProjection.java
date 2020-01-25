package com.gamify.elearning.repository.projection;

import java.util.List;

import org.springframework.data.rest.core.config.Projection;


import com.gamify.elearning.entity.Curriculum;

@Projection(name = "course", types = {Curriculum.class})
public interface CurruculumProjection {
	
	 List<CurriculumProjection> getcurriculum();
}
