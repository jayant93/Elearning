package com.ideyatech.opentides.um.response;

import java.util.List;

import com.gamify.elearning.entity.Curriculum;
import com.gamify.elearning.entity.Unit;

public class CourseCreate {

	private String id;
	private String title;

	private String description;

	private String tags;

	private String CourseBio;

	private String fileName;

	private String courseCategaries;

	private String className;
	private String unitName;
	private List<Unit> unit;

	private List<Curriculum> curriculum;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public List<Unit> getUnit() {
		return unit;
	}

	public void setUnit(List<Unit> unit) {
		this.unit = unit;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCourseBio() {
		return CourseBio;
	}

	public void setCourseBio(String courseBio) {
		CourseBio = courseBio;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCourseCategaries() {
		return courseCategaries;
	}

	public void setCourseCategaries(String courseCategaries) {
		this.courseCategaries = courseCategaries;
	}

	public List<Curriculum> getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(List<Curriculum> curriculum) {
		this.curriculum = curriculum;
	}

}
