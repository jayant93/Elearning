package com.ideyatech.opentides.um.entity;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

public class UserGroupTest {
	private UserGroup userGroup;	
	private final String permission = "PERMISSION";
	private final String differentPermission="DIFFERENT_PERMISSION";
	private final UserAuthority userAuthority= new UserAuthority();
	private final UserAuthority differentPermissionAuthority= new UserAuthority();
	private final List<GrantedAuthority> grantedAuthority = new ArrayList<GrantedAuthority>();
	private final List<String> authorityNames = new ArrayList<String>();

	private final BaseUser baseUser = new BaseUser();
	
	
	private final String name = "NAME";
	private final String description = "DESCRIPTION";
	private final Set<UserAuthority>authoritiesSet = new HashSet<UserAuthority>();
	private final Set<BaseUser> users = new HashSet<BaseUser>();
	private final Boolean isDefault = true;
	
	private final String newName = "NEW_NAME";
	private final String newDescription = "NEW_DESCRIPTION";
	private final Set<UserAuthority>newAuthoritiesSet = new HashSet<UserAuthority>();
	private final Set<BaseUser> newUsers = new HashSet<BaseUser>();
	private final Boolean newIsDefault = false;
	
	/*
	 * Initialization
	 */
	@Before
	public void init(){
		userGroup = new UserGroup();
		
		differentPermissionAuthority.setAuthority(differentPermission);
		userAuthority.setAuthority(permission);
		
		authoritiesSet.add(differentPermissionAuthority);
		authoritiesSet.add(userAuthority);
		
		userGroup.setAuthorities(authoritiesSet);
		userGroup.setName(name);
		userGroup.setDescription(description);
		userGroup.setUsers(users);
		userGroup.setIsDefault(isDefault);
		

		
		ReflectionTestUtils.setField(userGroup, "newName", newName);
	}
	/*
	 * Tests getAuthorityNames() method of UserGroup
	 * when AuthorityNames is Null
	 */
	@Test
	public void testGetAuthorityNamesNull(){
		ReflectionTestUtils.setField(userGroup, "authorityNames", null);
		for(UserAuthority user:authoritiesSet){
			authorityNames.add(user.getAuthority());
		}
		assertEquals("Checks Authority Names:",authorityNames,userGroup.getAuthorityNames());
	}
	/*
	 * Tests setAuthorityNames() method of UserGroup
	 * when authorityNames is null
	 */
	@Test
	public void testSetAuthorityNamesAuthorityNamesIsNull(){
		List<UserAuthority> removeList = new ArrayList<UserAuthority>();
		for(UserAuthority user:authoritiesSet){
			removeList.add(user);
		}
		userGroup.setAuthorityNames(null);
		assertEquals("Gets RemoveList:",removeList,userGroup.getRemoveList());
		assertEquals("Gets Authorities Size:",0,userGroup.getAuthorities().size());
	}
	/*
	 * Tests setAuthorityNames() method of UserGroup
	 * when authorityNames is not null
	 */
	@Test
	public void testSetAuthorityNamesAuthorityNamesIsNotNull(){
		List<UserAuthority> removeList = new ArrayList<UserAuthority>();
		removeList.add(differentPermissionAuthority);
		List<String> authorityNames= new ArrayList<String>();
		authorityNames.add(permission);
		authorityNames.add(name);
		userGroup.setAuthorityNames(authorityNames);
		assertEquals("Gets RemoveList:",removeList,userGroup.getRemoveList());
		assertEquals("Gets AddedList:",1,userGroup.getAddedList().size());
		assertEquals("Gets Authorities Size:",2,userGroup.getAuthorities().size());
	}
	/*
	 *Tests getAuthorities() method of UserGroup.java
	 */
	@Test
	public void testGetAuthorities() {
		assertEquals("Gets Authorities:",authoritiesSet,userGroup.getAuthorities());
	}
	/*
	 *Tests setAuthorities() method of UserGroup.java
	 */
	@Test
	public void testSetNewAuthorities() {
		userGroup.setAuthorities(newAuthoritiesSet);
		assertEquals("Gets new Authorities:",newAuthoritiesSet,userGroup.getAuthorities());
	}
	/*
	 * Tests addAuthority() method of UserGroup.java
	 * when authority and authorities are not null
	 */
	@Test
	public void testAddAuthorityAuthorityAndAuthoritiesNotNull(){
		assertEquals("Checks Status:",true,userGroup.addAuthority(differentPermissionAuthority));
		assertEquals("Gets Authorities:",authoritiesSet,userGroup.getAuthorities());
		
	}
	/*
	 * Tests addAuthority() method of UserGroup.java
	 * when authority is null and authorities is not null
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testAddAuthorityAuthorityIsNullAndAuthoritiesNotNull(){
		userGroup.addAuthority(null);
		assertEquals("Gets Authorities:",authoritiesSet,userGroup.getAuthorities());
		
	}
	/*
	 * Tests addAuthority() method of UserGroup.java
	 * when authority is not null and authorities is null
	 */
	@Test (expected = NullPointerException.class)
	public void testAddAuthorityAuthorityIsNotNullAndAuthoritiesIsNull(){
		userGroup.setAuthorities(null);
		userGroup.addAuthority(differentPermissionAuthority);
		assertEquals("Gets Authorities:",authoritiesSet,userGroup.getAuthorities());
		
	}
	/*
	 * Tests removeAuthority() method of UserGroup.java
	 * when authority is not null
	 */
	@Test
	public void testRemoveAuthority(){
		assertEquals("Checks Status:",true,userGroup.removeAuthority(differentPermissionAuthority));
		authoritiesSet.remove(differentPermissionAuthority);
		assertEquals("Gets Authorities:",authoritiesSet,userGroup.getAuthorities());
		
	}
	/*
	 * Tests removeAuthority() method of UserGroup.java
	 * when authority is null
	 */
	@Test
	public void testRemoveAuthorityNull(){
		assertEquals("Checks Status:",false,userGroup.removeAuthority(null));
		
	}
	/*
	 * Tests removeAuthority() method of UserGroup.java
	 * when authority is not in the Set
	 */
	@Test
	public void testRemoveAuthorityNotInSet(){
		assertEquals("Checks Status:",false,userGroup.removeAuthority(new UserAuthority() ));
		
	}
	/*
	 *Tests getName() method of UserGroup.java
	 */
	@Test
	public void testGetName() {
		assertEquals("Gets Name:",name,userGroup.getName());
	}
	/*
	 *Tests setName() method of UserGroup.java
	 */
	@Test
	public void testSetNewName() {
		userGroup.setName(newName);
		assertEquals("Gets new Name:",newName,userGroup.getName());
	}
	/*
	 *Tests getDescription() method of UserGroup.java
	 */
	@Test
	public void testGetDescription() {
		assertEquals("Gets Description:",description,userGroup.getDescription());
	}
	/*
	 *Tests setDescription() method of UserGroup.java
	 */
	@Test
	public void testSetNewDescription() {
		userGroup.setDescription(newDescription);
		assertEquals("Gets new Description:",newDescription,userGroup.getDescription());
	}

