/*
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.    
 */

package com.ideyatech.opentides.um.entity;

import com.ideyatech.opentides.core.entity.user.JwtClaim;
import com.ideyatech.opentides.core.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * This class is used by ACEGI to represent the currently logged user for the
 * session. To retrieve the SessionUser object use
 * SecurityUtil.getSessionUser();
 * 
 * @author allantan
 *
 */
public class SessionUser extends User implements JwtClaim {

	private static final long serialVersionUID = 8493532913557193485L;

	private final Map<String, Object> profile = new HashMap<String, Object>();

	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

	private String id;

	private String nosqlId;

	private String token;

	private String appSecret;

	public SessionUser(final UserDetails user) {
		super(user.getUsername(), user.getPassword(), user.isEnabled(), user
				.isAccountNonExpired(), user.isCredentialsNonExpired(), user
				.isAccountNonLocked(), user.getAuthorities());
	}

	public SessionUser(final BaseUser user,
					   final List<GrantedAuthority> authorities) {
		super(user.getCredential().getUsername(), user.getCredential()
				.getPassword(), user.getCredential().getEnabled(), true, true,
				true, authorities);
		this.addProfile("lastName", user.getLastName());
		this.addProfile("firstName", user.getFirstName());
		this.addAdditionalProperties("groups", user.getUserGroupIds());
		this.addAdditionalProperties("divisions", user.getDivisionKeys());
		this.addAdditionalProperties("homeDepartment", user.getHomeDepartmentKey());
		this.addAdditionalProperties("homeSection", user.getHomeSectionKey());
		id = user.getId();
		nosqlId = user.getNosqlId();
	}

	public SessionUser(final BaseUser user) {
		super(user.getCredential().getUsername(), user.getCredential()
						.getPassword(), user.getCredential().getEnabled(), true, true,
				true, user.getGrantedAuthorities());
		this.addProfile("lastName", user.getLastName());
		this.addProfile("firstName", user.getFirstName());
		this.addAdditionalProperties("groups", user.getUserGroupIds());
		this.addAdditionalProperties("divisions", user.getDivisionKeys());
		this.addAdditionalProperties("homeDepartment", user.getHomeDepartmentKey());
		this.addAdditionalProperties("homeSection", user.getHomeSectionKey());
		id = user.getId();
		nosqlId = user.getNosqlId();
	}

	/**
	 * Returns the complete name by concatenating lastName and firstName
	 * 
	 * @return
	 */
	public String getCompleteName() {
		String name = "";
		final String lastName = "" + profile.get("lastName");
		final String firstName = "" + profile.get("firstName");
		if (!StringUtil.isEmpty(lastName)) {
			name += lastName + ", ";
		}
		name += firstName;
		return name;
	}

	/**
	 * Checks if user has permission to the specified permission string
	 * 
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(final String permission) {
		for (final GrantedAuthority auth : this.getAuthorities()) {
			if (permission.equals(auth.getAuthority())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the profile
	 */
	public final Map<String, Object> getProfile() {
		return profile;
	}

	/**
	 * Adds profile settings to the session user.
	 */
	public void addProfile(final String key, final Object value) {
		if ("ID".equals(key)) {
			id = new String("" + value);
		} else {
			profile.put(key, value);
		}
	}

	/**
	 * Adds additional profile settings to the session user.
	 */
	public void addAdditionalProperties(final String key, final Object value) {
		additionalProperties.put(key, value);
	}

	/**
	 * 
	 * @return
	 */
	public String getSchemaName() {
		if (profile.get("SCHEMA_NAME") != null) {
			return profile.get("SCHEMA_NAME").toString();
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getTenantName() {
		if (profile.get("TENANT_NAME") != null) {
			return profile.get("TENANT_NAME").toString();
		}

		return null;
	}

	@Override
	public String getSubject() {
		return getUsername();
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	public String getNosqlId() {
		return nosqlId;
	}

	@Override
	public Set<String> getAuths() {
		Set<String> auths = new HashSet<>();
		Collection<GrantedAuthority> authorities = getAuthorities();
		for(GrantedAuthority authority : authorities) {
			auths.add(authority.getAuthority());
		}
		return auths;
	}

	@Override
	public Map<String, Object> getAdditionalProperties() {
		Map<String, Object> props =
				additionalProperties != null ? additionalProperties : new HashMap<>();
		props.put("nosqlId", nosqlId);
		return props;
	}

	@Override
	public void processBody(Claims body) {

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
}
