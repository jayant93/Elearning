package com.ideyatech.opentides.um.repository.jpa;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.UserCustomField;
import com.ideyatech.opentides.um.repository.UserCustomFieldRepository;
import com.ideyatech.opentides.um.repository.projections.UserCustomFieldProjection;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Gino on 9/7/2016.
 */
@RepositoryRestResource(path = "/fields", excerptProjection = UserCustomFieldProjection.class)
public interface UserCustomFieldJpaRepository extends UserCustomFieldRepository<String> {

}
