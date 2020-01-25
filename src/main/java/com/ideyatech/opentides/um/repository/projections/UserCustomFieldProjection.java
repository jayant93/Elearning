package com.ideyatech.opentides.um.repository.projections;

import com.ideyatech.opentides.um.entity.UserAuthority;
import com.ideyatech.opentides.um.entity.UserCustomField;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author Gino
 */
@Projection(name = "authorityDefaultProjection", types = UserCustomField.class)
public interface UserCustomFieldProjection {

    String getKey();

    String getLabel();

    String getDescription();

    String getDataType();

}
