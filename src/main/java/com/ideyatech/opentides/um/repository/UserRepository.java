package com.ideyatech.opentides.um.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.ideyatech.opentides.core.repository.BaseEntityRepository;
import com.ideyatech.opentides.um.entity.BaseUser;

/**
 * Created by Gino on 8/25/2016.
 */
@NoRepositoryBean
@Transactional
public interface UserRepository<ID extends Serializable>
        extends JpaRepository<BaseUser,ID>,UserRepositoryExternalCustom, BaseEntityRepository<BaseUser, ID> {

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
    @org.springframework.data.mongodb.repository.Query(value = "{'credential.username' : ?0}")
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
    
    @Query(value = "select u from BaseUser u where u.facebookUserId=:facebookUserId")
    @QueryHints(value = {
        @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and facebookUserId=$1")
    @org.springframework.data.mongodb.repository.Query(value = "{'facebookUserId' : ?0}")
    BaseUser findByFacebookUserId(@Param("facebookUserId") String facebookUserId);

    @Query(value = "select u from BaseUser u where u.googleUserId=:googleUserId")
    @QueryHints(value = {
            @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and googleUserId=$1")
    @org.springframework.data.mongodb.repository.Query(value = "{'googleUserId' : ?0}")
    BaseUser findByGoogleUserId(@Param("googleUserId") String googleUserId);

    /**
     * Find a list of BaseUser that belongs to given userGroupName
     *
     * @param userGroupName the name of the group
     * @return a List of BaseUser
     */
    @Query("select u from BaseUser u LEFT JOIN FETCH u.groups g " +
            "where g.name= :name AND u.credential.enabled=true " +
            "order by u.lastName, u.firstName")
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and any grp in groups satisfies grp.name = $1 end")
    List<BaseUser> findByUsergroupName(@Param("name") String userGroupName);
    
    /**
     * Find a list of BaseUser that belongs to given division/orgUnit.
     *
     * @param divisionKey the name of the group
     * @return a List of BaseUser
     */
    List<BaseUser> findByOrgUnit(@Param("name") String divisionName);

    /**
     * Find a list of BaseUser that belongs to given list of division/orgUnit.
     *
     * @param orgUnitKeyList the list of name of the group
     * @return a List of BaseUser
     */
    List<BaseUser> findByOrgUnitList(@Param("orgUnits") String orgUnitKeyList);

    List<BaseUser> findByLastNameStartingWith(String lastName);

    @RestResource(exported = false)
    BaseUser findByActivationVerificationKey(String activationVerificationKey);

    @RestResource(exported = false)
    BaseUser findByResetPasswordKey(String resetPasswordKey);

    @Query(value = "select u from BaseUser u where u.emailAddress=:param or u.credential.username=:param")
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and emailAddress = $1 or credential.username = $1")
    @RestResource(exported = false)
    BaseUser findByUsernameOrEmail(@Param("param") String param);
	
	List<BaseUser> keywordSearchUser(@Param("keyword") String keyword);
	
	List<BaseUser> findAllByLastNameIgnoreCase(@Param("lastname") String lastname);
	List<BaseUser> findAllByFirstNameIgnoreCase(@Param("firstname") String firstname);
	List<BaseUser> findAllByMiddleNameIgnoreCase(@Param("middlename") String middlename);
	List<BaseUser> findAllByEmailAddressIgnoreCase(@Param("email") String email);
	List<BaseUser> findAllByGroupsNameIgnoreCase(@Param("group") String group);
	List<BaseUser> findAllByCredentialUsernameIgnoreCase(@Param("username") String username);
	List<BaseUser> findAllByCredentialStatusIgnoreCase(@Param("status") String status);
	List<BaseUser> findAllByHomeDepartment(@Param("homeDepartment") String homeDepartment);
	
	Page<BaseUser> search(String type, HashMap<String, Object> criteria, Pageable page);

	@Query(value = "select u from BaseUser u where u.emailAddress=:email")
	//@Query(value = "select * from user_profile u where u.email=:email" ,nativeQuery=true)
	Optional<BaseUser> findbyEmail(@Param("email") String email);
	
	

	
	
}