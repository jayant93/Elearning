package com.gamify.elearning.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VIDEO_PROGRESS")
public class VideoProgress extends ElementProgress {
	
	private Double percentWatched;

	public Double getPercentWatched() {
		return percentWatched;
	}

	public void setPercentWatched(Double percentWatched) {
		this.percentWatched = percentWatched;
	}
	
	

}
