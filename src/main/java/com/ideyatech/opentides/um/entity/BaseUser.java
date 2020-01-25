package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ideyatech.opentides.core.annotation.Auditable;
import com.ideyatech.opentides.core.annotation.CbDocument;
import com.ideyatech.opentides.core.entity.user.JwtClaim;
import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.um.*;
import io.jsonwebtoken.Claims;

import org.hibernate.annotations.Type;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author allantan
 */
//@org.springframework.data.mongodb.core.mapping.Document
@Document
@Auditable(excludeFields = {"credential", "groups", "application"})
@Entity
@Table(name = "USER_PROFILE")
@Inheritance(strategy = InheritanceType.JOINED)
@CbDocument(excludeFieldsForUpdate = {
    "credential", "lastLogin", "lastLoginIP", "prevLoginIP", "lastFailedIP",
    "totalLoginCount", "failedLoginCount", "lastFailedLoginMillis", "tenant",
    "activationVerificationKey", "resetPasswordKey", "tacAcceptedTs"
})
public class BaseUser extends BaseUMEntity implements JwtClaim {

    public enum Gender {
        MALE,
        FEMALE
    }
    
    @Column(name="Provider")
	private String Provider;
	

	@Column(name="GoogleidToken",columnDefinition="TEXT")
	private String GoogleidToken;

   
	@Column(name="ProfilePhotoUrl",columnDefinition="TEXT")
	private String ProfilePhotoUrl;
	
	@Field
    @Column(name = "FIRSTNAME")
    private String firstName;

    @Field
    @Column(name = "LASTNAME")
    private String lastName;

    @Field
    @Column(name = "MIDDLENAME")
    private String middleName;

    @Field
    @Column(name = "TITLE")
    private String title;
    
    @Field
    @Column(name = "ABOUT_ME", length = 1000)
    private String aboutMe;

    @Field
    @Column(name = "EMAIL", unique = true)
    private String emailAddress;


    @Column(name = "SHOULD_RECEIVE_MAIL")
    @Field
    private Boolean receiveEmail;

    @Field
    @Column(name = "OFFICE", nullable = true)
    private String office;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    @Field
    private UserCredential credential;

