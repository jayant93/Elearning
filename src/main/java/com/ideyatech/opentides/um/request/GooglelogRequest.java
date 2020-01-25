package com.ideyatech.opentides.um.request;

public class GooglelogRequest {
	
	private String googleUserid;
	private String email; 
	private String firstname; 
	private String image; 
	private String lastname;
	private String googlidToken;
	private String provider;
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getGooglidToken() {
		return googlidToken;
	}
	public void setGooglidToken(String googlidToken) {
		this.googlidToken = googlidToken;
	}
	public GooglelogRequest() {
		super();
	}
	public GooglelogRequest(String googleUserid, String email, String firstname, String image, String lastname) {
		super();
		this.googleUserid = googleUserid;
		this.email = email;
		this.firstname = firstname;
		this.image = image;
		this.lastname = lastname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getGoogleUserid() {
		return googleUserid;
	}
	public void setGoogleUserid(String googleid) {
		this.googleUserid = googleid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String name) {
		this.firstname = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
	
	
}
