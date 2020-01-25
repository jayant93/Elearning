package com.ideyatech.opentides.um.entity;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ideyatech.opentides.um.entity.BaseUser.Gender;

import io.jsonwebtoken.Claims;

public class BaseUserTest {
	private BaseUser baseUser;
	private BaseUser anotherBaseUser;
	
	private final String name="NAME";
	private final String username="USER_NAME";
	private final String setA = "SET_A";
	private final String setB = "SET_B";

	private final UserGroup userGroup = new UserGroup();
	private final UserGroup newUserGroup = new UserGroup();
	private final UserGroup anotherUserGroup = new UserGroup();

	private final Set<UserAuthority>authorities = new HashSet<UserAuthority>();
	private final List<UserAuthority> permissions = new ArrayList<UserAuthority>();
	private final UserAuthority userAuthority= new UserAuthority();
	private final UserAuthority differentPermissionAuthority= new UserAuthority();
	private final String permission = "PERMISSION";
	private final String differentPermission="DIFFERENT_PERMISSION";

	private final String nosqlId = "NOSQL_ID";
	
	private final Map<String, BaseUserCustomValue> customValuesMap = new HashMap<>();
	private final String mobilePhoneNumber = "MOBILE_PHONE_NUMBER";
	private final String facebookUserId = "FACEBOOK_USER_ID";
	private final String workContact = "WORK_CONTACT";
	private final String jobTitle = "JOB_TITLE";
	private final String aboutMe = "ABOUT_ME";
	private final String title = "TITLE";
	private final String userGroupName = "USER_GROUP_NAME";
	private final String additionalFieldsValues = "ADDITIONAL_FIELDS_VALUES";
    private final Date tacAcceptedTs = new Date(System.currentTimeMillis()+100);
	private final String passwordRule = "PASSWORD_RULE";
	private final String resetPasswordKey = "RESET_PASSWORD_KEY";
	private final String activationVerificationKey = "ACTIVATION_VERIFICATION_KEY";
	private final Gender gender = Gender.MALE;
    private final Date birthDate = new Date(System.currentTimeMillis()+200);
	private final String contactNumber = "CONTACT_NUMBER";
	private final String country = "COUNTRY";
	private final String address = "ADDRESS";
	private final String department = "DEPARTMENT";
	private final String company = "COMPANY";
	private final Tenant tenant = new Tenant();
	private final Boolean archived = true;
	private final Long lastFailedLoginMillis = 100l;
	private final Long failedLoginCount = 200l;
	private final Long totalLoginCount = 300l;
	private final String lastFailedIP = "LAST_FAILED_IP";
	private final String prevLoginIP = "PREV_LOGIN_IP";
	private final String lastLoginIP = "LAST_LOGIN_IP";
	private final String language = "LANGUAGE";
    private final Date lastLogin = new Date(System.currentTimeMillis()+300);
	private final HashSet<UserGroup>groups= new HashSet<UserGroup>();
	private final UserCredential credential= new UserCredential();
	private final String office = "OFFICE";
	private final String emailAddress = "EMAIL_ADDRESS";
	private final String middleName = "MIDDLE_NAME";
	private final String lastName = "LAST_NAME";
	private final String firstName = "FIRST_NAME";
	private final List<String> userGroupIds = new ArrayList<String>();
	

	private final Map<String, BaseUserCustomValue> newCustomValuesMap = new HashMap<>();
	private final String newMobilePhoneNumber = "NEW_MOBILE_PHONE_NUMBER";
	private final String newFacebookUserId = "NEW_FACEBOOK_USER_ID";
	private final String newWorkContact = "NEW_WORK_CONTACT";
	private final String newJobTitle = "NEW_JOB_TITLE";
	private final String newAboutMe = "NEW_ABOUT_ME";
	private final String newTitle = "NEW_TITLE";
	private final String newUserGroupName = "NEW_USER_GROUP_NAME";
	private final String newAdditionalFieldsValues = "NEW_ADDITIONAL_FIELDS_VALUES";
    private final Date newTacAcceptedTs = new Date(System.currentTimeMillis()+1000);
	private final String newPasswordRule = "NEW_PASSWORD_RULE";
	private final String newResetPasswordKey = "NEW_RESET_PASSWORD_KEY";
	private final String newActivationVerificationKey = "NEW_ACTIVATION_VERIFICATION_KEY";
	private final Gender newGender = Gender.FEMALE;
    private final Date newBirthDate = new Date(System.currentTimeMillis()+2000);
	private final String newContactNumber = "NEW_CONTACT_NUMBER";
	private final String newCountry = "NEW_COUNTRY";
	private final String newAddress = "NEW_ADDRESS";
	private final String newDepartment = "NEW_DEPARTMENT";
	private final String newCompany = "NEW_COMPANY";
	private final Tenant newTenant = new Tenant();
	private final Boolean newArchived = false;
	private final Long newLastFailedLoginMillis = 1000l;
	private final Long newFailedLoginCount = 2000l;
	private final Long newTotalLoginCount = 3000l;
	private final String newLastFailedIP = "NEW_LAST_FAILED_IP";
	private final String newPrevLoginIP = "NEW_PREV_LOGIN_IP";
	private final String newLastLoginIP = "NEW_LAST_LOGIN_IP";
	private final String newLanguage = "NEW_LANGUAGE";
    private final Date newLastLogin = new Date(System.currentTimeMillis()+3000);
	private final HashSet<UserGroup>newGroups= new HashSet<UserGroup>();
	private final UserCredential newCredential= new UserCredential();
	private final String newOffice = "NEW_OFFICE";
	private final String newEmailAddress = "NEW_EMAIL_ADDRESS";
	private final String newMiddleName = "NEW_MIDDLE_NAME";
	private final String newLastName = "NEW_LAST_NAME";
	private final String newFirstName = "NEW_FIRST_NAME";
	private final List<String> newUserGroupIds = new ArrayList<String>();
	

