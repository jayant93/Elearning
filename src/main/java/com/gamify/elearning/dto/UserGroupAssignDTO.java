package com.gamify.elearning.dto;

import java.util.List;

public class UserGroupAssignDTO {

	String id;
	List<String> groups;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	
}
