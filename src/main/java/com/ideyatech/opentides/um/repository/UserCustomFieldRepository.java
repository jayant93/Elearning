package com.ideyatech.opentides.um.repository;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.UserCustomField;
import com.ideyatech.opentides.um.repository.projections.UserCustomFieldProjection;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.io.Serializable;

/**
 * Created by Gino on 9/7/2016.
 */
@NoRepositoryBean
public interface UserCustomFieldRepository<ID extends Serializable> extends BaseEntityRepository<UserCustomField, ID> {

    UserCustomField findByKey(@Param("key") String key);

}