	private final BaseUserCustomValue baseUserCustomValue= new BaseUserCustomValue();
	private final BaseUserCustomValue anotherBaseUserCustomValue= new BaseUserCustomValue();
	private final String key ="KEY";
	private final String anotherKey ="ANOTHER_KEY";
	private final String newKey = "NEW_KEY";

	private final String value = "VALUE";
	private final String anotherValue = "ANOTHER_VALUE";
	private final String newValue = "NEW_VALUE";
	/*
	 * Initialization
	 */
	@Before
	public void init()throws Exception{
		
		//dirty solution to reset the static variable 'entityId'
		Class clazz = com.ideyatech.opentides.um.Application.class;
		String fieldName = "entityIdType";
		final Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		final Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, null);
		tenant.setName(name);
		
		differentPermissionAuthority.setAuthority(differentPermission);
		userAuthority.setAuthority(permission);
		authorities.add(differentPermissionAuthority);
		authorities.add(userAuthority);
		userGroup.setAuthorities(authorities);
		for(UserAuthority userAuthority : authorities){
			permissions.add(userAuthority);
		}
		
		userGroup.setName(name);
		newUserGroup.setName(firstName);
		anotherUserGroup.setName(lastName);
		
		credential.setUsername(username);
		
		userGroupIds.add(setA);
		newUserGroupIds.add(setB);
		
		baseUserCustomValue.setValue(value);
		customValuesMap.put(key, baseUserCustomValue);
		anotherBaseUserCustomValue.setValue(anotherValue);
		newCustomValuesMap.put(anotherKey, anotherBaseUserCustomValue);
		
		baseUser= new BaseUser();
		baseUser.setMobilePhoneNumber(mobilePhoneNumber);
		baseUser.setFacebookUserId(facebookUserId);
		baseUser.setWorkContact(workContact);
		baseUser.setJobTitle(jobTitle);
		baseUser.setAboutMe(aboutMe);
		baseUser.setTitle(title);
		baseUser.setUserGroupName(userGroupName);
		baseUser.setAdditionalFieldsValues(additionalFieldsValues);
		baseUser.setTacAcceptedTs(tacAcceptedTs);
		baseUser.setPasswordRule(passwordRule);
		baseUser.setResetPasswordKey(resetPasswordKey);
		baseUser.setActivationVerificationKey(activationVerificationKey);
		baseUser.setGender(gender);
		baseUser.setBirthDate(birthDate);
		baseUser.setContactNumber(contactNumber);
		baseUser.setCountry(country);
		baseUser.setAddress(address);
		baseUser.setDepartment(department);
		baseUser.setCompany(company);
		baseUser.setTenant(tenant);
		baseUser.setArchived(archived);
		baseUser.setLastFailedLoginMillis(lastFailedLoginMillis);
		baseUser.setFailedLoginCount(failedLoginCount);
		baseUser.setTotalLoginCount(totalLoginCount);
		baseUser.setLastFailedIP(lastFailedIP);
		baseUser.setPrevLoginIP(prevLoginIP);
		baseUser.setLastLoginIP(lastLoginIP);
		baseUser.setLanguage(language);
		baseUser.setLastLogin(lastLogin);
		groups.add(userGroup);
		groups.add(anotherUserGroup);
		baseUser.setGroups(groups);
		baseUser.setCredential(credential);
		baseUser.setOffice(office);
		baseUser.setEmailAddress(emailAddress);
		baseUser.setMiddleName(middleName);
		baseUser.setLastName(lastName);
		baseUser.setFirstName(firstName);
		baseUser.setUserGroupIds(userGroupIds);
		baseUser.setCustomValuesMap(customValuesMap);
		
		baseUser.setNosqlId(nosqlId);



		anotherBaseUser= new BaseUser();
		anotherBaseUser.setMobilePhoneNumber(mobilePhoneNumber);
		anotherBaseUser.setFacebookUserId(facebookUserId);
		anotherBaseUser.setWorkContact(workContact);
		anotherBaseUser.setJobTitle(jobTitle);
		anotherBaseUser.setAboutMe(aboutMe);
		anotherBaseUser.setTitle(title);
		anotherBaseUser.setUserGroupName(userGroupName);
		anotherBaseUser.setAdditionalFieldsValues(additionalFieldsValues);
		anotherBaseUser.setTacAcceptedTs(tacAcceptedTs);
		anotherBaseUser.setPasswordRule(passwordRule);
		anotherBaseUser.setResetPasswordKey(resetPasswordKey);
		anotherBaseUser.setActivationVerificationKey(activationVerificationKey);
		anotherBaseUser.setGender(gender);
		anotherBaseUser.setBirthDate(birthDate);
		anotherBaseUser.setContactNumber(contactNumber);
		anotherBaseUser.setCountry(country);
		anotherBaseUser.setAddress(address);
		anotherBaseUser.setDepartment(department);
		anotherBaseUser.setCompany(company);
		anotherBaseUser.setTenant(tenant);
		anotherBaseUser.setArchived(archived);
		anotherBaseUser.setLastFailedLoginMillis(lastFailedLoginMillis);
		anotherBaseUser.setFailedLoginCount(failedLoginCount);
		anotherBaseUser.setTotalLoginCount(totalLoginCount);
		anotherBaseUser.setLastFailedIP(lastFailedIP);
		anotherBaseUser.setPrevLoginIP(prevLoginIP);
		anotherBaseUser.setLastLoginIP(lastLoginIP);
		anotherBaseUser.setLanguage(language);
		anotherBaseUser.setLastLogin(lastLogin);
		anotherBaseUser.setGroups(groups);
		anotherBaseUser.setCredential(credential);
		anotherBaseUser.setOffice(office);
		anotherBaseUser.setEmailAddress(emailAddress);
		anotherBaseUser.setMiddleName(middleName);
		anotherBaseUser.setLastName(lastName);
		anotherBaseUser.setFirstName(firstName);
		anotherBaseUser.setUserGroupIds(userGroupIds);
		anotherBaseUser.setCustomValuesMap(customValuesMap);
		
