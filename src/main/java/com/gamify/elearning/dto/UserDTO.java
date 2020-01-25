package com.gamify.elearning.dto;

public class UserDTO {
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String company;
	private String newPassword;
	private String confirmPassword;
	private String websiteUrl;
	private String authToken;
	private String facebookUserId;
	private String profilePhotoUrl;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getFacebookUserId() {
		return facebookUserId;
	}
	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}
	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}
	public void setProfilePhotoUrl(String profilePhotoUrl) {
		this.profilePhotoUrl = profilePhotoUrl;
	}

	@Override
	public String toString() {
		return new String("firstName: "+firstName+";"
				+"lastName: "+lastName+ ";"
				+"emailAddress: "+emailAddress+ ";"
				+"company: "+company+ ";"
				+"newPassword: "+newPassword+ ";"
				+"confirmPassword: "+confirmPassword+ ";"
				+"websiteUrl: "+websiteUrl);
	}
}
