package com.gamify.elearning.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.ideyatech.opentides.core.web.json.Views;
import com.ideyatech.opentides.um.util.SecurityUtil;

@Entity
@Table(name = "COURSE")
public class Course extends Material {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476322058708593606L;

	@Column(name = "COMPANY")
	private String company;

	@Column(name = "CourseBio")
	private String CourseBio;

	@Column
	private String courseCategaries;

	@Column(name = "NUM_OF_LESSONS", columnDefinition = "integer default 0")
	private Integer numOfLessons = 0;

	@JsonManagedReference
	@JsonView(Views.SearchView.class)
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
	@OrderBy(clause = "ORDINAL ASC")
	@Where(clause = "DELETED = 0")
	private List<Lesson> lessons;

	@JsonManagedReference
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Curriculum> curriculum;

	@OneToOne(mappedBy = "course")
	@Field
	private PreviewVideo previewVideo;

	@JsonProperty("type")
	private String type;

	@ManyToMany
	@JoinTable(name = "COURSE_BADGE", joinColumns = { @JoinColumn(name = "COURSE_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "BADGE_ID") })
	private Set<Badge> badges;

	@Transient
	@JsonProperty("isOwner")
	private boolean isOwner;

	@Transient
	private String badgeId;

	public String getCourseBio() {
		return CourseBio;
	}

	public void setCourseBio(String courseBio) {
		CourseBio = courseBio;
	}

	public String getCourseCategaries() {
		return courseCategaries;
	}

	public void setCourseCategaries(String courseCategaries) {
		this.courseCategaries = courseCategaries;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public List<Curriculum> getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(List<Curriculum> curriculum) {
		this.curriculum = curriculum;
	}

	public Integer getNumOfLessons() {
		return numOfLessons;
	}

	public void setNumOfLessons(Integer numOfLessons) {
		this.numOfLessons = numOfLessons;
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

	public PreviewVideo getPreviewVideo() {
		return previewVideo;
	}

	public void setPreviewVideo(PreviewVideo previewVideo) {
		this.previewVideo = previewVideo;
	}

	public String getType() {
		return this.getClass().getSimpleName();
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<Badge> getBadges() {
		return badges;
	}

	public void setBadges(Set<Badge> badges) {
		this.badges = badges;
	}

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public boolean isOwner() {
		return isOwner;
	}

	@Override
	public String toString() {
		return "Course [company=" + company + ", CourseBio=" + CourseBio + ", courseCategaries=" + courseCategaries
				+ ", numOfLessons=" + numOfLessons + ", lessons=" + lessons + ", curriculum=" + curriculum
				+ ", previewVideo=" + previewVideo + ", type=" + type + ", badges=" + badges + ", isOwner=" + isOwner
				+ ", badgeId=" + badgeId + "]";
	}

//	public Boolean getIsOwner() {
//		return SecurityUtil.getJwtSessionUser().getId().equals(getUser().getId());
//	}

}
