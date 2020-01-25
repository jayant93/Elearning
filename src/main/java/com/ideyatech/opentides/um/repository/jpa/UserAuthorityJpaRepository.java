package com.ideyatech.opentides.um.repository.jpa;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.UserAuthority;
import com.ideyatech.opentides.um.repository.UserAuthorityRepository;
import com.ideyatech.opentides.um.repository.projections.UserAuthorityProjection;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/user-authority", excerptProjection = UserAuthorityProjection.class)
public interface UserAuthorityJpaRepository extends UserAuthorityRepository<String> {

}