package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ideyatech.opentides.core.annotation.Auditable;
import com.ideyatech.opentides.core.annotation.CbDocument;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author allantan
 */
@Document
//@org.springframework.data.mongodb.core.mapping.Document
@Auditable(excludeFields = {"users"})
@Entity
@Table(name = "USERGROUP")
@CbDocument(excludeFieldsForUpdate = {
        "users"
})
public class UserGroup extends BaseUMEntity {

    @Field
    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    @Field
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Field
    @Column(name = "_KEY")
    private String key;

    @Field
    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    @ManyToMany(mappedBy = "groups")
    @JsonIgnore
    @org.springframework.data.annotation.Transient
    private Set<BaseUser> users;

    @Field
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userGroup", fetch = FetchType.EAGER)
    private Set<UserAuthority> authorities;

    @Transient
    private String newName;

    @Transient
    @org.springframework.data.annotation.Transient
    private transient List<UserAuthority> removeList = new ArrayList<UserAuthority>(); // list of user roles for deletion

    @Transient
    @org.springframework.data.annotation.Transient
    private transient List<UserAuthority> addedList = new ArrayList<UserAuthority>(); // list of user roles to add

    @Transient
    @org.springframework.data.annotation.Transient
    private transient List<String> authorityNames; // used for checkboxes in UI

    @Transient
    @org.springframework.data.annotation.Transient
    private transient Map<String, String> userAuthorityMap; // user for permission matrix in UI

    public UserGroup() {
        authorities = new HashSet<UserAuthority>();
        authorityNames = new ArrayList<String>();
    }

    /**
     * @return the roleNames
     */
    public List<String> getAuthorityNames() {
        if (authorityNames == null || authorityNames.isEmpty())
            syncAuthorityNames();
        return authorityNames;
    }

    /**
     * @param authorityNames
     *            the authorityNames to set
     */
    public void setAuthorityNames(List<String> authorityNames) {
        removeList = new ArrayList<UserAuthority>();
        addedList = new ArrayList<UserAuthority>();
        this.authorityNames = new ArrayList<String>();
        if (authorityNames == null) {
            for (UserAuthority auth : authorities) {
                removeList.add(auth);
            }
            for (UserAuthority auth : removeList) {
                this.removeAuthority(auth);
            }
            return;
        }
        this.authorityNames.addAll(authorityNames);
        for (UserAuthority role : authorities) {
            if (authorityNames.contains(role.getAuthority())) {
                // we keep the role, and remove the name
                authorityNames.remove(role.getAuthority());
            } else {
                // this role has been removed
                removeList.add(role);
            }
        }
        for (UserAuthority role : removeList) {
            this.removeAuthority(role);
        }
        // now we need to add what's left in rNames
        for (String name : authorityNames) {
            addedList.add(new UserAuthority(this, name));
            authorities.add(new UserAuthority(this, name));
        }
    }

    /**
     * Add authority to group authorities
     *
     * @param authority
     * @return true if add successful otherwise false
     */
    public boolean addAuthority(UserAuthority authority) {
        if (authority == null)
            throw new IllegalArgumentException("Empty authority is not allowed.");
        if (authorities != null) {
            authorities.remove(authority);
        }
        authority.setUserGroup(this);
        return authorities.add(authority);
    }

    /**
     * Remove authority from group authorities
     *
     * @param authority
     * @return true if remove successful otherwise false
     */
    public boolean removeAuthority(UserAuthority authority) {
        if (authority != null)
            return authorities.remove(authority);
        else
            return false;
    }

    public void syncAuthorityNames() {
        authorityNames = new ArrayList<String>();
        for (UserAuthority auth : authorities) {
            authorityNames.add(auth.getAuthority());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Set<UserAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<UserAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * Getter method for removeList.
     *
     * @return the removeList
     */
    public final List<UserAuthority> getRemoveList() {
        return removeList;
    }

    /**
     * Getter method for addedList.
     *
     * @return the addedList
     */
    public List<UserAuthority> getAddedList() {
        return addedList;
    }

    /**
     * @return the users
     */
    public Set<BaseUser> getUsers() {
        return users;
    }

    /**
     * @param users
     *            the users to set
     */
    public void setUsers(Set<BaseUser> users) {
        this.users = users;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault the isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonIgnore
    public List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> auths = getAuthorityNames().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return auths;
    }

    public String getNewName() {
        return newName;
    }

}
