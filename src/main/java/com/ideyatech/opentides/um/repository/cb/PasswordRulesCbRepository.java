    package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.PasswordRules;
import com.ideyatech.opentides.um.repository.PasswordRulesRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Gino on 8/30/2016.
 */
@RepositoryRestResource(path = "/password-rules")
public interface PasswordRulesCbRepository extends PasswordRulesRepository<String> {

}
