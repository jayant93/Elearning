package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ideyatech.opentides.core.entity.BaseEntity;
import org.springframework.data.couchbase.core.mapping.Document;

import javax.persistence.*;

/**
 * Entity representing the Authority of a user.
 *
 * @author allantan
 */
@Entity
@Document
//@org.springframework.data.mongodb.core.mapping.Document
@AttributeOverride(name = "id", column = @Column(insertable = false, updatable = false))
@Table(name = "USER_AUTHORITY")
public class UserAuthority extends BaseUMEntity {

    public UserAuthority() {
        super();
    }

    /**
     * @param userGroup
     * @param authority
     */
    public UserAuthority(UserGroup userGroup, String authority) {
        this.setUserGroup(userGroup);
        this.setAuthority(authority);
    }

    @Column(name = "USERNAME")
    @Field
    private String username;

    @Column(name = "AUTHORITY")
    @Field
    private String authority;

    // userGroup is nullable, to support username linkage
    @ManyToOne(optional = false)
    @JoinColumn(name = "USERGROUP_ID", nullable = true)
    @JsonIgnore
    @org.springframework.data.annotation.Transient
    private UserGroup userGroup;

    /**
     * Gets username.
     *
     * @return Value of username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets new username.
     *
     * @param username New value of username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets new authority.
     *
     * @param authority New value of authority.
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * Gets authority.
     *
     * @return Value of authority.
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Gets userGroup.
     *
     * @return Value of userGroup.
     */
    public UserGroup getUserGroup() {
        return userGroup;
    }

    /**
     * Sets new userGroup.
     *
     * @param userGroup New value of userGroup.
     */
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
}
