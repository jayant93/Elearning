package com.ideyatech.opentides.um.repository.projections;

import com.ideyatech.opentides.um.entity.UserAuthority;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author Gino
 */
@Projection(name = "authorityDefaultProjection", types = UserAuthority.class)
public interface UserAuthorityProjection {

    String getAuthority();

    String getUsername();

}
