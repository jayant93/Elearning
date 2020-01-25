package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name="COMPANY")
public class Company extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7456155440711124632L;

	@Column
	private String name;
	
	@Column
	private String websiteUrl;
	
	@Column
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "elearningCompany")
	@JsonManagedReference
	private List<ELearningUser> users;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public List<ELearningUser> getUsers() {
		return users;
	}

	public void setUsers(List<ELearningUser> users) {
		this.users = users;
	}
		
}
