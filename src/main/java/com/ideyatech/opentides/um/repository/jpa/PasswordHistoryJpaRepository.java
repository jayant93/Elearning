package com.ideyatech.opentides.um.repository.jpa;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.PasswordHistory;
import com.ideyatech.opentides.um.repository.PasswordHistoryRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 *
 * @author Gino
 */
@RepositoryRestResource(path = "/password-history")
public interface PasswordHistoryJpaRepository extends PasswordHistoryRepository<String> {

    /**
     * Find all password history by user order by latest first
     *
     * @param user
     * @return
     */
    List<PasswordHistory> findByUserOrderByIdDesc(BaseUser user);

}
