package com.ideyatech.opentides.um.config;

import com.ideyatech.opentides.um.entity.*;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

import com.ideyatech.opentides.core.entity.SystemCodes;
import com.ideyatech.opentides.um.repository.projections.BaseUserProjection;

/**
 *
 * @author Gino
 */
@Component
public class SpringDataRestConfigurerAdapter extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(BaseUser.class, Application.class,
                SystemCodes.class, UserGroup.class, UserAuthority.class, Division.class);
        config.getProjectionConfiguration().addProjection(BaseUserProjection.class);
    }
}
