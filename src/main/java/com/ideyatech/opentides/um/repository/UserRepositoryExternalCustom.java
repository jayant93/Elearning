package com.ideyatech.opentides.um.repository;

import com.ideyatech.opentides.um.entity.Application;
import com.ideyatech.opentides.um.entity.BaseUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by Gino on 12/9/2016.
 */
public interface UserRepositoryExternalCustom {

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

    @RestResource(exported = false)
    @Modifying
    @Query("update BaseUser u set u.archived = true where u.id = :id")
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.archived = true where u.id = :id")
    void archiveUser(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    @org.springframework.data.mongodb.repository.Query()
    @Query("update BaseUser u set u.archived = false where u.id = :id")
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.archived = false where u.id = :id")
    void unarchiveUser(@Param("id") Long id);

    @RestResource(exported = false)
    @Modifying
    @Query("update BaseUser u set u.tacAcceptedTs = CURRENT_TIMESTAMP where u.id = :id")
    @org.springframework.data.couchbase.core.query.Query("update BaseUser u set u.tacAcceptedTs = CURRENT_TIMESTAMP where u.id = :id")
    void acceptTermsAndConditions(@Param("id") Long id);

}