	/*
	 *Tests getUsers() method of UserGroup.java
	 */
	@Test
	public void testGetUsers() {
		assertEquals("Gets Users:",users,userGroup.getUsers());
	}
	/*
	 *Tests setUsers() method of UserGroup.java
	 */
	@Test
	public void testSetNewUsers() {
		userGroup.setUsers(newUsers);
		assertEquals("Gets new Users:",newUsers,userGroup.getUsers());
	}

	/*
	 *Tests getIsDefault() method of UserGroup.java
	 */
	@Test
	public void testGetIsDefault() {
		assertEquals("Gets IsDefault:",isDefault,userGroup.getIsDefault());
	}
	/*
	 *Tests setIsDefault() method of UserGroup.java
	 */
	@Test
	public void testSetNewIsDefault() {
		userGroup.setIsDefault(newIsDefault);
		assertEquals("Gets new IsDefault:",newIsDefault,userGroup.getIsDefault());
	}
	/*
	 * Tests getNewName() method of UserGroup.java
	 */
	@Test
	public void testGetNewName(){
		assertEquals("Gets New Name:",newName,userGroup.getNewName());
		
	}
	/*
	 * Tests getGrantedAuthorities() method of UserGroup.java
	 */
	@Test
	public void getGrantedAuthorities(){
	      List<GrantedAuthority> auths = userGroup.getAuthorityNames().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		assertEquals("Gets GrantedAuthorities",auths,userGroup.getGrantedAuthorities());
	}
}
