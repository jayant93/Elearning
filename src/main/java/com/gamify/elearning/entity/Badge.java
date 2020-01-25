package com.gamify.elearning.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ideyatech.opentides.core.entity.BaseEntity;

/**
 * @author johanna@ideyatech.coms
 */
@Entity
@Table(name = "BADGE")
public class Badge extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1102721767048548652L;

	@Column
	private String title;

	@Column
	private String imageUrl;

	@ManyToMany(mappedBy = "badges")
	@JsonBackReference
	private Set<Course> courses;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}
	
}
