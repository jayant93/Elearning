package com.gamify.elearning.repository.projection;

import com.gamify.elearning.entity.ELearningUser;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "elearningUser", types = {ELearningUser.class})
public interface ELearningUserProjection {
    String getId();
    
    String getEmailAddress();
}