		anotherBaseUser.setNosqlId(nosqlId);
		
	}
	/*
	 * Tests cloneUserProfile() method of BaseUser.java
	 */
	@Test
	public void testCloneUserProfile(){
		BaseUser cloned = baseUser.cloneUserProfile();
		assertEquals("Gets First Name:",firstName,cloned.getFirstName());
		assertEquals("Gets Last Name:",lastName,cloned.getLastName());
		assertEquals("Gets Middle Name:",middleName,cloned.getMiddleName());
		assertEquals("Gets Email Address:",emailAddress,cloned.getEmailAddress());
		assertEquals("Gets Office:",office,cloned.getOffice());
		assertEquals("Gets Language:",language,cloned.getLanguage());
		assertEquals("Gets Last Login:",lastLogin,cloned.getLastLogin());
		assertEquals("Gets Last Failed IP:",lastFailedIP,cloned.getLastFailedIP());
		assertEquals("Gets Last Login IP:",lastLoginIP,cloned.getLastLoginIP());
		assertEquals("Gets Previous Login IP:",prevLoginIP,cloned.getPrevLoginIP());
		assertEquals("Gets Total Login Count:",totalLoginCount,cloned.getTotalLoginCount());
		assertEquals("Gets Failed Login Count:",failedLoginCount,cloned.getFailedLoginCount());
	}
	/*
	 * Tests addGroup() method of BaseUser.java
	 */
	@Test
	public void testAddGroup(){
		baseUser.addGroup(userGroup);
		assertEquals("Checks Group:",true,baseUser.getGroups().contains(userGroup));
	}	
	/*
	 * Tests addGroup() method of BaseUser.java
	 * when groups is null and group is also null
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testAddGroupWhenGroupsAndGroupAreNull(){
		baseUser.setGroups(null);
		baseUser.addGroup(null);
		assertEquals("Checks Group:",true,baseUser.getGroups().contains(userGroup));
	}

	/*
	 * Tests removeGroup() method of BaseUser.java
	 */
	@Test
	public void testRemoveGroup(){
		baseUser.removeGroup(userGroup);
		assertEquals("Checks Group:",false,baseUser.getGroups().contains(userGroup));
	}
	/*
	 * Tests removeGroup() method of BaseUser.java
	 * when group is null
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveGroupWhenGroupIsNull(){
		baseUser.removeGroup(null);
		assertEquals("Checks Group:",false,baseUser.getGroups().contains(userGroup));
	}	
	/*
	 * Tests removeGroup() method of BaseUser.java
	 * when groups is null
	 */
	@Test
	public void testRemoveGroupWhenGroupsIsNull(){
		baseUser.setGroups(null);
		baseUser.removeGroup(userGroup);
		assertEquals("Checks Groups:",null,baseUser.getGroups());
	}	
	/*
	 * Tests removeGroupByName() method of BaseUser.java
	 */
	@Test
	public void testRemoveGroupByName(){
		baseUser.removeGroupByName(name);
		assertEquals("Checks Group:",false,baseUser.getGroups().contains(userGroup));
	}
	/*
	 * Tests removeGroupByName() method of BaseUser.java
	 * when group name is null
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveGroupByNameWhenNameIsNull(){
		baseUser.removeGroupByName(null);
		assertEquals("Checks Group:",false,baseUser.getGroups().contains(userGroup));
	}
	/*
	 * Tests removeGroupByName() method of BaseUser.java
	 * when group name is empty
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveGroupByNameWhenNameIsEmpty(){
		baseUser.removeGroupByName("");
		assertEquals("Checks Group:",false,baseUser.getGroups().contains(userGroup));
	}	
	/*
	 * Tests removeGroupByName() method of BaseUser.java
	 * when groups is null
	 */
	@Test 
	public void testRemoveGroupByNameWhenGroupsIsNull(){
		baseUser.setGroups(null);
		baseUser.removeGroupByName(name);
		assertEquals("Checks Groups:",null,baseUser.getGroups());
	}	
	/*
	 *Tests getUserGroupIds() method of BaseUser.java
	 */
	
	
	
