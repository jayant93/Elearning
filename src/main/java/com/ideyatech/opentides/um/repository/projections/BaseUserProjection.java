package com.ideyatech.opentides.um.repository.projections;

import java.util.Date;
import java.util.Set;

import com.gamify.elearning.entity.Company;
import com.ideyatech.opentides.um.entity.Division;
import com.ideyatech.opentides.um.entity.UserCredential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.gamify.elearning.entity.ELearningUser;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserGroup;

/**
 * @author Gino
 */
@Projection(name = "baseUser", types = {BaseUser.class, ELearningUser.class})
public interface BaseUserProjection {

    String getId();

    String getFirstName();

    String getLastName();

    String getMiddleName();

    String getEmailAddress();

    String getAddress();

    String getCompleteName();

    Set<UserGroup> getGroups();
    
    UserCredential getCredential();
    
    String getProfilePhotoUrl();

    Company getElearningCompany();

    String getFacebookUserId();

    @Value("#{target.getCredential().getUsername()}")
    String getUsername();

}
