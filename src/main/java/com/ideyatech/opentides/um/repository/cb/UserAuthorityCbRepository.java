package com.ideyatech.opentides.um.repository.cb;

import com.couchbase.client.java.query.N1qlQuery;
import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserAuthority;
import com.ideyatech.opentides.um.repository.UserAuthorityRepository;
import com.ideyatech.opentides.um.repository.projections.UserAuthorityProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.repository.query.StringN1qlBasedQuery;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/user-authority", excerptProjection = UserAuthorityProjection.class)
public interface UserAuthorityCbRepository extends UserAuthorityRepository<String> {

}