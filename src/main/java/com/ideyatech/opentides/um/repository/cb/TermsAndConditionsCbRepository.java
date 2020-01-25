package com.ideyatech.opentides.um.repository.cb;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.TermsAndConditions;
import com.ideyatech.opentides.um.repository.TermsAndConditionsRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Gino
 */
@RepositoryRestResource(path = "/terms-and-conditions")
public interface TermsAndConditionsCbRepository extends TermsAndConditionsRepository<String> {


}
