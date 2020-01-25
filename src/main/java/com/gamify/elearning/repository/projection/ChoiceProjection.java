package com.gamify.elearning.repository.projection;

import com.gamify.elearning.entity.Choice;

import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "choice", types = {Choice.class})
public interface ChoiceProjection {
    
    String getId();

    String getValue();

    Boolean getCorrectAnswer();

    Integer getOrdinal();
}