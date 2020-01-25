package com.ideyatech.opentides.um.repository.mongo;

import com.ideyatech.opentides.um.repository.AuthorityRepository;
import com.ideyatech.opentides.um.repository.projections.AuthorityProjection;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/authority", excerptProjection = AuthorityProjection.class)
public interface AuthorityMongoRepository extends AuthorityRepository<String> {

}
