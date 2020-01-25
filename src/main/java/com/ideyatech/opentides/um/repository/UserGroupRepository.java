package com.ideyatech.opentides.um.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.entity.UserGroup;

/**
 * Created by Gino on 8/25/2016.
 */
@NoRepositoryBean
public interface UserGroupRepository<ID extends Serializable>
        extends UserGroupRepositoryCustom, BaseEntityRepository<UserGroup, ID> {

    /**
     * Find UserGroup by name.
     *
     * @param name the name of the group
     * @return the UserGroup with the given name
     */
    UserGroup findByName(@Param("name") String name);

    @Override
    @RestResource(path = "findByExample")
    List<UserGroup> findByExample(UserGroup example, boolean exactMatch, int start, int total);
    
	List<UserGroup> keywordSearchUserGroup(@Param("keyword") String keyword);
	
	List<UserGroup> findAllByNameIgnoreCase(@Param("name") String name);
	
	List<UserGroup> findAllByDescriptionIgnoreCase(@Param("description") String description);
	
	Page<UserGroup> search(String type, HashMap<String, Object> criteria, Pageable page);
    
}

/**
 * Custom repository for UserGroup
 *
 */
interface UserGroupRepositoryCustom {

    /**
     * Create the default authority
     *
     * @return
     */
    @RestResource(exported = false)
    List<Authority> createDefaultAuthority(Application application);

    UserGroup setupAdminGroup(Application application);

}