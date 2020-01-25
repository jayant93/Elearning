package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ideyatech.opentides.core.entity.SystemCodes;

@Entity
@Table(name="VIDEO")
public class Video extends Element {
	
	private static final long serialVersionUID = -3408916114022871917L;
	
	@Column
	private String etag;
	
	@Column
	private String youtubeId;

	@Column
	private String vimeoId;
	
	@Column
	private String fileName;
	
	@Column
	private String defaultThumbnailUrl;

	@OneToMany(mappedBy = "video")
	private List<Thumbnail> thumbnails;
	
	@OneToOne
    @JoinColumn(name = "COURSE_ID")
	private Course course;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RESTRICTION_TYPE")
    private SystemCodes restrictionType;
	
	@Column
    private Integer selectedThumbnail;

	@Column
	private Integer durationInSec;
	
	@Transient
	String restrictionTypeKey;
	
	public Video() {}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getYoutubeId() {
		return youtubeId;
	}

	public void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDefaultThumbnailUrl() {
		return defaultThumbnailUrl;
	}

	public void setDefaultThumbnailUrl(String defaultThumbnailUrl) {
		this.defaultThumbnailUrl = defaultThumbnailUrl;
	}
	
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public SystemCodes getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(SystemCodes restrictionType) {
		this.restrictionType = restrictionType;
	}

	public String getRestrictionTypeKey() {
		return restrictionTypeKey;
	}

	public void setRestrictionTypeKey(String restrictionTypeKey) {
		this.restrictionTypeKey = restrictionTypeKey;
	}

	public Integer getSelectedThumbnail() {
		return selectedThumbnail;
	}

	public void setSelectedThumbnail(Integer selectedThumbnail) {
		this.selectedThumbnail = selectedThumbnail;
	}

	public Integer getDurationInSec() {
		return durationInSec;
	}

	public void setDurationInSec(Integer durationInSec) {
		this.durationInSec = durationInSec;
	}

	public String getElementType() {
		return this.getClass().getSimpleName();
	}

	public String getVimeoId() {
		return vimeoId;
	}

	public void setVimeoId(String vimeoId) {
		this.vimeoId = vimeoId;
	}

	public List<Thumbnail> getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(List<Thumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}
}
