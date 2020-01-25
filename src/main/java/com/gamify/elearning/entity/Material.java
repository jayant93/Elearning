package com.gamify.elearning.entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.ideyatech.opentides.core.entity.BaseEntity;

@MappedSuperclass
public abstract class Material extends BaseEntity {

    private static final long serialVersionUID = 2263897662131779258L;

    @Column(name = "TITLE")
	private String title;
	
	@Column(name = "DESCRIPTION", columnDefinition="longtext")
	private String description;
	
	@Column(name="TAGS")
    private String tags;
	
    @ManyToOne
    @JoinColumn(name = "USERID")
	private ELearningUser user;

	@Column(name = "DELETED", columnDefinition = "bit(1) DEFAULT false")
	private boolean deleted;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public ELearningUser getUser() {
		return user;
	}

	public void setUser(ELearningUser user) {
		this.user = user;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}