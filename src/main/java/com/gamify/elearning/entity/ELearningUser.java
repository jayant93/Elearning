package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ideyatech.opentides.um.entity.BaseUser;

@Entity
@Table(name="ELEARNING_USER")
public class ELearningUser extends BaseUser{

	
	private static final long serialVersionUID = 5265225286332682678L;

	@Column(name= "WEBSITE_URL")
	private String websiteUrl;
	
	@Column(name="PROFILE_PHOTO_URL")
	private String profilePhotoUrl;

	
	
	

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<UserCourse> coursesTaken;

	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID")
	private Company elearningCompany;
	
	@Transient
	private String companyTitle;
	
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}

	public void setProfilePhotoUrl(String profilePhotoUrl) {
		this.profilePhotoUrl = profilePhotoUrl;
	}

	public List<UserCourse> getCoursesTaken() {
		return coursesTaken;
	}

	public void setCoursesTaken(List<UserCourse> coursesTaken) {
		this.coursesTaken = coursesTaken;
	}

	public Company getElearningCompany() {
		return elearningCompany;
	}

	public void setElearningCompany(Company elearningCompany) {
		this.elearningCompany = elearningCompany;
	}

	public String getCompanyTitle() {
		return companyTitle;
	}

	public void setCompanyTitle(String companyTitle) {
		this.companyTitle = companyTitle;
	}
	
}