    @ManyToMany
    @Field
    @JoinTable(name = "USER_GROUP", joinColumns = { @JoinColumn(name = "USER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "GROUP_ID") })
    @DBRef
    private Set<UserGroup> groups;

    @Field
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Set<Division> divisions;
    
    @Field
	@Type(type = "json")
    @Column(columnDefinition = "json")
    private Division homeDepartment;
    
    @Field
	@Type(type = "json")
    @Column(columnDefinition = "json")
    private Division homeSection;

    @Column(name = "LASTLOGIN")
    @Temporal(TemporalType.TIMESTAMP)
    @Field
    private Date lastLogin;

    @Column(name = "LANGUAGE")
    @Field
    private String language;

    @Column(name = "LAST_LOGIN_IP")
    @Field
    private String lastLoginIP;

    @Column(name = "PREV_LOGIN_IP")
    @Field
    private String prevLoginIP;

    @Column(name = "LAST_FAILED_IP")
    @Field
    private String lastFailedIP;

    @Column(name = "TOTAL_LOGIN_COUNT")
    @Field
    private Long totalLoginCount;

    @Column(name = "FAILED_LOGIN_COUNT")
    @Field
    private Long failedLoginCount;

    @Column(name = "LAST_FAILED_LOGIN_MILLIS")
    @Field
    private Long lastFailedLoginMillis;

    @Column(name = "IS_ARCHIVED")
    @Field
    private Boolean archived;

    @ManyToOne
    @JoinColumn(name = "TENANT_ID")
    @JsonIgnore
    @Field
    private Tenant tenant;

    /**
     * Company of the user
     */
    @Column(name = "COMPANY")
    @Field
    private String company;

    @Field
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @Field
    @Column(name = "WORK_CONTACT")
    private String workContact;

    @Field
    @Column(name = "MOBILE_PHONE_NUMBER")
    private String mobilePhoneNumber;

    /**
     * User department
     */
    @Column(name = "DEPARTMENT")
    @Field
    private String department;

    /**
     * Address of the user
     */
    @Column(name = "ADDRESS", length = 1000)
    @Field
    private String address;

    /**
     * Country of the user
     */
    @Column(name = "COUNTRY")
    @Field
    private String country;

    /**
     * Contact number of the user
     */
    @Column(name = "CONTACT_NUMBER")
    @Field
    private String contactNumber;

    /**
     * Birth date of the user
     */
    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.DATE)
    @Field
    private Date birthDate;

    /**
     * Gender of the user
     */
    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    @Field
    private Gender gender;

    /**
     *  Key to use to activate a user
     */
    @Column(name = "ACTIVATION_VERIFICATION_KEY")
    @Field
    private String activationVerificationKey;

    /**
     * Key to use when requesting for password reset
     */
    @Column(name = "RESET_PASSWORD_KEY")
    @Field
    private String resetPasswordKey;

    /**
     * Timestamp when Terms and Conditions were accepted.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Field
    @Column(name = "TAC_ACCEPTED_TS")
    private Date tacAcceptedTs;

    /**
     * Additonal fields value in JSON format
     */
    @Lob
    @Field
    @Column(name = "ADDTL_FIELDS_VALUES")
    private String additionalFieldsValues;

    @OneToMany(mappedBy = "baseUser")
    @MapKey(name = "customFieldKey")
    @Field
    private Map<String, BaseUserCustomValue> customValuesMap;

    /**
     * The password rule that will be use for password validation
     */
    @Transient
    private transient String passwordRule;

    @Transient
    private String userGroupName;

    /**
     * List of String of UserGroup IDs
     */
    @Transient
    private List<String> userGroupIds;

    @Transient
    private List<String> divisionKeys;
    
    @Transient
    private String homeDepartmentKey;
    
    @Transient
    private String homeSectionKey;
    
    /**
     * Key to use when requesting for password reset
     */
    @Column(name = "FACEBOOK_USER_ID")
    @Field
    private String facebookUserId;

    @Column(name = "GOOGLE_USER_ID")
    @Field
    private String googleUserId;

    /**
     * Creates a clone of this object containing basic information including the
     * following: firstName, lastName, middleName, emailAddress and lastLogin.
     * This function is used to populate the user object associated to AuditLog.
     *
     * Note: groups and credentials are not cloned.
     *
     * @return
     */
    public BaseUser cloneUserProfile() {
        final BaseUser clone = new BaseUser();
        clone.firstName = firstName;
        clone.lastName = lastName;
        clone.middleName = middleName;
        clone.emailAddress = emailAddress;
        clone.office = office;
        clone.language = language;
        clone.lastLogin = lastLogin;
        clone.credential = credential;
        clone.lastFailedIP = lastFailedIP;
        clone.lastLoginIP = lastLoginIP;
        clone.prevLoginIP = prevLoginIP;
        clone.totalLoginCount = totalLoginCount;
        clone.failedLoginCount = failedLoginCount;
        return clone;
    }
    
    public String getProvider() {
		return Provider;
	}

	public void setProvider(String provider) {
		Provider = provider;
	}

	public String getGoogleidToken() {
		return GoogleidToken;
	}

	public void setGoogleidToken(String googleidToken) {
		GoogleidToken = googleidToken;
	}


    public String getProfilePhotoUrl() {
		return ProfilePhotoUrl;
	}

	public void setProfilePhotoUrl(String profilePhotoUrl) {
		ProfilePhotoUrl = profilePhotoUrl;
	}

	public void addGroup(final UserGroup group) {
        if(this.groups == null) {
            groups = new HashSet<>();
        }
        if (group == null) {
            throw new IllegalArgumentException("Null group.");
        }
        if (groups != null) {
            groups.remove(group);
        }
        groups.add(group);
    }

    public void removeGroup(final UserGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("Null group.");
        }
        if (groups != null) {
            groups.remove(group);
        }
    }

    public void removeGroupByName(final String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            throw new IllegalArgumentException("Null group.");
        }
        if (groups != null) {
            UserGroup group = null;
            for ( UserGroup g : groups) {
                if(g.getName().equalsIgnoreCase(groupName)) {
                    group = g;
                    break;
                }
            }
            groups.remove(group);
        }
    }

    /**
     * Returns the complete name by concatenating lastName and firstName
     *
     * @return
     */
    public String getCompleteName() {
        String name = "";
        if (!StringUtil.isEmpty(getFirstName())) {
            name += getFirstName() + " ";
        }
        if (!StringUtil.isEmpty(getLastName())) {
            name += getLastName() + " ";
        }
        return name.trim();
    }

    /**
     * Returns the shortened name by concatenating firstName and lastName first letter (ex: John D.)
     *
     * @return
     */
    public String getShortenedName() {
        String name = "";
        if (!StringUtil.isEmpty(getFirstName())) {
            name += getFirstName() + " ";
        }
        if (!StringUtil.isEmpty(getLastName())) {
            name += getLastName().charAt(0) + ". ";
        }
        return name.trim();
    }

