package com.ideyatech.opentides.um.repository.jpa;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.TermsAndConditions;
import com.ideyatech.opentides.um.repository.TermsAndConditionsRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Gino
 */
@RepositoryRestResource(path = "/terms-and-conditions")
public interface TermsAndConditionsJpaRepository extends TermsAndConditionsRepository<String> {

}
