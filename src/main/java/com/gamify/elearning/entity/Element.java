package com.gamify.elearning.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ideyatech.opentides.core.entity.SystemCodes;
import com.ideyatech.opentides.core.web.json.Views;

/**
 * @author johanna@ideyatech.coms
 */
@Entity
@Table(name = "ELEMENT")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class Element extends Material {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3258200035123363L;

	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TYPE")
    private SystemCodes type;
    
    @Column(name="TAGS")
    private String tags;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="USER_ID")
    private ELearningUser user;
    
    @Column(name="ORDINAL")
	private int ordinal;
    
    @JsonBackReference
	@JsonView(Views.SearchView.class)
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "LESSON_ID")
	private Lesson lesson;
    
    @Transient
    @JsonProperty("elementType")
    private String elementType;
	
	@Transient
	private int prevIndex;

	@Transient
	private String lessonId;
	
	@Transient
	private int numOfVideoViews = 0;
	
	public SystemCodes getType() {
		return type;
	}

	public void setType(SystemCodes type) {
		this.type = type;
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

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public Lesson getLesson() {
		return lesson;
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	public String getElementType() {
		return type.getValue();
	}

	public int getPrevIndex() {
		return prevIndex;
	}

	public void setPrevIndex(int prevIndex) {
		this.prevIndex = prevIndex;
	}

	public String getLessonId() {
		return lessonId;
	}

	public void setLessonId(String lessonId) {
		this.lessonId = lessonId;
	}

	public int getNumOfVideoViews() {
		return numOfVideoViews;
	}

	public void setNumOfVideoViews(int numOfVideoViews) {
		this.numOfVideoViews = numOfVideoViews;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
}
