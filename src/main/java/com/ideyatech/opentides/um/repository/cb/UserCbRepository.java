package com.ideyatech.opentides.um.repository.cb;

import com.couchbase.client.java.query.N1qlQuery;
import com.ideyatech.opentides.core.repository.BaseEntityRepositoryHelper;
import com.ideyatech.opentides.core.repository.cb.BaseEntityRepositoryHelperCbImpl;
import com.ideyatech.opentides.core.util.JwtUtil;
import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import com.ideyatech.opentides.um.entity.UserCredential;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.repository.projections.BaseUserProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.repository.query.StringN1qlBasedQuery;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

/**
 * Created by Gino on 8/25/2016.
 */
@RepositoryRestResource(path = "/user", excerptProjection = BaseUserProjection.class)
@N1qlPrimaryIndexed
public interface UserCbRepository extends UserRepository<String>, UserCbRepositoryCustom {

    @RestResource(exported = false)
    @Modifying
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.tacAcceptedTs = CURRENT_TIMESTAMP where u.id = :id")
    void acceptTermsAndConditions(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.archived = true where u.id = :id")
    void archiveUser(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.archived = false where u.id = :id")
    void unarchiveUser(@Param("id") Long id);

    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and any d in divisions satisfies d.`key` = $1 " +
    		"or homeDepartment.`key` = $1 end")
    List<BaseUser> findByOrgUnit(@Param("name") String divisionName);

    @org.springframework.data.couchbase.core.query.Query(value = "#{#n1ql.selectEntity} " +
            "where #{#n1ql.filter} and " +
            "((any d in divisions satisfies d.`key` in #{#orgUnits} end) " +
            "or homeDepartment.`key` in #{#orgUnits})")
    List<BaseUser> findByOrgUnitList(@Param("orgUnits") String orgUnitKeyList);
}

interface UserCbRepositoryCustom {
    List<BaseUser> findByUsernames(String ... usernames);
}

class UserCbRepositoryCustomImpl {

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

    public void updateFailedLogin(String username, long timestamp) {

    }

    public void updateLastLogin(@Param("username") String username) {
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

    public List<BaseUser> findByUsernames(String ... usernames) {
        CouchbaseOperations couchbaseOperations =
                ((BaseEntityRepositoryHelperCbImpl)baseEntityRepositoryHelper).getCouchbaseOperations();
        StringN1qlBasedQuery.N1qlSpelValues spelValues = StringN1qlBasedQuery.createN1qlSpelValues(couchbaseOperations.getCouchbaseBucket().name(),
                couchbaseOperations.getConverter().getTypeKey(), BaseUser.class, false);
        String n1ql = spelValues.selectEntity + " where  " + spelValues.filter + " and credential.username in [%s]";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < usernames.length; i++) {
            String username = usernames[i];
            sb.append("\"" + username + "\"");
            if(i != usernames.length - 1) {
                sb.append(",");
            }
        }
        n1ql = String.format(n1ql, sb.toString());
        N1qlQuery query = N1qlQuery.simple(n1ql);

        return couchbaseOperations.findByN1QL(query, BaseUser.class);
    }
}