    package com.ideyatech.opentides.um.repository;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.PasswordRules;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.io.Serializable;

    /**
 * Created by Gino on 8/30/2016.
 */
@NoRepositoryBean
public interface PasswordRulesRepository<ID extends Serializable>
            extends BaseEntityRepository<PasswordRules, ID> {

}