    /**
     * Returns the username from credential object
     *
     * @return
     */
    public String getUsername() {
        if (credential != null) {
            return credential.getUsername();
        } else {
            return null;
        }
    }

    /**
     * Returns Last Name, First Name Middle Name
     *
     * @return
     */
    public String getFullName() {
        String name = "";
        if (!StringUtil.isEmpty(getLastName())) {
            name += getLastName() + ", ";
        }
        if (!StringUtil.isEmpty(getFirstName())) {
            name += getFirstName();
        }
        if (!StringUtil.isEmpty(getMiddleName())) {
            name += " " + getMiddleName();
        }
        return name;
    }

    /**
     * Checks if this user has the given permission.
     *
     * @param permission
     *            the permission to check
     * @return true if user has the given permission, false otherwise
     */
    public boolean hasPermission(final String permission) {
        if (groups == null) {
            return false;
        }
        for (final UserGroup group : groups) {
            for (final UserAuthority userRole : group.getAuthorities()) {
                if (permission.equals(userRole.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get all authorities of the user
     *
     * @return a list of {@link UserAuthority} objects
     */
    public List<UserAuthority> getAuthorities() {
        final List<UserAuthority> permissions = new ArrayList<UserAuthority>();
        for (final UserGroup group : groups) {
            for (final UserAuthority userAuthority : group.getAuthorities()) {
                permissions.add(userAuthority);
            }
        }
        return permissions;
    }

    @Override
    public String getSubject() {
        return getCredential().getUsername();
    }

    @JsonIgnore
    @Override
    public Set<String> getAuths() {
        Set<String> auths = new HashSet<>();
        List<UserAuthority> authorities = getAuthorities();
        for(UserAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return auths;
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("nosqlId", getNosqlId());
        return props;
    }

    @Override
    public Map<String, Object> getProfile() {
        Map<String, Object> props = new HashMap<>();
        props.put("firstName", getFirstName());
        props.put("lastName", getLastName());
        return props;
    }

    @Override
    public void processBody(Claims body) {

    }

    @JsonIgnore
    public List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities =
                getAuths().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return grantedAuthorities;
    }

    @Override
    public String toString() {
        return getCompleteName();
    }

    //@SearchableFields
    public List<String> searchableFields() {
        final List<String> props = new ArrayList<String>();
        props.add("firstName");
        props.add("lastName");
        props.add("emailAddress");
        props.add("credential.username");
        return props;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseUser other = (BaseUser) obj;
        if (emailAddress == null) {
            if (other.emailAddress != null) {
                return false;
            }
        } else if (!emailAddress.equals(other.emailAddress)) {
            return false;
        }
        return true;
    }

    /**
     * Getter method for firstName.
     *
     * @return the firstName
     */
    public final String getFirstName() {
        return firstName;
    }

    /**
     * Setter method for firstName.
     *
     * @param firstName
     *            the firstName to set
     */
    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter method for lastName.
     *
     * @return the lastName
     */
    public final String getLastName() {
        return lastName;
    }

    /**
     * Setter method for lastName.
     *
     * @param lastName
     *            the lastName to set
     */
    public final void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter method for middleName.
     *
     * @return the middleName
     */
    public final String getMiddleName() {
        return middleName;
    }

    /**
     * Setter method for middleName.
     *
     * @param middleName
     *            the middleName to set
     */
    public final void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    /**
     * Getter method for emailAddress.
     *
     * @return the emailAddress
     */
    public final String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Setter method for emailAddress.
     *
     * @param emailAddress
     *            the emailAddress to set
     */
    public final void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the office
     */
    public final String getOffice() {
        return office;
    }

    /**
     * @param office
     *            the office to set
     */
    public final void setOffice(final String office) {
        this.office = office;
    }

    /**
     * Getter method for credential.
     *
     * @return the credential
     */
    public final UserCredential getCredential() {
        return credential;
    }

    /**
     * Setter method for credential.
     *
     * @param credential
     *            the credential to set
     */
    public final void setCredential(final UserCredential credential) {
        this.credential = credential;
        if(credential != null)
            credential.setUser(this);
    }

    /**
     * Getter method for groups.
     *
     * @return the groups
     */
    public final Set<UserGroup> getGroups() {
        return groups;
    }

    /**
     * Setter method for groups.
     *
     * @param groups
     *            the groups to set
     */
    public final void setGroups(final Set<UserGroup> groups) {
        this.groups = groups;
    }

    /**
     * Getter method for divisions.
     *
     * @return the divisions
     */
    public Set<Division> getDivisions() {
        return divisions;
    }

    /**
     * Setter method for divisions.
     *
     * @param divisions
     *            the divisions to set
     */
    public void setDivisions(Set<Division> divisions) {
        this.divisions = divisions;
    }

    /**
     * Getter method for home department.
     *
     * @return the home department
     */
    public Division getHomeDepartment() {
		return homeDepartment;
	}

    /**
     * Setter method for home department.
     *
     * @param home department
     *            the home department to set
     */
	public void setHomeDepartment(Division homeDepartment) {
		this.homeDepartment = homeDepartment;
	}

	/**
     * Getter method for home section.
     *
     * @return the home section
     */
	public Division getHomeSection() {
		return homeSection;
	}

	/**
     * Setter method for home section.
     *
     * @param home section
     *            the home section to set
     */
	public void setHomeSection(Division homeSection) {
		this.homeSection = homeSection;
	}

	/**
     * Returns the list of groups for display purposes
     *
     * @return
     */
    public final String getDisplayGroups() {
        final StringBuilder display = new StringBuilder();
        int count = 0;
        for (final UserGroup group : groups) {
            if (count++ > 0) {
                display.append(", ");
            }
            display.append(group.getName());
        }
        return display.toString();

    }
    
    /**
     * Returns the list of groups key for display purposes
     *
     * @return
     */
    public final String getDisplayGroupsKey() {
        final StringBuilder display = new StringBuilder();
        int count = 0;
        for (final UserGroup group : groups) {
            if (count++ > 0) {
                display.append(",");
            }
            display.append(group.getKey());
        }
        return display.toString();

    }

    /**
     * Getter method for lastLogin.
     *
     * @return the lastLogin
     */
    public final Date getLastLogin() {
        return lastLogin;
    }

    /**
     * Setter method for lastLogin.
     *
     * @param lastLogin
     *            the lastLogin to set
     */
    public final void setLastLogin(final Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Getter method for language.
     *
     * @return the language
     */
    public final String getLanguage() {
        return language;
    }

    /**
     * Setter method for language.
     *
     * @param language
     *            the language to set
     */
    public final void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Getter method for lastLoginIP.
     *
     * @return the lastLoginIP
     */
    public final String getLastLoginIP() {
        return lastLoginIP;
    }

    /**
     * Setter method for lastLoginIP.
     *
     * @param lastLoginIP
     *            the lastLoginIP to set
     */
    public final void setLastLoginIP(final String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    /**
     * Getter method for prevLoginIP.
     *
     * @return the prevLoginIP
     */
    public final String getPrevLoginIP() {
        return prevLoginIP;
    }

    /**
     * Setter method for prevLoginIP.
     *
     * @param prevLoginIP
     *            the prevLoginIP to set
     */
    public final void setPrevLoginIP(final String prevLoginIP) {
        this.prevLoginIP = prevLoginIP;
    }

    /**
     * Getter method for lastFailedIP.
     *
     * @return the lastFailedIP
     */
    public final String getLastFailedIP() {
        return lastFailedIP;
    }

    /**
     * Setter method for lastFailedIP.
     *
     * @param lastFailedIP
     *            the lastFailedIP to set
     */
    public final void setLastFailedIP(final String lastFailedIP) {
        this.lastFailedIP = lastFailedIP;
    }

    /**
     * Getter method for totalLoginCount.
     *
     * @return the totalLoginCount
     */
    public final Long getTotalLoginCount() {
        return totalLoginCount;
    }

    /**
     * Setter method for totalLoginCount.
     *
     * @param totalLoginCount
     *            the totalLoginCount to set
     */
    public final void setTotalLoginCount(final Long totalLoginCount) {
        this.totalLoginCount = totalLoginCount;
    }

    /**
     * Getter method for failedLoginCount.
     *
     * @return the failedLoginCount
     */
    public final Long getFailedLoginCount() {
        return failedLoginCount;
    }

    /**
     * Setter method for failedLoginCount.
     *
     * @param failedLoginCount
     *            the failedLoginCount to set
     */
    public final void setFailedLoginCount(final Long failedLoginCount) {
        this.failedLoginCount = failedLoginCount;
    }

    /**
     * Increment login count by 1
     */
    public void incrementFailedLoginCount() {
        if (failedLoginCount == null) {
            failedLoginCount = 0l;
        }
        failedLoginCount++;
    }

    /**
     * Set failedLoginCount to 0
     */
    public void resetFailedLoginCount() {
        failedLoginCount = 0l;
    }

    /**
     * @return the lastFailedLoginMillis
     */
    public Long getLastFailedLoginMillis() {
        return lastFailedLoginMillis;
    }

    /**
     * @param lastFailedLoginMillis
     *            the lastFailedLoginMillis to set
     */
    public void setLastFailedLoginMillis(final Long lastFailedLoginMillis) {
        this.lastFailedLoginMillis = lastFailedLoginMillis;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getActivationVerificationKey() {
        return activationVerificationKey;
    }

    public void setActivationVerificationKey(String activationVerificationKey) {
        this.activationVerificationKey = activationVerificationKey;
    }

    public String getResetPasswordKey() {
        return resetPasswordKey;
    }

    public void setResetPasswordKey(String resetPasswordKey) {
        this.resetPasswordKey = resetPasswordKey;
    }

    public String getPasswordRule() {
        return passwordRule;
    }

    public void setPasswordRule(String passwordRule) {
        this.passwordRule = passwordRule;
    }

    public Date getTacAcceptedTs() {
        return tacAcceptedTs;
    }

    public void setTacAcceptedTs(Date tacAcceptedTs) {
        this.tacAcceptedTs = tacAcceptedTs;
    }

    public String getAdditionalFieldsValues() {
        return additionalFieldsValues;
    }

    public void setAdditionalFieldsValues(String additionalFieldsValues) {
        this.additionalFieldsValues = additionalFieldsValues;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public List<String> getUserGroupIds() {
        List<String> list = new ArrayList<>();
        if (this.getGroups() != null) {
            for (UserGroup userGroup: this.getGroups()) {
                list.add(userGroup.getId());
            }
        }
        return list;
    }

    public void setUserGroupIds(List<String> userGroupIds) {
        this.userGroupIds = userGroupIds;
    }

    public List<String> getDivisionKeys() {
        List<String> list = new ArrayList<>();
        if( this.getDivisions() != null){
            for(Division d : this.getDivisions()){
                list.add(d.getKey());
            }
        }
        return list;
    }

    public void setDivisionKeys(List<String> divisionKeys) {
        this.divisionKeys = divisionKeys;
    }

    public String getHomeDepartmentKey() {
    	if (this.getHomeDepartment() != null){
    		homeDepartmentKey = this.getHomeDepartment().getKey();
    	}
		return homeDepartmentKey;
	}

	public void setHomeDepartmentKey(String homeDepartmentKey) {
		this.homeDepartmentKey = homeDepartmentKey;
	}

	public String getHomeSectionKey() {
		if (this.getHomeSection() != null){
			homeSectionKey = this.getHomeSection().getKey();
    	}
		return homeSectionKey;
	}

	public void setHomeSectionKey(String homeSectionKey) {
		this.homeSectionKey = homeSectionKey;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAboutMe() {
		return aboutMe;
	}
    
    public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getWorkContact() {
        return workContact;
    }

    public void setWorkContact(String workContact) {
        this.workContact = workContact;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Map<String, BaseUserCustomValue> getCustomValuesMap() {
        return customValuesMap;
    }

    public void setCustomValuesMap(Map<String, BaseUserCustomValue> customValuesMap) {
        this.customValuesMap = customValuesMap;
    }

    public void addCustomValue(String key, String value) {
        if(this.customValuesMap == null) {
            this.customValuesMap = new HashMap<>();
        }
        BaseUserCustomValue customValue = this.customValuesMap.get(key);
        if(customValue == null) {
            customValue = new BaseUserCustomValue();
            this.customValuesMap.put(key, customValue);
        }
        customValue.setValue(value);
    }

    public void removeCustomValue(String key) {
        if(this.customValuesMap != null) {
            this.customValuesMap.remove(key);
        }
    }

    @Override
    public String getAdditionalQueryClause(boolean exactMatch) {
        if(String.class.equals(com.ideyatech.opentides.um.Application.getEntityIdType())) {

        } else {
            if(!StringUtil.isEmpty(userGroupName)) {
                return " gp.name = :userGroupName ";
            }
        }
        return "";
    }

    @Override
    public String getJoinClause() {
        if(!StringUtil.isEmpty(userGroupName)) {
            return " INNER JOIN obj.groups gp ";
        }
        return "";
    }
    
    public String getFacebookUserId() {
		return facebookUserId;
	}
    
    public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

    public String getGoogleUserId() {
        return googleUserId;
    }

    public void setGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    public Boolean getReceiveEmail() {
        return receiveEmail;
    }

    public void setReceiveEmail(Boolean receiveEmail) {
        this.receiveEmail = receiveEmail;
    }
}
