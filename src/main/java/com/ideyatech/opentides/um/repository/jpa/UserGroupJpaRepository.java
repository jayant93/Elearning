package com.ideyatech.opentides.um.repository.jpa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.Authority;
import com.ideyatech.opentides.um.entity.UserGroup;
import com.ideyatech.opentides.um.repository.AuthorityRepository;
import com.ideyatech.opentides.um.repository.UserGroupRepository;

/**
 * Created by Gino on 8/25/2016.
 */
@RepositoryRestResource(path = "/usergroup")
public interface UserGroupJpaRepository extends UserGroupRepository<String> {

    /**
     * Find UserGroup by name.
     *
     * @param name the name of the group
     * @return the UserGroup with the given name
     */
    UserGroup findByName(@Param("name") String name);

    @Query(value="select * from usergroup where "
			+ "name like %:keyword% or description like %:keyword%", nativeQuery = true)
	List<UserGroup> keywordSearchUserGroup(@Param("keyword") String keyword);
	
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

class UserGroupJpaRepositoryCustomImpl implements UserGroupRepositoryCustom {

    @Autowired
    private BaseEntityRepositoryHelper baseEntityRepositoryHelper;

    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private UserGroupRepository userGroupRepository;
    
    public Page<UserGroup> search(String type, HashMap<String, Object> criteria, Pageable page) {
    	List<UserGroup> usergroups = new ArrayList<UserGroup>();
    	HashMap<String, List<UserGroup>> mapList = new HashMap<String, List<UserGroup>>();
    	Set<UserGroup> userGroupsSet = new HashSet<UserGroup>();
    	
    	if (type.equals("keyword")) {
    		usergroups = userGroupRepository.keywordSearchUserGroup(criteria.get("keyword").toString());
    	} else {
    		HashMap<String, String> keyValue = new HashMap<String, String>();
        	Iterator it = ((HashMap<String, Object>) criteria.get("criteria")).entrySet().iterator();
        	int ctr = 0;

        	while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getValue() != null && !StringUtils.isBlank(pair.getValue().toString())) {
                	List<UserGroup> tempUsergroups = new ArrayList<UserGroup>();
                	
                	if (pair.getKey().toString().equals("name")) {
                		tempUsergroups = userGroupRepository.findAllByNameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("description")) {
                		tempUsergroups = userGroupRepository.findAllByDescriptionIgnoreCase(pair.getValue().toString());
                	}
                	
                	if (ctr == 0) {
                		userGroupsSet.addAll(tempUsergroups);
                	} else {
                		userGroupsSet.retainAll(tempUsergroups);
                	}
                	ctr++;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
        	
        	usergroups.addAll(userGroupsSet);
    	}
    	
    	// Create and return user's data with pagination
        PagedListHolder<UserGroup> tempUserGroup = new PagedListHolder<UserGroup>(usergroups);
        MutableSortDefinition sortDefinition = new MutableSortDefinition("createDate", true, false);
        tempUserGroup.setSort(sortDefinition);
        tempUserGroup.resort();
        tempUserGroup.setPage(page.getPageNumber());
        tempUserGroup.setPageSize(page.getPageSize());
        
        Page<UserGroup> paginatedUserList = new PageImpl<UserGroup>(tempUserGroup.getPageList(), page, tempUserGroup.getSource().size());
        
    	return paginatedUserList;
    }
	
    @Override
    public List<Authority> createDefaultAuthority(Application application) {

        ObjectMapper mapper = new ObjectMapper();
        List<Authority> auths = new ArrayList<>();
        try {
            auths = mapper.readValue(getDefaultAuthsJson(), new TypeReference<List<Authority>>() {});
            for(Authority auth : auths) {
                auth.setApplication(application);
                authorityRepository.save(auth);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return auths;
    }

    private String getDefaultAuthsJson() {
        String json = "[\n" +
                "  {\n" +
                "    \"key\" : \"MANAGE_APPLICATION\",\n" +
                "    \"level\" : \"01.00.00.00\",\n" +
                "    \"title\" : \"Manage Application\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\" : \"MANAGE_ORGANIZATION\",\n" +
                "    \"level\" : \"02.00.00.00\",\n" +
                "    \"title\" : \"Manage ORGANIZATION\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\" : \"MANAGE_USERGROUP\",\n" +
                "    \"level\" : \"02.01.00.00\",\n" +
                "    \"title\" : \"Manage Usergroup\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\" : \"MANAGE_USER\",\n" +
                "    \"level\" : \"02.02.00.00\",\n" +
                "    \"title\" : \"Manage User\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\" : \"MANAGE_SYSTEM\",\n" +
                "    \"level\" : \"03.00.00.00\",\n" +
                "    \"title\" : \"Manage System\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\" : \"MANAGE_SYSTEM_CODES\",\n" +
                "    \"level\" : \"03.01.00.00\",\n" +
                "    \"title\" : \"Manage System Codes\"\n" +
                "  }\n" +
                "]";
        return json;
    }

    @Override
    public UserGroup setupAdminGroup(Application application) {
        UserGroup userGroup = new UserGroup();
        userGroup.setName("Administrator");
        userGroup.setDescription("System Administrators (Default)");
        userGroup.setIsDefault(Boolean.TRUE);

        List<Authority> auths = createDefaultAuthority(application);

        List<String> names = new ArrayList<String>();
        for (Authority auth : auths) {
            String key = auth.getKey();
            names.add(key);
        }
        userGroup.setAuthorityNames(names);
        baseEntityRepositoryHelper.saveEntityModel(userGroup);

        return userGroup;
    }
}