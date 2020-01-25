package com.ideyatech.opentides.um.repository.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.QueryHint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.couchbase.client.java.query.N1qlQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.core.repository.jpa.BaseEntityRepositoryHelperJpaImpl;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserCredential;
import com.ideyatech.opentides.um.repository.UserRepository;

/**
 * Created by Gino on 8/25/2016.
 */
@RepositoryRestResource(path = "/user")
public interface UserJpaRepository extends UserRepository<String> {
    /**
     * Count by email address.
     *
     * @param emailAddress email address to count
     * @return the number of users with the given email address
     */
    Long countByEmailAddress(String emailAddress);

    /**
     * Find BaseUser with the given username
     * @param username username to find
     *
     * @return the BaseUser
     */
    @Query(value = "select u from BaseUser u where u.credential.username=:username")
    @QueryHints(value = {
        @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and credential.username=$1")
    BaseUser findByUsername(@Param("username") String username);

    /**
     * Find BaseUser that is not archived with the given username
     * @param username username to find
     *
     * @return the BaseUser
     */
    @Query(value = "select u from BaseUser u where (u.archived is null or u.archived = false) and u.credential.username=:username")
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and (archived is null or archived = false) and credential.username=$1")
    @QueryHints(value = {
            @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    BaseUser findByUsernameNotArchived(@Param("username") String username);

    /**
     * Find BaseUser with the given emailAddress
     * @param emailAddress the email address of the baseuser
     * @return the BaseUser with the given email address
     */
    BaseUser findByEmailAddress(String emailAddress);

    BaseUser findByEmailAddressAndFacebookUserId(String emailAddress, String facebookUserId);

    /**
     * Find a list of BaseUser that belongs to given userGroupName
     *
     * @param userGroupName the name of the group
     * @return a List of BaseUser
     */
    @Query("select u from BaseUser u LEFT JOIN FETCH u.groups g " +
            "where g.name= :name AND u.credential.enabled=true " +
            "order by u.lastName, u.firstName")
    @org.springframework.data.couchbase.core.query.Query("select u from BaseUser u LEFT JOIN FETCH u.groups g " +
            "where g.name= :name AND u.credential.enabled=true " +
            "order by u.lastName, u.firstName")
    List<BaseUser> findByUsergroupName(@Param("name") String userGroupName);

    List<BaseUser> findByLastNameStartingWith(String lastName);

    @RestResource(exported = false)
    BaseUser findByActivationVerificationKey(String activationVerificationKey);

    @RestResource(exported = false)
    BaseUser findByResetPasswordKey(String resetPasswordKey);

    @Query(value = "select u from BaseUser u where u.emailAddress=:param or u.credential.username=:param")
    @org.springframework.data.couchbase.core.query.Query(value = "select u from BaseUser u " +
            "where u.emailAddress=:param or u.credential.username=:param")
    @RestResource(exported = false)
    BaseUser findByUsernameOrEmail(@Param("param") String param);

    @RestResource(exported = false)
    @Modifying
    @Query("update BaseUser u set u.tacAcceptedTs = CURRENT_TIMESTAMP where u.id = :id")
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.tacAcceptedTs = CURRENT_TIMESTAMP where u.id = :id")
    void acceptTermsAndConditions(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    @Query("update BaseUser u set u.archived = true where u.id = :id")
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.archived = true where u.id = :id")
    void archiveUser(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    @Query("update BaseUser u set u.archived = false where u.id = :id")
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.archived = false where u.id = :id")
    void unarchiveUser(@Param("id") Long id);

    @Override
    List<BaseUser> findByOrgUnit(@Param("name") String divisionName);

    @Override
    List<BaseUser> findByOrgUnitList(@Param("orgUnits") String orgUnitKeyList);
    
    @Query(value="SELECT * FROM user_profile as user where lastname like %:keyword%" +
	    	" OR middlename like %:keyword% OR firstname like %:keyword%" +
	        " OR email like %:keyword%" +
	        " OR lower(json_extract(home_department, '$.name')) like lower(json_quote(%:keyword%))" +
	        " OR user.id = (SELECT userid FROM users where userid=user.id AND username like %:keyword%)" +
	        " OR user.id = (SELECT userid FROM users where userid=user.id AND status like %:keyword%)" +
	        " OR user.id = (SELECT user_id FROM user_group as ugroup where user_id=user.id AND" +
			" group_id = (SELECT id FROM usergroup where name like %:keyword% AND id=ugroup.group_id))", nativeQuery=true)
    List<BaseUser> keywordSearchUser(@Param("keyword") String keyword);
    
    @Query(value="select * from user_profile where "
			+ "lower(json_extract(home_department, '$.name')) = lower(json_quote(:homeDepartment))", nativeQuery = true)
    List<BaseUser> findAllByHomeDepartment(@Param("homeDepartment") String homeDepartment);
    
    Page<BaseUser> search(String type, HashMap<String, Object> criteria, Pageable page);
}
	
interface UserRepositoryCustom {

    /**
     * Create an admin user
     * @return
     */
    BaseUser createAdminUser(Application application);

	/**
     * Handle failed login attempt.
     *
     * @param username
     * @param timestamp
     */
    void updateFailedLogin(String username, long timestamp);

    /**
     * Update the date when the user last logged-in.
     * @param username The username of the user to be updated
     */
    @Modifying
    @Query(value = "update BaseUser u set u.lastLogin = CURRENT_TIMESTAMP where u.credential.username=:username")
    @org.springframework.data.couchbase.core.query.Query(value = "update BaseUser u set u.lastLogin = " +
            "CURRENT_TIMESTAMP where u.credential.username=:username")
    void updateLastLogin(@Param("username") String username);
}

class UserJpaRepositoryCustomImpl implements UserRepositoryCustom {

    @Autowired
    private BaseEntityRepositoryHelper baseEntityRepositoryHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    @PersistenceContext
    protected EntityManager em;

    public Page<BaseUser> search(String type, HashMap<String, Object> criteria, Pageable page) {
    	List<BaseUser> users = new ArrayList<BaseUser>();
    	HashMap<String, List<BaseUser>> mapList = new HashMap<String, List<BaseUser>>();
    	Set<BaseUser> usersSet = new HashSet<BaseUser>();
    	
    	if (type.equals("keyword")) {
    		users = userRepository.keywordSearchUser(criteria.get("keyword").toString());
    	} else {
    		HashMap<String, String> keyValue = new HashMap<String, String>();
        	Iterator it = ((HashMap<String, Object>) criteria.get("criteria")).entrySet().iterator();
        	int ctr = 0; 

        	while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getValue() != null && !StringUtils.isBlank(pair.getValue().toString())) {
                	List<BaseUser> tempUsers = new ArrayList<BaseUser>();
                	
                	if (pair.getKey().toString().equals("lastname")) {
                		tempUsers = userRepository.findAllByLastNameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("firstname")) {
                		tempUsers = userRepository.findAllByFirstNameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("middlename")) {
                		tempUsers = userRepository.findAllByMiddleNameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("email")) {
                		tempUsers = userRepository.findAllByEmailAddressIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("groups")) {
                		tempUsers = userRepository.findAllByGroupsNameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("username")) {
                		tempUsers = userRepository.findAllByCredentialUsernameIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("status")) {
                		tempUsers = userRepository.findAllByCredentialStatusIgnoreCase(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("homeDepartment")) {
                		tempUsers = userRepository.findAllByHomeDepartment(pair.getValue().toString());
                	} else if (pair.getKey().toString().equals("agency")) {
                		tempUsers = userRepository.findAllByGroupsNameIgnoreCase(pair.getValue().toString());
                	}                 	
                	
                	if (ctr == 0) {
                		usersSet.addAll(tempUsers);
                	} else {
                		usersSet.retainAll(tempUsers);
                	}
                	ctr++;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
        	users.addAll(usersSet);
    	}
    	
    	// Create and return user's data with pagination
        PagedListHolder<BaseUser> tempUser = new PagedListHolder<BaseUser>(users);
        MutableSortDefinition sortDefinition = new MutableSortDefinition("createDate", true, false);
        tempUser.setSort(sortDefinition);
        tempUser.resort();
        tempUser.setPage(page.getPageNumber());
        tempUser.setPageSize(page.getPageSize());
        
        Page<BaseUser> paginatedUserList = new PageImpl<BaseUser>(tempUser.getPageList(), page, tempUser.getSource().size());

    	return paginatedUserList;
    }       
    
    @Override
    public BaseUser createAdminUser(Application application) {
        BaseUser user = new BaseUser();

        UserCredential cred = new UserCredential();
        cred.setUsername("admin");
        String tempPassword = JwtUtil.generateSecretKey(8);
        cred.setNewPassword(tempPassword);
        cred.setPassword(passwordEncoder.encode(tempPassword));
        cred.setEnabled(true);
        cred.setUser(user);
        user.setCredential(cred);
        user.setEmailAddress(application.getEmailAddress());
        user.setFirstName("Administrator");
        user.setLastName("Administrator");
        user.setArchived(false);

        user.addGroup(application.getAdminUserGroup());
        application.setAdminPassword(tempPassword);

        baseEntityRepositoryHelper.saveEntityModel(user);

        return user;
    }

    @Override
    public void updateFailedLogin(String username, long timestamp) {

    }

    @Override
    public void updateLastLogin(@Param("username") String username) {
        //Need to do this because Spring Data Couchbase currently doesn't support Modifying Queries
        if("JPA".equals(baseEntityRepositoryHelper.getSpringDataType())) {
            EntityManager em = ((BaseEntityRepositoryHelperJpaImpl)baseEntityRepositoryHelper).getEntityManager();
            javax.persistence.Query query = em.createQuery("update BaseUser u set u.lastLogin = CURRENT_TIMESTAMP where u.credential.username=:username");
            query.setParameter("username", username);
            query.executeUpdate();
        } else if("CB".equals(baseEntityRepositoryHelper.getSpringDataType())) {
            CouchbaseOperations couchbaseOperations =
                    ((BaseEntityRepositoryHelperCbImpl)baseEntityRepositoryHelper).getCouchbaseOperations();
            N1qlQuery query = N1qlQuery.simple("#{#n1ql.selectEntity} " +
                    "where #{#n1ql.filter} and credential.username=\"" + username + "\"");
            List<BaseUser> users = couchbaseOperations.findByN1QL(query, BaseUser.class);
            if(!users.isEmpty()) {
                BaseUser user = users.get(0);
                user.setLastLogin(new Date());
                baseEntityRepositoryHelper.saveEntityModel(user);
            }

        }
    }
    
    public List<BaseUser> findByOrgUnit(String divisionName){
    	List<String> sortProperties = new ArrayList<String>();
    	sortProperties.add("lastName");
    	sortProperties.add("firstName");
    	Sort sort = new Sort(Sort.Direction.ASC, sortProperties);
    	
    	List<BaseUser> users = (List<BaseUser>) userRepository.findAll();
    	List<BaseUser> userToRetrieve = new ArrayList<BaseUser>();
    	
    	for (BaseUser user : users){
    		if (user.getDivisionKeys().contains(divisionName) || (user.getHomeDepartment() != null && user.getHomeDepartment().getKey().equals(divisionName))){
    			userToRetrieve.add(user);
    		}
    	}
    	
    	return userToRetrieve;
    }
    
    public List<BaseUser> findByOrgUnitList(String orgUnitKeyList){
    	List<String> sortProperties = new ArrayList<String>();
    	sortProperties.add("lastName");
    	sortProperties.add("firstName");
    	Sort sort = new Sort(Sort.Direction.ASC, sortProperties);
    	
    	List<BaseUser> users = (List<BaseUser>) userRepository.findAll();
    	List<BaseUser> userToRetrieve = new ArrayList<BaseUser>();
    	ObjectMapper mapper = new ObjectMapper();
    	List<String> str = new ArrayList<String>();
    	
		try {
			str = mapper.readValue(orgUnitKeyList, ArrayList.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	for (BaseUser user : users){
    		if (user.getDivisionKeys().contains(str) || (user.getHomeDepartment() != null && str.contains(user.getHomeDepartment().getKey()))){
    			userToRetrieve.add(user);
    		}
    	}
    	
    	return userToRetrieve;
    }
}