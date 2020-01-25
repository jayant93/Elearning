package com.gamify.elearning.dto;

import java.util.List;

public class LessonDTO {
	
	String id;
	List<ElementDTO> elements;
	int ordinal;
	int numOfElements;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<ElementDTO> getElements() {
		return elements;
	}
	
	public void setElements(List<ElementDTO> elements) {
		this.elements = elements;
	}
	
	public int getOrdinal() {
		return ordinal;
	}
	
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getNumOfElements() {
		return numOfElements;
	}

	public void setNumOfElements(int numOfElements) {
		this.numOfElements = numOfElements;
	}

}
