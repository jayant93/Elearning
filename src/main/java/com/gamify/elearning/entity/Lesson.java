package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.ideyatech.opentides.core.web.json.Views;

@Entity
@Table(name="LESSON")
public class Lesson extends Material {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5110230320572305600L;

	@JsonBackReference
	@JsonView(Views.SearchView.class)
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "COURSE_ID")
	private Course course;
	
	@JsonManagedReference
	@JsonView(Views.SearchView.class)
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
	@OrderBy(clause = "ORDINAL ASC")
	@Where(clause = "DELETED = 0")
	private List<Element> elements;
	
	@Column(name="ORDINAL")
	private int ordinal;
	
	@Column(name = "NUM_OF_ELEMENTS", columnDefinition="integer default 0")
	private int numOfElements = 0;
	
	@Column(name = "NUM_OF_VIDEOS", columnDefinition="integer default 0")
	private int numOfVideos = 0;
	
	@Column(name = "NUM_OF_QUIZZES", columnDefinition="integer default 0")
	private int numOfQuizzes = 0;
	
	@JsonProperty("type")
	private String type;
	
	@Transient
	private String courseId;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getCourseId() {
		if(course != null) {
			return course.getId();
		}
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getType() {
		return this.getClass().getSimpleName();
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNumOfElements() {
		return numOfElements;
	}

	public void setNumOfElements(int numOfElements) {
		this.numOfElements = numOfElements < 0 ? 0 : numOfElements;
	}

	public int getNumOfVideos() {
		return numOfVideos;
	}

	public void setNumOfVideos(int numOfVideos) {
		this.numOfVideos = numOfVideos < 0 ? 0 : numOfVideos;
	}

	public int getNumOfQuizzes() {
		return numOfQuizzes;
	}

	public void setNumOfQuizzes(int numOfQuizzes) {
		this.numOfQuizzes = numOfQuizzes < 0 ? 0 : numOfQuizzes;
	}

}
