package com.ideyatech.opentides.um.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.Authority;

/**
 * Created by Gino on 8/30/2016.
 */
@NoRepositoryBean
public interface AuthorityRepository<ID extends Serializable> extends BaseEntityRepository<Authority, ID> {
	Authority findByKey(@Param("key") String key);
	List<Authority> findRootAuthz();
	List<Authority> findChildAuthz(@Param("parentKey") String parentKey);
}
