package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name="Curriculum")
public class Curriculum extends BaseEntity {
	

    @NotNull
	@Column(name="class_name") 
	private String className;  
	
	
	@JsonManagedReference
	@OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<Unit> unit;
	
	@JsonBackReference
	@ManyToOne
	private Course course;

	
	
	


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	

	public List<Unit> getUnit() {
		return unit;
	}

	public void setUnit(List<Unit> unit) {
		this.unit = unit;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
	
}
