package com.ideyatech.opentides.um.repository.mongo;

import com.ideyatech.opentides.um.repository.TermsAndConditionsRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author Gino
 */
@RepositoryRestResource(path = "/terms-and-conditions")
public interface TermsAndConditionsMongoRepository extends TermsAndConditionsRepository<String> {

}
