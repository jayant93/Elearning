package com.gamify.elearning.repository.projection;

import org.springframework.data.rest.core.config.Projection;

import com.gamify.elearning.entity.Course;
import com.ideyatech.opentides.um.entity.BaseUser;

/**
 * @author Gino
 */
@Projection(name = "course", types = {Course.class})
public interface UserProjection {

    String getId();
    
    String getTitle();
    
    String getDescription();
    
    String getTags();
    
    Integer getNumOfLessons();
    
    BaseUser getUser();

}
