package com.ideyatech.opentides.um.repository.mongo;

import com.couchbase.client.java.query.N1qlQuery;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.mongo.repository.impl.BaseEntityMongoRepositoryHelper;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserCredential;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.repository.UserRepositoryExternalCustom;
import com.ideyatech.opentides.um.repository.projections.BaseUserProjection;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.repository.query.StringN1qlBasedQuery;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by Gino on 8/25/2016.
 */
@RepositoryRestResource(path = "/user", excerptProjection = BaseUserProjection.class)
public interface UserMongoRepository extends UserMongoRepositoryCustom, UserRepository<String> {

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
     *
     * @param username username to find
     *
     * @return the BaseUser
     */
    @Query(value = "select u from BaseUser u where (u.archived is null or u.archived = false) and u.credential.username=:username")
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and (archived is null or archived = false) and credential.username=$1")
    @QueryHints(value = {
            @QueryHint(value = "true", name = "org.hibernate.cacheable")})
    @org.springframework.data.mongodb.repository.Query(value = "{$and : [{'credential.username' : ?0},{$or : [{'archived' : null}, {'archived' : false}]}]}")
    BaseUser findByUsernameNotArchived(@Param("username") String username);


    /**
     * Find a list of BaseUser that belongs to given userGroupName
     *
     * @param userGroupName the name of the group
     * @return a List of BaseUser
     */
    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and any grp in groups satisfies grp.name = $1 end")
    @org.springframework.data.mongodb.repository.Query(value = "{'groups' : {$elemMatch : { 'name' : ?0 }}}")
    List<BaseUser> findByUserGroupName(@Param("name") String userGroupName);

    List<BaseUser> findByLastNameStartingWith(String lastName);

    @Query(value = "select u from BaseUser u where u.emailAddress=:param or u.credential.username=:param")
    @org.springframework.data.couchbase.core.query.Query(value = "select u from BaseUser u " +
            "where u.emailAddress=:param or u.credential.username=:param")
    @org.springframework.data.mongodb.repository.Query(value = "{$or : [{'emailAddress' : ?0}, {'credential.username' : ?0}]}")
    @RestResource(exported = false)
    BaseUser findByUsernameOrEmail(@Param("param") String param);

}

interface UserMongoRepositoryCustom {
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
    void updateLastLogin(@Param("username") String username);

    @RestResource(exported = false)
    void archiveUser(@Param("id") Long id);

    @RestResource(exported = false)
    void unarchiveUser(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    void acceptTermsAndConditions(@Param("id") Long id);


    List<BaseUser> findByUsernames(String ... usernames);
    List<BaseUser> findByUsergroupId(String userGroupId);

}

class UserMongoRepositoryCustomImpl implements UserMongoRepositoryCustom {

    @Autowired
    private BaseEntityRepositoryHelper baseEntityRepositoryHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public void updateLastLogin(@Param("username") String username) {
        CouchbaseOperations couchbaseOperations =
                ((BaseEntityRepositoryHelperCbImpl) baseEntityRepositoryHelper).getCouchbaseOperations();
        N1qlQuery query = N1qlQuery.simple("#{#n1ql.selectEntity} " +
                "where #{#n1ql.filter} and credential.username=\"" + username + "\"");
        List<BaseUser> users = couchbaseOperations.findByN1QL(query, BaseUser.class);
        if (!users.isEmpty()) {
            BaseUser user = users.get(0);
            user.setLastLogin(new Date());
            baseEntityRepositoryHelper.saveEntityModel(user);
        }
    }

    @Override
    public void archiveUser(@Param("id") Long id) {

    }

    @Override
    public void unarchiveUser(@Param("id") Long id) {

    }

    public List<BaseUser> findByUsernames(String... usernames) {
        MongoTemplate mongoTemplate = ((BaseEntityMongoRepositoryHelper)baseEntityRepositoryHelper).getMongoTemplate();

        org.springframework.data.mongodb.core.query.Query  query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("credential.username").in(usernames));
        return mongoTemplate.find(query, BaseUser.class);

    }

    @Override
    public List<BaseUser> findByUsergroupId(String userGroupId) {
        MongoTemplate mongoTemplate = ((BaseEntityMongoRepositoryHelper)baseEntityRepositoryHelper).getMongoTemplate();

        org.springframework.data.mongodb.core.query.Query  query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("groups.$id").is(new ObjectId(userGroupId)));
        return mongoTemplate.find(query, BaseUser.class);
    }


    @Override
    public void acceptTermsAndConditions(@Param("id") Long id) {

    }

}