package com.ideyatech.opentides.um.repository;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.TermsAndConditions;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.io.Serializable;

/**
 * @author Gino
 */
@NoRepositoryBean
public interface TermsAndConditionsRepository<ID extends Serializable>
        extends BaseEntityRepository<TermsAndConditions, ID> {



}
