package com.ideyatech.opentides.um.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.repository.AuthorityRepository;
import com.ideyatech.opentides.um.repository.projections.AuthorityProjection;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/authority", excerptProjection = AuthorityProjection.class)
public interface AuthorityJpaRepository extends AuthorityRepository<String> {
	
	@Query(value = "SELECT authz.* FROM authority authz WHERE parent is null", nativeQuery = true)
	List<Authority> findRootAuthz();
	
	@Query(value = "SELECT authz.* FROM authority authz WHERE parent  = :parentKey ", nativeQuery = true)
	List<Authority> findChildAuthz(@Param("parentKey") String parentKey);

}