//	@Test  //chang by op
//	public void testGetUserGroupIds() {
//		assertEquals("Gets UserGroupIds:",userGroupIds,baseUser.getUserGroupIds());
//	}
//	/*
//	 *Tests setUserGroupIds() method of BaseUser.java
//	 */
//	
//	
//	@Test   //chang by op
//	public void testSetNewUserGroupIds() {
//		baseUser.setUserGroupIds(newUserGroupIds);
//		assertEquals("Gets new UserGroupIds:",newUserGroupIds,baseUser.getUserGroupIds());
//	}
	
	
	/*
	 * Tests getFullName() method of BaseUser.java
	 */
	@Test
	public void testGetFullName(){
		assertEquals("Gets Full Name:",lastName+", "+firstName+" "+middleName,baseUser.getFullName());
		
	}
	/*
	 * Tests getFullName() method of BaseUser.java
	 * when lastName,firstName and middleName are empty
	 */
	@Test
	public void testGetFullNameEmpty(){
		baseUser.setFirstName("");
		baseUser.setLastName("");
		baseUser.setMiddleName("");
		assertEquals("Gets Full Name:","",baseUser.getFullName());
		
	}
	/*
	 * Tests hasPermission() method of BaseUser.java
	 */
	@Test
	public void testHasPermission(){
		assertEquals("Checks Permission:",true,baseUser.hasPermission(permission));
	}

	/*
	 * Tests hasPermission() method of BaseUser.java
	 * when permission is not found
	 */
	@Test
	public void testHasNoPermission(){
		assertEquals("Checks Permission:",false,baseUser.hasPermission("RANDOM_STRING"));
	}

	/*
	 * Tests hasPermission() method of BaseUser.java
	 * when groups is null
	 */
	@Test
	public void testHasPermissionGroupsIsNull(){
		baseUser.setGroups(null);
		assertEquals("Checks Permission:",false,baseUser.hasPermission(permission));
	}
	
	/*
	 * Tests getAuthorities() method of BaseUser.java
	 */
	@Test
	public void testGetAuthorities(){
		assertEquals("Checks Permissions:",permissions,baseUser.getAuthorities());
	}
	/*
	 * Tests getSubject() method of BaseUser.java
	 */
	@Test
	public void testGetSubject(){
		assertEquals("Checks Subject:",credential.getUsername(),baseUser.getSubject());
	}
	/*
	 * Tests getAuths() method of BaseUser.java
	 */
	@Test 
	public void testGetAuths(){
		Set<String> permissionSet = new HashSet<String>();
		permissionSet.add(permission);
		permissionSet.add(differentPermission);
		assertEquals("Checks Auths:",permissionSet,baseUser.getAuths());
	}
	/*
	 * Tests getAdditionalProperties() method of BaseUser.java
	 */
	@Test
	public void testGetAdditionalProperties(){
		Map<String, Object> props =new HashMap<>();
		props.put("nosqlId",nosqlId);
		assertEquals("Checks AdditionalProperties:",props,baseUser.getAdditionalProperties());
	}
	
	/*
	 * Tests processBody() method of BaseUser.java
	 */
	public void testProcessBody(){
		baseUser.processBody(new FakeClaims());
	}
	
	/*
	 * Tests getGrantedAuthorities() method of BaseUser.java
	 */
	@Test
	public void testGetGrantedAuthorities(){
        List<GrantedAuthority> grantedAuthorities =
        		baseUser.getAuths().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        
        assertEquals("Checks Granted Authorities:",grantedAuthorities,baseUser.getGrantedAuthorities());
	}
	/*
	 * Tests searchableFields() method of BaseUser.java
	 */
	@Test
	public void testSearchableFields(){
        List<String> props = new ArrayList<String>();
        props.add("firstName");
        props.add("lastName");
        props.add("emailAddress");
        props.add("credential.username");
        assertEquals("Checks Searchable Fields:",props,baseUser.searchableFields());
	}
	/*
	 * Tests hashCode() method BaseUser.java
	 */
	@Test
	public void testHashCodeEmailNotNull(){
		assertEquals("Checks Hashcode:",true,((Integer)baseUser.hashCode())instanceof Integer);
		baseUser.setEmailAddress(null);
		assertEquals("Checks Hashcode:",true,((Integer)baseUser.hashCode())instanceof Integer);
	}


	/*
	 * Tests equal() method BaseUser.java when the parameter passed is itself
	 */
	@Test
	public void testEqualSameObject(){
		assertEquals("Checks same object",true,baseUser.equals(baseUser));
	}
	/*
	 * Tests equal() method BaseUser.java when the parameter passed is not an instance BaseUser.java
	 */
	@Test
	public void testEqualNotInstanceOfBaseUser(){
		assertEquals("Checks non-instance of BaseUser",false,baseUser.equals(new Object()));
	}
	/*
	 * Tests equal() method BaseUser.java when the parameter passed
	 * which is an instance BaseUser.java and has the same values in variables.
	 */
	@Test
	public void testEqualSameValues(){
		assertEquals("Checks BaseUser ",false,baseUser.equals(anotherBaseUser));
	}	
	/*
	 * Tests equal() method BaseUser.java when the parameter passed
	 * is null
	 */
	@Test
	public void testEqualNull(){
		assertEquals("Checks BaseUser",false,baseUser.equals(null));
	}		
	
	/*
	 * Tests getUsername() method of BaseUser.java
	 */
	@Test
	public void testGetUsername(){
		assertEquals("Gets Username:",credential.getUsername(),baseUser.getUsername());
		
	}	
	/*
	 * Tests getUsername() method of BaseUser.java
	 * when credential is null
	 */
	@Test
	public void testGetUsernameNull(){
		baseUser.setCredential(null);
		assertEquals("Gets Username:",null,baseUser.getUsername());
		
	}
	
	/*
	 * Tests getCompleteName() method of BaseUser.java
	 */
	@Test
	public void testGetCompleteName(){
		assertEquals("Gets Complete Name:",firstName+" "+lastName,baseUser.getCompleteName());
	}
	/*
	 * Tests getCompleteName() method of BaseUser.java
	 * when firstName and lastName are empty
	 */
	@Test
	public void testGetCompleteNameAllEmpty(){
		baseUser.setFirstName("");
		baseUser.setLastName("");
		assertEquals("Gets Complete Name:","",baseUser.getCompleteName());
	}
	/*
	 * Tests toString() method of BaseUser.java
	 */
	@Test
	public void testToString(){
		assertEquals("Gets String:",firstName+" "+lastName,baseUser.toString());
	}
	
	/*
	 *Tests getFirstName() method of BaseUser.java
	 */
	@Test
	public void testGetFirstName() {
		assertEquals("Gets FirstName:",firstName,baseUser.getFirstName());
	}
	/*
	 *Tests setFirstName() method of BaseUser.java
	 */
	@Test
	public void testSetNewFirstName() {
		baseUser.setFirstName(newFirstName);
		assertEquals("Gets new FirstName:",newFirstName,baseUser.getFirstName());
	}
	/*
	 *Tests getLastName() method of BaseUser.java
	 */
	@Test
	public void testGetLastName() {
		assertEquals("Gets LastName:",lastName,baseUser.getLastName());
	}
	/*
	 *Tests setLastName() method of BaseUser.java
	 */
	@Test
	public void testSetNewLastName() {
		baseUser.setLastName(newLastName);
		assertEquals("Gets new LastName:",newLastName,baseUser.getLastName());
	}
	/*
	 *Tests getMiddleName() method of BaseUser.java
	 */
	@Test
	public void testGetMiddleName() {
		assertEquals("Gets MiddleName:",middleName,baseUser.getMiddleName());
	}
	/*
	 *Tests setMiddleName() method of BaseUser.java
	 */
	@Test
	public void testSetNewMiddleName() {
		baseUser.setMiddleName(newMiddleName);
		assertEquals("Gets new MiddleName:",newMiddleName,baseUser.getMiddleName());
	}
	/*
	 *Tests getEmailAddress() method of BaseUser.java
	 */
	@Test
	public void testGetEmailAddress() {
		assertEquals("Gets EmailAddress:",emailAddress,baseUser.getEmailAddress());
	}
	/*
	 *Tests setEmailAddress() method of BaseUser.java
	 */
	@Test
	public void testSetNewEmailAddress() {
		baseUser.setEmailAddress(newEmailAddress);
		assertEquals("Gets new EmailAddress:",newEmailAddress,baseUser.getEmailAddress());
	}
	/*
	 *Tests getOffice() method of BaseUser.java
	 */
	@Test
	public void testGetOffice() {
		assertEquals("Gets Office:",office,baseUser.getOffice());
	}
	/*
	 *Tests setOffice() method of BaseUser.java
	 */
	@Test
	public void testSetNewOffice() {
		baseUser.setOffice(newOffice);
		assertEquals("Gets new Office:",newOffice,baseUser.getOffice());
	}
	/*
	 *Tests getCredential() method of BaseUser.java
	 */
	@Test
	public void testGetCredential() {
		assertEquals("Gets Credential:",credential,baseUser.getCredential());
	}
	/*
	 *Tests setCredential() method of BaseUser.java
	 */
	@Test
	public void testSetNewCredential() {
		baseUser.setCredential(newCredential);
		assertEquals("Gets new Credential:",newCredential,baseUser.getCredential());
	}

	
	/*
	 *Tests getGroups() method of BaseUser.java
	 */
	@Test
	public void testGetGroups() {
		assertEquals("Gets Groups:",groups,baseUser.getGroups());
	}
	/*
	 *Tests setGroups() method of BaseUser.java
	 */
	@Test
	public void testSetNewGroups() {
		baseUser.setGroups(newGroups);
		assertEquals("Gets new Groups:",newGroups,baseUser.getGroups());
	}
	
	/*
	 * Tests getDisplayGroups() method of BaseUser.java
	 */
	@Test
	public void testGetDisplayGroups(){
        final StringBuilder display = new StringBuilder();
        int count = 0;
        for (final UserGroup group : groups) {
            if (count++ > 0) {
                display.append(", ");
            }
            display.append(group.getName());
        }

		assertEquals("Gets Display Groups:", display.toString(),baseUser.getDisplayGroups());
	}
	/*
	 *Tests getLastLogin() method of BaseUser.java
	 */
	@Test
	public void testGetLastLogin() {
		assertEquals("Gets LastLogin:",lastLogin,baseUser.getLastLogin());
	}
	/*
	 *Tests setLastLogin() method of BaseUser.java
	 */
	@Test
	public void testSetNewLastLogin() {
		baseUser.setLastLogin(newLastLogin);
		assertEquals("Gets new LastLogin:",newLastLogin,baseUser.getLastLogin());
	}
	/*
	 *Tests getLanguage() method of BaseUser.java
	 */
	@Test
	public void testGetLanguage() {
		assertEquals("Gets Language:",language,baseUser.getLanguage());
	}
	/*
	 *Tests setLanguage() method of BaseUser.java
	 */
	@Test
	public void testSetNewLanguage() {
		baseUser.setLanguage(newLanguage);
		assertEquals("Gets new Language:",newLanguage,baseUser.getLanguage());
	}
	/*
	 *Tests getLastLoginIP() method of BaseUser.java
	 */
	@Test
	public void testGetLastLoginIP() {
		assertEquals("Gets LastLoginIP:",lastLoginIP,baseUser.getLastLoginIP());
	}
	/*
	 *Tests setLastLoginIP() method of BaseUser.java
	 */
	@Test
	public void testSetNewLastLoginIP() {
		baseUser.setLastLoginIP(newLastLoginIP);
		assertEquals("Gets new LastLoginIP:",newLastLoginIP,baseUser.getLastLoginIP());
	}
	/*
	 *Tests getPrevLoginIP() method of BaseUser.java
	 */
	@Test
	public void testGetPrevLoginIP() {
		assertEquals("Gets PrevLoginIP:",prevLoginIP,baseUser.getPrevLoginIP());
	}
	/*
	 *Tests setPrevLoginIP() method of BaseUser.java
	 */
	@Test
	public void testSetNewPrevLoginIP() {
		baseUser.setPrevLoginIP(newPrevLoginIP);
		assertEquals("Gets new PrevLoginIP:",newPrevLoginIP,baseUser.getPrevLoginIP());
	}
	/*
	 *Tests getLastFailedIP() method of BaseUser.java
	 */
	@Test
	public void testGetLastFailedIP() {
		assertEquals("Gets LastFailedIP:",lastFailedIP,baseUser.getLastFailedIP());
	}
	/*
	 *Tests setLastFailedIP() method of BaseUser.java
	 */
	@Test
	public void testSetNewLastFailedIP() {
		baseUser.setLastFailedIP(newLastFailedIP);
		assertEquals("Gets new LastFailedIP:",newLastFailedIP,baseUser.getLastFailedIP());
	}
	/*
	 *Tests getTotalLoginCount() method of BaseUser.java
	 */
	@Test
	public void testGetTotalLoginCount() {
		assertEquals("Gets TotalLoginCount:",totalLoginCount,baseUser.getTotalLoginCount());
	}
	/*
	 *Tests setTotalLoginCount() method of BaseUser.java
	 */
	@Test
	public void testSetNewTotalLoginCount() {
		baseUser.setTotalLoginCount(newTotalLoginCount);
		assertEquals("Gets new TotalLoginCount:",newTotalLoginCount,baseUser.getTotalLoginCount());
	}
	/*
	 *Tests getFailedLoginCount() method of BaseUser.java
	 */
	@Test
	public void testGetFailedLoginCount() {
		assertEquals("Gets FailedLoginCount:",failedLoginCount,baseUser.getFailedLoginCount());
	}
	/*
	 *Tests setFailedLoginCount() method of BaseUser.java
	 */
	@Test
	public void testSetNewFailedLoginCount() {
		baseUser.setFailedLoginCount(newFailedLoginCount);
		assertEquals("Gets new FailedLoginCount:",newFailedLoginCount,baseUser.getFailedLoginCount());
	}
	
	
	/*
	 * Tests incrementFailedLoginCount() method of BaseUser.java
	 * when FailedLoginCount is not null
	 */
	@Test
	public void testIncrementFailedLoginCount(){
		baseUser.incrementFailedLoginCount();
		assertEquals("Gets new FailedLoginCount:",(Long)(failedLoginCount+1l),baseUser.getFailedLoginCount());
	}
	
	/*
	 * Tests incrementFailedLoginCount() method of BaseUser.java
	 * when FailedLoginCount is null
	 */
	@Test
	public void testIncrementFailedLoginCountNull(){
		baseUser.setFailedLoginCount(null);
		baseUser.incrementFailedLoginCount();
		assertEquals("Gets new FailedLoginCount:",(Long)1l,baseUser.getFailedLoginCount());
	}
	
	/*
	 * Tests resetFailedLoginCount() method of BaseUser.java
	 */
	@Test
	public void testResetFailedLoginCount(){
		baseUser.resetFailedLoginCount();
		assertEquals("Gets new FailedLoginCount:",(Long)0l,baseUser.getFailedLoginCount());
	}
	
	/*
	 *Tests getLastFailedLoginMillis() method of BaseUser.java
	 */
	@Test
	public void testGetLastFailedLoginMillis() {
		assertEquals("Gets LastFailedLoginMillis:",lastFailedLoginMillis,baseUser.getLastFailedLoginMillis());
	}
	/*
	 *Tests setLastFailedLoginMillis() method of BaseUser.java
	 */
	@Test
	public void testSetNewLastFailedLoginMillis() {
		baseUser.setLastFailedLoginMillis(newLastFailedLoginMillis);
		assertEquals("Gets new LastFailedLoginMillis:",newLastFailedLoginMillis,baseUser.getLastFailedLoginMillis());
	}
	/*
	 *Tests getArchived() method of BaseUser.java
	 */
	@Test
	public void testGetArchived() {
		assertEquals("Gets Archived:",archived,baseUser.getArchived());
	}
	/*
	 *Tests setArchived() method of BaseUser.java
	 */
	@Test
	public void testSetNewArchived() {
		baseUser.setArchived(newArchived);
		assertEquals("Gets new Archived:",newArchived,baseUser.getArchived());
	}
	/*
	 *Tests getTenant() method of BaseUser.java
	 */
	@Test
	public void testGetTenant() {
		assertEquals("Gets Tenant:",tenant,baseUser.getTenant());
	}
	/*
	 *Tests setTenant() method of BaseUser.java
	 */
	@Test
	public void testSetNewTenant() {
		baseUser.setTenant(newTenant);
		assertEquals("Gets new Tenant:",newTenant,baseUser.getTenant());
	}
	/*
	 *Tests getCompany() method of BaseUser.java
	 */
	@Test
	public void testGetCompany() {
		assertEquals("Gets Company:",company,baseUser.getCompany());
	}
	/*
	 *Tests setCompany() method of BaseUser.java
	 */
	@Test
	public void testSetNewCompany() {
		baseUser.setCompany(newCompany);
		assertEquals("Gets new Company:",newCompany,baseUser.getCompany());
	}
	/*
	 *Tests getDepartment() method of BaseUser.java
	 */
	@Test
	public void testGetDepartment() {
		assertEquals("Gets Department:",department,baseUser.getDepartment());
	}
	/*
	 *Tests setDepartment() method of BaseUser.java
	 */
	@Test
	public void testSetNewDepartment() {
		baseUser.setDepartment(newDepartment);
		assertEquals("Gets new Department:",newDepartment,baseUser.getDepartment());
	}
	/*
	 *Tests getAddress() method of BaseUser.java
	 */
	@Test
	public void testGetAddress() {
		assertEquals("Gets Address:",address,baseUser.getAddress());
	}
	/*
	 *Tests setAddress() method of BaseUser.java
	 */
	@Test
	public void testSetNewAddress() {
		baseUser.setAddress(newAddress);
		assertEquals("Gets new Address:",newAddress,baseUser.getAddress());
	}
	/*
	 *Tests getCountry() method of BaseUser.java
	 */
	@Test
	public void testGetCountry() {
		assertEquals("Gets Country:",country,baseUser.getCountry());
	}
	/*
	 *Tests setCountry() method of BaseUser.java
	 */
	@Test
	public void testSetNewCountry() {
		baseUser.setCountry(newCountry);
		assertEquals("Gets new Country:",newCountry,baseUser.getCountry());
	}
	/*
	 *Tests getContactNumber() method of BaseUser.java
	 */
	@Test
	public void testGetContactNumber() {
		assertEquals("Gets ContactNumber:",contactNumber,baseUser.getContactNumber());
	}
	/*
	 *Tests setContactNumber() method of BaseUser.java
	 */
	@Test
	public void testSetNewContactNumber() {
		baseUser.setContactNumber(newContactNumber);
		assertEquals("Gets new ContactNumber:",newContactNumber,baseUser.getContactNumber());
	}
	/*
	 *Tests getBirthDate() method of BaseUser.java
	 */
	@Test
	public void testGetBirthDate() {
		assertEquals("Gets BirthDate:",birthDate,baseUser.getBirthDate());
	}
	/*
	 *Tests setBirthDate() method of BaseUser.java
	 */
	@Test
	public void testSetNewBirthDate() {
		baseUser.setBirthDate(newBirthDate);
		assertEquals("Gets new BirthDate:",newBirthDate,baseUser.getBirthDate());
	}
	/*
	 *Tests getGender() method of BaseUser.java
	 */
	@Test
	public void testGetGender() {
		assertEquals("Gets Gender:",gender,baseUser.getGender());
	}
	/*
	 *Tests setGender() method of BaseUser.java
	 */
	@Test
	public void testSetNewGender() {
		baseUser.setGender(newGender);
		assertEquals("Gets new Gender:",newGender,baseUser.getGender());
	}
	/*
	 *Tests getActivationVerificationKey() method of BaseUser.java
	 */
	@Test
	public void testGetActivationVerificationKey() {
		assertEquals("Gets ActivationVerificationKey:",activationVerificationKey,baseUser.getActivationVerificationKey());
	}
	/*
	 *Tests setActivationVerificationKey() method of BaseUser.java
	 */
	@Test
	public void testSetNewActivationVerificationKey() {
		baseUser.setActivationVerificationKey(newActivationVerificationKey);
		assertEquals("Gets new ActivationVerificationKey:",newActivationVerificationKey,baseUser.getActivationVerificationKey());
	}
	/*
	 *Tests getResetPasswordKey() method of BaseUser.java
	 */
	@Test
	public void testGetResetPasswordKey() {
		assertEquals("Gets ResetPasswordKey:",resetPasswordKey,baseUser.getResetPasswordKey());
	}
	/*
	 *Tests setResetPasswordKey() method of BaseUser.java
	 */
	@Test
	public void testSetNewResetPasswordKey() {
		baseUser.setResetPasswordKey(newResetPasswordKey);
		assertEquals("Gets new ResetPasswordKey:",newResetPasswordKey,baseUser.getResetPasswordKey());
	}
	/*
	 *Tests getPasswordRule() method of BaseUser.java
	 */
	@Test
	public void testGetPasswordRule() {
		assertEquals("Gets PasswordRule:",passwordRule,baseUser.getPasswordRule());
	}
	/*
	 *Tests setPasswordRule() method of BaseUser.java
	 */
	@Test
	public void testSetNewPasswordRule() {
		baseUser.setPasswordRule(newPasswordRule);
		assertEquals("Gets new PasswordRule:",newPasswordRule,baseUser.getPasswordRule());
	}
	/*
	 *Tests getTacAcceptedTs() method of BaseUser.java
	 */
	@Test
	public void testGetTacAcceptedTs() {
		assertEquals("Gets TacAcceptedTs:",tacAcceptedTs,baseUser.getTacAcceptedTs());
	}
	/*
	 *Tests setTacAcceptedTs() method of BaseUser.java
	 */
	@Test
	public void testSetNewTacAcceptedTs() {
		baseUser.setTacAcceptedTs(newTacAcceptedTs);
		assertEquals("Gets new TacAcceptedTs:",newTacAcceptedTs,baseUser.getTacAcceptedTs());
	}
	/*
	 *Tests getAdditionalFieldsValues() method of BaseUser.java
	 */
	@Test
	public void testGetAdditionalFieldsValues() {
		assertEquals("Gets AdditionalFieldsValues:",additionalFieldsValues,baseUser.getAdditionalFieldsValues());
	}
	/*
	 *Tests setAdditionalFieldsValues() method of BaseUser.java
	 */
	@Test
	public void testSetNewAdditionalFieldsValues() {
		baseUser.setAdditionalFieldsValues(newAdditionalFieldsValues);
		assertEquals("Gets new AdditionalFieldsValues:",newAdditionalFieldsValues,baseUser.getAdditionalFieldsValues());
	}
	/*
	 *Tests getUserGroupName() method of BaseUser.java
	 */
	@Test
	public void testGetUserGroupName() {
		assertEquals("Gets UserGroupName:",userGroupName,baseUser.getUserGroupName());
	}
	/*
	 *Tests setUserGroupName() method of BaseUser.java
	 */
	@Test
	public void testSetNewUserGroupName() {
		baseUser.setUserGroupName(newUserGroupName);
		assertEquals("Gets new UserGroupName:",newUserGroupName,baseUser.getUserGroupName());
	}
	/*
	 *Tests getTitle() method of BaseUser.java
	 */
	@Test
	public void testGetTitle() {
		assertEquals("Gets Title:",title,baseUser.getTitle());
	}
	/*
	 *Tests setTitle() method of BaseUser.java
	 */
	@Test
	public void testSetNewTitle() {
		baseUser.setTitle(newTitle);
		assertEquals("Gets new Title:",newTitle,baseUser.getTitle());
	}
	/*
	 *Tests getAboutMe() method of BaseUser.java
	 */
	@Test
	public void testGetAboutMe() {
		assertEquals("Gets AboutMe:",aboutMe,baseUser.getAboutMe());
	}
	/*
	 *Tests setAboutMe() method of BaseUser.java
	 */
	@Test
	public void testSetNewAboutMe() {
		baseUser.setAboutMe(newAboutMe);
		assertEquals("Gets new AboutMe:",newAboutMe,baseUser.getAboutMe());
	}
	/*
	 *Tests getJobTitle() method of BaseUser.java
	 */
	@Test
	public void testGetJobTitle() {
		assertEquals("Gets JobTitle:",jobTitle,baseUser.getJobTitle());
	}
	/*
	 *Tests setJobTitle() method of BaseUser.java
	 */
	@Test
	public void testSetNewJobTitle() {
		baseUser.setJobTitle(newJobTitle);
		assertEquals("Gets new JobTitle:",newJobTitle,baseUser.getJobTitle());
	}
	/*
	 *Tests getWorkContact() method of BaseUser.java
	 */
	@Test
	public void testGetWorkContact() {
		assertEquals("Gets WorkContact:",workContact,baseUser.getWorkContact());
	}
	/*
	 *Tests setWorkContact() method of BaseUser.java
	 */
	@Test
	public void testSetNewWorkContact() {
		baseUser.setWorkContact(newWorkContact);
		assertEquals("Gets new WorkContact:",newWorkContact,baseUser.getWorkContact());
	}
	/*
	 *Tests getMobilePhoneNumber() method of BaseUser.java
	 */
	@Test
	public void testGetMobilePhoneNumber() {
		assertEquals("Gets MobilePhoneNumber:",mobilePhoneNumber,baseUser.getMobilePhoneNumber());
	}
	/*
	 *Tests setMobilePhoneNumber() method of BaseUser.java
	 */
	@Test
	public void testSetNewMobilePhoneNumber() {
		baseUser.setMobilePhoneNumber(newMobilePhoneNumber);
		assertEquals("Gets new MobilePhoneNumber:",newMobilePhoneNumber,baseUser.getMobilePhoneNumber());
	}

	/*
	 *Tests getCustomValuesMap() method of BaseUser.java
	 */
	@Test
	public void testGetCustomValuesMap() {
		assertEquals("Gets CustomValuesMap:",customValuesMap,baseUser.getCustomValuesMap());
	}
	/*
	 *Tests setCustomValuesMap() method of BaseUser.java
	 */
	@Test
	public void testSetNewCustomValuesMap() {
		baseUser.setCustomValuesMap(newCustomValuesMap);
		assertEquals("Gets new CustomValuesMap:",newCustomValuesMap,baseUser.getCustomValuesMap());
	}
	/*
	 * Tests addCustomValue() method of BaseUser.java
	 * when key already exists
	 */
	@Test
	public void testAddCustomValueKeyExist(){
		assertEquals("Checks Custom Value",value,baseUser.getCustomValuesMap().get(key).getValue());
		baseUser.addCustomValue(key, newValue);
		assertEquals("Checks Custom Value",newValue,baseUser.getCustomValuesMap().get(key).getValue());
	}
	/*
	 * Tests addCustomValue() method of BaseUser.java
	 * when key doesn't exist yet
	 */
	@Test
	public void testAddCustomValueKeyDoesNotExist(){
		assertEquals("Checks Custom Key",false,baseUser.getCustomValuesMap().containsKey(newKey));
		baseUser.addCustomValue(newKey, newValue);
		assertEquals("Checks Custom Key",true,baseUser.getCustomValuesMap().containsKey(newKey));
	}
	/*
	 * Tests addCustomValue() method of BaseUser.java
	 * when customValueMap is null
	 */
	@Test
	public void testAddCustomValueWhenCustomValueMapIsNull(){
		baseUser.setCustomValuesMap(null);
		baseUser.addCustomValue(newKey, newValue);
		assertEquals("Checks Custom Key",true,baseUser.getCustomValuesMap().containsKey(newKey));
	}
	/*
	 * Tests addCustomValue() method of BaseUser.java
	 * when key already exists
	 */
	@Test
	public void testAddCustomValue(){
		assertEquals("Checks Custom Value",value,baseUser.getCustomValuesMap().get(key).getValue());
		baseUser.addCustomValue(key, newValue);
		assertEquals("Checks Custom Value",newValue,baseUser.getCustomValuesMap().get(key).getValue());
	}
	/*
	 * Tests removeCustomValue() method of BaseUser.java
	 */
	@Test
	public void testRemoveCustomValue(){
		baseUser.removeCustomValue(key);
		assertEquals("Checks Custom Key",false,baseUser.getCustomValuesMap().containsKey(key));
	}
	/*
	 * Tests removeCustomValue() method of BaseUser.java
	 * when customValuesMap  passed is null
	 */
	@Test
	public void testRemoveCustomValueCustomValuesMapIsNull(){
		baseUser.setCustomValuesMap(null);
		baseUser.removeCustomValue(key);
		assertEquals("Checks Custom Key",null,baseUser.getCustomValuesMap());
	}
	/*
	 * Tests getAdditionalQueryClause() method of BaseUser.java
	 */

	@Test 
	public void testGetAdditionalQueryClause(){
		assertEquals("Checks Additional Query Clause:"," gp.name = :userGroupName ",baseUser.getAdditionalQueryClause(true));
	}
	/*
	 * Tests getAdditionalQueryClause() method of BaseUser.java
	 * when userGroupName is empty
	 */
	@Test
	public void testGetAdditionalQueryClauseUserGroupNameIsEmpty(){
		baseUser.setUserGroupName("");
		assertEquals("Checks Additional Query Clause:","",baseUser.getAdditionalQueryClause(true));
	}
	/*
	 * Tests getJoinClause() method of BaseUser.java
	 */
	@Test
	public void testGetJoinClause(){
		assertEquals("Checks Join Clause:"," INNER JOIN obj.groups gp ",baseUser.getJoinClause());
	}
	/*
	 * Tests getJoinClause() method of BaseUser.java
	 * when userGroupName is empty
	 */
	@Test
	public void testGetJoinClauseUserGroupNameIsEmpty(){
		baseUser.setUserGroupName("");
		assertEquals("Checks Join Clause:","",baseUser.getJoinClause());
	}
	
	/*
	 *Tests getFacebookUserId() method of BaseUser.java
	 */
	@Test
	public void testGetFacebookUserId() {
		assertEquals("Gets FacebookUserId:",facebookUserId,baseUser.getFacebookUserId());
	}
	/*
	 *Tests setFacebookUserId() method of BaseUser.java
	 */
	@Test
	public void testSetNewFacebookUserId() {
		baseUser.setFacebookUserId(newFacebookUserId);
		assertEquals("Gets new FacebookUserId:",newFacebookUserId,baseUser.getFacebookUserId());
	}
	
	/*
	 * Class used to create an instance of Claims.java
	 */
	private class FakeClaims implements Claims{

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object get(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object put(String key, Object value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Set<String> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Object> values() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIssuer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setIssuer(String iss) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSubject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setSubject(String sub) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getAudience() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setAudience(String aud) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getExpiration() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setExpiration(Date exp) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getNotBefore() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setNotBefore(Date nbf) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getIssuedAt() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setIssuedAt(Date iat) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Claims setId(String jti) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T get(String claimName, Class<T> requiredType) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}


}
