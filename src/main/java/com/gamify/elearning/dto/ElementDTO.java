package com.gamify.elearning.dto;

public class ElementDTO {
	
	String id;
	String title;
	String lessonId;
	int ordinal;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLessonId() {
		return lessonId;
	}
	public void setLessonId(String lessonId) {
		this.lessonId = lessonId;
	}
	public int getOrdinal() {
		return ordinal;
	}
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
	
	@Override
	public String toString() {
		return new String("id: "+id+";"+"title: "+title
				+"lessonId: "+lessonId+ ";"
				+"ordinal: "+ordinal);
	}

}
