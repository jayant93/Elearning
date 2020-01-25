package com.ideyatech.opentides.um.repository;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.PasswordHistory;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Gino
 */
@NoRepositoryBean
public interface PasswordHistoryRepository<ID extends Serializable>
        extends BaseEntityRepository<PasswordHistory, ID> {

    /**
     * Find all password history by user order by latest first
     *
     * @param user
     * @return
     */
    List<PasswordHistory> findByUserOrderByIdDesc(BaseUser user);

}
