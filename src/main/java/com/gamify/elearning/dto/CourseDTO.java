package com.gamify.elearning.dto;

import java.util.List;

public class CourseDTO {
	private String id;
	private List<LessonDTO> lessons;
	private int numOfLessons;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<LessonDTO> getLessons() {
		return lessons;
	}
	
	public void setLessons(List<LessonDTO> lessons) {
		this.lessons = lessons;
	}

	public int getNumOfLessons() {
		return numOfLessons;
	}

	public void setNumOfLessons(int numOfLessons) {
		this.numOfLessons = numOfLessons;
	}
	
}
