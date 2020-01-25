package com.ideyatech.opentides.um.repository;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.Application;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.QueryHint;

import java.io.Serializable;

/**
 * Created by Gino on 8/30/2016.
 */
@NoRepositoryBean
public interface ApplicationRepository<ID extends Serializable> extends BaseEntityRepository<Application, ID> {

    /**
     * Find application by name
     * @param name
     * @return
     */
    Application findByName(String name);

    /**
     * Count application by app secret.
     *
     * @param appSecret
     * @return
     */
    Long countByAppSecret(String appSecret);

    /**
     * Find application by secret key.
     *
     * @param appSecret
     * @return
     */
    @QueryHints(value = {
        @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    Application findByAppSecret(String appSecret);

    @RestResource(exported = false)
    @Override
    Application save(Application entity);
}
