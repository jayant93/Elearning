package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ideyatech.opentides.core.entity.BaseEntity;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "COURSE_PROGRESS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CourseProgress extends BaseEntity {

	private static final long serialVersionUID = -533951634734407710L;

	@ManyToOne
	@JoinColumn(name = "COURSE_ID")
	private Course course;
	
	@OneToMany(cascade= CascadeType.PERSIST, mappedBy = "courseProgress")
	@Where(clause = "DELETED = 0")
	private List<ElementProgress> elementLogs;
	
	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private ELearningUser user;

	@Column(name = "IS_FINISHED", columnDefinition = "bit(1) DEFAULT false")
	private boolean finished;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<ElementProgress> getElementLogs() {
		return elementLogs;
	}

	public void setElementLogs(List<ElementProgress> elementLogs) {
		this.elementLogs = elementLogs;
	}

	public ELearningUser getUser() {
		return user;
	}

	public void setUser(ELearningUser user) {
		this.user = user;
	}
	
	public boolean getFinished() {
		return finished;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
}
