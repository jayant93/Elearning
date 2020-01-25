package com.gamify.elearning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name = "ELEMENT_PROGRESS")
public class ElementProgress extends BaseEntity{

	private static final long serialVersionUID = -533951634734407709L;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private ELearningUser user;
	
	@ManyToOne
	@JoinColumn(name = "COURSE_PROGRESS")
	private CourseProgress courseProgress;
	
	@OneToOne
	@JoinColumn(name = "ELEMENT_ID")
	private Element element;
	
	@Column(name = "IS_COMPLETED")
	private boolean completed;

	@Column(name = "DELETED", columnDefinition = "bit(1) DEFAULT false")
	private boolean deleted;

	public CourseProgress getCourseProgress() {
		return courseProgress;
	}

	public void setCourseProgress(CourseProgress courseProgress) {
		this.courseProgress = courseProgress;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public ELearningUser getUser() {
		return user;
	}

	public void setUser(ELearningUser user) {
		this.user = user;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
