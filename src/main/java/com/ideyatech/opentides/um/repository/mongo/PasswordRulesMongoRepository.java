package com.ideyatech.opentides.um.repository.mongo;

import com.ideyatech.opentides.um.repository.PasswordRulesRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/password-rules")
public interface PasswordRulesMongoRepository extends PasswordRulesRepository<String> {

}
