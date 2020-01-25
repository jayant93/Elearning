package com.ideyatech.opentides.um.repository.mongo;

import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.repository.ApplicationRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.QueryHint;

/**
 * Created by Gino on 12/9/2016.
 */
@RepositoryRestResource(path = "/application")
public interface ApplicationMongoRepository extends ApplicationRepository<String> {

    @QueryHints(value = {
            @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    Application findByAppSecret(String appSecret);

    @RestResource(exported = false)
    @Override
    Application save(Application entity);

}
