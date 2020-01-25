package com.gamify.elearning.dto;

import java.util.List;

public class UserGroupAuthsDTO {
	String id;
	List<String> auths;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getAuths() {
		return auths;
	}
	public void setAuths(List<String> auths) {
		this.auths = auths;
	}
	
	
}
