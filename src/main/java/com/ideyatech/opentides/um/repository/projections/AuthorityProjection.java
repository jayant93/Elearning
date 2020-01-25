package com.ideyatech.opentides.um.repository.projections;

import com.ideyatech.opentides.um.entity.Authority;
import org.springframework.data.rest.core.config.Projection;

/**
 * Projection for {@link Authority} class.
 * @author Gino
 */
@Projection(types = {Authority.class})
public interface AuthorityProjection {

    String getKey();

    String getLevel();

    String getTitle();
    
    String getParent();

}
