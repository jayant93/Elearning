package com.gamify.elearning.repository.projection;

import java.util.List;
import java.util.Set;

import com.gamify.elearning.entity.Badge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.gamify.elearning.entity.Course;
import com.gamify.elearning.entity.Curriculum;
import com.ideyatech.opentides.um.repository.projections.BaseUserProjection;

/**
 * @author johanna@ideyatech.com
 */
@Projection(name = "course", types = {Course.class})
public interface CourseProjection {

  //  String getId();
    
   // String getTitle();
    
   // String getDescription();
    
    //String getTags();
    
   // Integer getNumOfLessons();
    
   // List<LessonProjection> getLessons();
    
     List<CurriculumProjection> getcurriculum();
   
     //String getclassName();
	 //List<UnitProjection> getunit();
  //  Set<BadgeProjection> getBadges();
    
  //  ELearningUserProjection getUser();

   // Boolean getIsOwner();
}
