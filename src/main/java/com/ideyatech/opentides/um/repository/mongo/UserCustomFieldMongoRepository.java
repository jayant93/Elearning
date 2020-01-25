package com.ideyatech.opentides.um.repository.mongo;

import com.ideyatech.opentides.um.repository.UserCustomFieldRepository;
import com.ideyatech.opentides.um.repository.projections.UserCustomFieldProjection;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Gino on 9/7/2016.
 */
@RepositoryRestResource(path = "/fields", excerptProjection = UserCustomFieldProjection.class)
public interface UserCustomFieldMongoRepository extends UserCustomFieldRepository<String> {

}
