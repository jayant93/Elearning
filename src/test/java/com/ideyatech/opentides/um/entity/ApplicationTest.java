package com.ideyatech.opentides.um.entity;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ApplicationTest {
	private Application application;
	

	private final String firstName = "FIRST_NAME";
	private final String id = "1";

	private final String link = "LINK";
	private final Boolean useLdap = true;
	private final String ldapServer = "LDAP_SERVER";
	private final String name = "NAME";
	private final String description = "DESCRIPTION";
	private final int failedLoginAttempts = 100;
	private final Boolean allowFbLogin = true;
	private final String fbAppId = "FB_APP_ID";
	private final String fbAppSecret = "FB_APP_SECRET";
	private final String emailAddress = "EMAIL_ADDRESS";
	private final Boolean sendActivationEmail = true;
	private final BaseUser adminUser = new BaseUser();
	private final String appSecret = "APP_SECRET";
	private final PasswordRules passwordRules = new PasswordRules();
	private final UserGroup adminUserGroup = new UserGroup();
	private final String adminPassword = "ADMIN_PASSWORD";
	private final long lockoutTime = 200;
	
	
	private final String newLink = "NEW_LINK";
	private final Boolean newUseLdap = false;
	private final String newLdapServer = "NEW_LDAP_SERVER";
	private final String newName = "NEW_NAME";
	private final String newDescription = "NEW_DESCRIPTION";
	private final int newFailedLoginAttempts = 1000;
	private final Boolean newAllowFbLogin = false;
	private final String newFbAppId = "NEW_FB_APP_ID";
	private final String newFbAppSecret = "NEW_FB_APP_SECRET";
	private final String newEmailAddress = "NEW_EMAIL_ADDRESS";
	private final Boolean newSendActivationEmail = false;
	private final BaseUser newAdminUser = new BaseUser();
	private final String newAppSecret = "NEW_APP_SECRET";
	private final PasswordRules newPasswordRules = new PasswordRules();
	private final UserGroup newAdminUserGroup = new UserGroup();
	private final String newAdminPassword = "NEW_ADMIN_PASSWORD";
	private final long newLockoutTime = 2000;
	/*
	 * Initialization
	 */
	@Before
	public void init() {
		adminUser.setFirstName(firstName);
		passwordRules.setId(id);
		adminUserGroup.setId(id);
		
		application = new Application();
		application.setLink(link);
		application.setUseLdap(useLdap);
		application.setLdapServer(ldapServer);
		application.setName(name);
		application.setDescription(description);
		application.setFailedLoginAttempts(failedLoginAttempts);
		application.setAllowFbLogin(allowFbLogin);
		application.setFbAppId(fbAppId);
		application.setFbAppSecret(fbAppSecret);
		application.setEmailAddress(emailAddress);
		application.setSendActivationEmail(sendActivationEmail);
		application.setAdminUser(adminUser);
		application.setAppSecret(appSecret);
		application.setPasswordRules(passwordRules);
		application.setAdminUserGroup(adminUserGroup);
		application.setAdminPassword(adminPassword);
		application.setLockoutTime(lockoutTime);
	}

	/*
	 *Tests getLink() method of Application.java
	 */
	@Test
	public void testGetLink() {
		assertEquals("Gets Link:",link,application.getLink());
	}
	/*
	 *Tests setLink() method of Application.java
	 */
	@Test
	public void testSetNewLink() {
		application.setLink(newLink);
		assertEquals("Gets new Link:",newLink,application.getLink());
	}

	/*
	 *Tests getUseLdap() method of Application.java
	 */
	@Test
	public void testGetUseLdap() {
		assertEquals("Gets UseLdap:",useLdap,application.getUseLdap());
	}
	/*
	 *Tests setUseLdap() method of Application.java
	 */
	@Test
	public void testSetNewUseLdap() {
		application.setUseLdap(newUseLdap);
		assertEquals("Gets new UseLdap:",newUseLdap,application.getUseLdap());
	}

	/*
	 *Tests getLdapServer() method of Application.java
	 */
	@Test
	public void testGetLdapServer() {
		assertEquals("Gets LdapServer:",ldapServer,application.getLdapServer());
	}
	/*
	 *Tests setLdapServer() method of Application.java
	 */
	@Test
	public void testSetNewLdapServer() {
		application.setLdapServer(newLdapServer);
		assertEquals("Gets new LdapServer:",newLdapServer,application.getLdapServer());
	}

	/*
	 *Tests getName() method of Application.java
	 */
	@Test
	public void testGetName() {
		assertEquals("Gets Name:",name,application.getName());
	}
	/*
	 *Tests setName() method of Application.java
	 */
	@Test
	public void testSetNewName() {
		application.setName(newName);
		assertEquals("Gets new Name:",newName,application.getName());
	}

	/*
	 *Tests getDescription() method of Application.java
	 */
	@Test
	public void testGetDescription() {
		assertEquals("Gets Description:",description,application.getDescription());
	}
	/*
	 *Tests setDescription() method of Application.java
	 */
	@Test
	public void testSetNewDescription() {
		application.setDescription(newDescription);
		assertEquals("Gets new Description:",newDescription,application.getDescription());
	}

	/*
	 *Tests getFailedLoginAttempts() method of Application.java
	 */
	@Test
	public void testGetFailedLoginAttempts() {
		assertEquals("Gets FailedLoginAttempts:",(Integer)failedLoginAttempts,application.getFailedLoginAttempts());
	}
	/*
	 *Tests setFailedLoginAttempts() method of Application.java
	 */
	@Test
	public void testSetNewFailedLoginAttempts() {
		application.setFailedLoginAttempts(newFailedLoginAttempts);
		assertEquals("Gets new FailedLoginAttempts:",(Integer)newFailedLoginAttempts,application.getFailedLoginAttempts());
	}

	/*
	 *Tests getAllowFbLogin() method of Application.java
	 */
	@Test
	public void testGetAllowFbLogin() {
		assertEquals("Gets AllowFbLogin:",allowFbLogin,application.getAllowFbLogin());
	}
	/*
	 *Tests setAllowFbLogin() method of Application.java
	 */
	@Test
	public void testSetNewAllowFbLogin() {
		application.setAllowFbLogin(newAllowFbLogin);
		assertEquals("Gets new AllowFbLogin:",newAllowFbLogin,application.getAllowFbLogin());
	}

	/*
	 *Tests getFbAppId() method of Application.java
	 */
	@Test
	public void testGetFbAppId() {
		assertEquals("Gets FbAppId:",fbAppId,application.getFbAppId());
	}
	/*
	 *Tests setFbAppId() method of Application.java
	 */
	@Test
	public void testSetNewFbAppId() {
		application.setFbAppId(newFbAppId);
		assertEquals("Gets new FbAppId:",newFbAppId,application.getFbAppId());
	}

	/*
	 *Tests getFbAppSecret() method of Application.java
	 */
	@Test
	public void testGetFbAppSecret() {
		assertEquals("Gets FbAppSecret:",fbAppSecret,application.getFbAppSecret());
	}
	/*
	 *Tests setFbAppSecret() method of Application.java
	 */
	@Test
	public void testSetNewFbAppSecret() {
		application.setFbAppSecret(newFbAppSecret);
		assertEquals("Gets new FbAppSecret:",newFbAppSecret,application.getFbAppSecret());
	}

	/*
	 *Tests getEmailAddress() method of Application.java
	 */
	@Test
	public void testGetEmailAddress() {
		assertEquals("Gets EmailAddress:",emailAddress,application.getEmailAddress());
	}
	/*
	 *Tests setEmailAddress() method of Application.java
	 */
	@Test
	public void testSetNewEmailAddress() {
		application.setEmailAddress(newEmailAddress);
		assertEquals("Gets new EmailAddress:",newEmailAddress,application.getEmailAddress());
	}

	/*
	 *Tests getSendActivationEmail() method of Application.java
	 */
	@Test
	public void testGetSendActivationEmail() {
		assertEquals("Gets SendActivationEmail:",sendActivationEmail,application.getSendActivationEmail());
	}
	/*
	 *Tests setSendActivationEmail() method of Application.java
	 */
	@Test
	public void testSetNewSendActivationEmail() {
		application.setSendActivationEmail(newSendActivationEmail);
		assertEquals("Gets new SendActivationEmail:",newSendActivationEmail,application.getSendActivationEmail());
	}

	/*
	 *Tests getAdminUser() method of Application.java
	 */
	@Test
	public void testGetAdminUser() {
		assertEquals("Gets AdminUser:",adminUser,application.getAdminUser());
	}
	/*
	 *Tests setAdminUser() method of Application.java
	 */
	@Test
	public void testSetNewAdminUser() {
		application.setAdminUser(newAdminUser);
		assertEquals("Gets new AdminUser:",newAdminUser,application.getAdminUser());
	}

	/*
	 *Tests getAppSecret() method of Application.java
	 */
	@Test
	public void testGetAppSecret() {
		assertEquals("Gets AppSecret:",appSecret,application.getAppSecret());
	}
	/*
	 *Tests setAppSecret() method of Application.java
	 */
	@Test
	public void testSetNewAppSecret() {
		application.setAppSecret(newAppSecret);
		assertEquals("Gets new AppSecret:",newAppSecret,application.getAppSecret());
	}

	/*
	 *Tests getPasswordRules() method of Application.java
	 */
	@Test
	public void testGetPasswordRules() {
		assertEquals("Gets PasswordRules:",passwordRules,application.getPasswordRules());
	}
	/*
	 *Tests setPasswordRules() method of Application.java
	 */
	@Test
	public void testSetNewPasswordRules() {
		application.setPasswordRules(newPasswordRules);
		assertEquals("Gets new PasswordRules:",newPasswordRules,application.getPasswordRules());
	}

	/*
	 *Tests getAdminUserGroup() method of Application.java
	 */
	@Test
	public void testGetAdminUserGroup() {
		assertEquals("Gets AdminUserGroup:",adminUserGroup,application.getAdminUserGroup());
	}
	/*
	 *Tests setAdminUserGroup() method of Application.java
	 */
	@Test
	public void testSetNewAdminUserGroup() {
		application.setAdminUserGroup(newAdminUserGroup);
		assertEquals("Gets new AdminUserGroup:",newAdminUserGroup,application.getAdminUserGroup());
	}

	/*
	 *Tests getAdminPassword() method of Application.java
	 */
	@Test
	public void testGetAdminPassword() {
		assertEquals("Gets AdminPassword:",adminPassword,application.getAdminPassword());
	}
	/*
	 *Tests setAdminPassword() method of Application.java
	 */
	@Test
	public void testSetNewAdminPassword() {
		application.setAdminPassword(newAdminPassword);
		assertEquals("Gets new AdminPassword:",newAdminPassword,application.getAdminPassword());
	}

	/*
	 *Tests getLockoutTime() method of Application.java
	 */
	@Test
	public void testGetLockoutTime() {
		assertEquals("Gets LockoutTime:",(Long)lockoutTime,application.getLockoutTime());
	}
	/*
	 *Tests setLockoutTime() method of Application.java
	 */
	@Test
	public void testSetNewLockoutTime() {
		application.setLockoutTime(newLockoutTime);
		assertEquals("Gets new LockoutTime:",(Long)newLockoutTime,application.getLockoutTime());
	}
	/*
	 * Tests isEnableUserLockCheck() method of Application.java
	 * when FailedLoginAttempts is not null and greater than zero
	 */
	@Test
	public void testIsEnableUserLockCheckNotNullGreaterThanZero(){
		assertEquals("Gets EnableUserLockCheck:",true,application.isEnableUserLockCheck());
	}
	/*
	 * Tests isEnableUserLockCheck() method of Application.java
	 * when FailedLoginAttempts is null
	 */
	@Test
	public void testIsEnableUserLockCheck(){
		application.setFailedLoginAttempts(null);
		assertEquals("Gets EnableUserLockCheck:",false,application.isEnableUserLockCheck());
	}
	/*
	 * Tests isEnableUserLockCheck() method of Application.java
	 * when FailedLoginAttempts is not null but less than or equal to zero
	 */
	@Test
	public void testIsEnableUserLockCheckNotNullButLessThanOrEqualToZero(){
		application.setFailedLoginAttempts(0);
		assertEquals("Gets EnableUserLockCheck:",false,application.isEnableUserLockCheck());
		application.setFailedLoginAttempts(-100);
		assertEquals("Gets EnableUserLockCheck:",false,application.isEnableUserLockCheck());
	}
	/*
	 * Tests prePersist() method of Application.java
	 * when allowFbLogin, useLdap, failedLoginAttempts, sendActivationEmail and lockoutTime are not null
	 */
	@Test 
	public void testPrePersistNotNull(){
		application.prePersist();
		assertEquals("Gets AllowFbLogin:",allowFbLogin,application.getAllowFbLogin());
		assertEquals("Gets UseLdap:",useLdap,application.getUseLdap());
		assertEquals("Gets FailedLoginAttempts:",(Integer)failedLoginAttempts,application.getFailedLoginAttempts());
		assertEquals("Gets SendActivationEmail:",sendActivationEmail,application.getSendActivationEmail());
		assertEquals("Gets LockoutTime:",(Long)lockoutTime,application.getLockoutTime());
	}
	/*
	 * Tests prePersist() method of Application.java
	 * when allowFbLogin, useLdap, failedLoginAttempts, sendActivationEmail and lockoutTime are null
	 */
	@Test 
	public void testPrePersistNull(){
		application.setAllowFbLogin(null);
		application.setUseLdap(null);
		application.setFailedLoginAttempts(null);
		application.setSendActivationEmail(null);
		application.setLockoutTime(null);
		
		application.prePersist();
		assertEquals("Gets AllowFbLogin:",false,application.getAllowFbLogin());
		assertEquals("Gets UseLdap:",false,application.getUseLdap());
		assertEquals("Gets FailedLoginAttempts:",(Integer)(-1),application.getFailedLoginAttempts());
		assertEquals("Gets SendActivationEmail:",false,application.getSendActivationEmail());
		assertEquals("Gets LockoutTime:",(Long)(15l * 60l),application.getLockoutTime());
	}
}

