package com.ideyatech.opentides.um.repository.projections;

import org.springframework.data.rest.core.config.Projection;

import com.ideyatech.opentides.um.entity.BaseUser;

/**
 * @author Gino
 */
@Projection(name = "baseUser", types = {BaseUser.class})
public interface UserDropdownProjection {

    String getId();

    String getNosqlId();

    String getCompleteName();

}
