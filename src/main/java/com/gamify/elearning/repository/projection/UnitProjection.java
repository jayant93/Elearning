package com.gamify.elearning.repository.projection;

import org.springframework.data.rest.core.config.Projection;


import com.gamify.elearning.entity.Unit;

@Projection(name = "unit", types = {Unit.class})
public interface UnitProjection {
    String getId();
	String getunitName();
	String getTextDescription();
	//String getImageLocation();
	String getFileBase64();
}
