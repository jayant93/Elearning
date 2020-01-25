package com.ideyatech.opentides.um.response;



public class CourseCreationResponse {
	private String courseid;
	private String unitname;
	private String description;
	private String title;
	private String tags;
	private String courseBio;
	private String className;
	private String unit;
    
	public String getCourseid() {
		return courseid;
	}

	public void setCourseid(String courseid) {
		this.courseid = courseid;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCourseBio() {
		return courseBio;
	}

	public void setCourseBio(String courseBio) {
		this.courseBio = courseBio;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	

	public String getUnit() {
		return unit;
	}

	public void setUnit(String string) {
		this.unit = string;
	}
}
