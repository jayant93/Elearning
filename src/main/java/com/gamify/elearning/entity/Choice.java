package com.gamify.elearning.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name="CHOICE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Choice extends BaseEntity {

    private static final long serialVersionUID = 1117286551641633809L;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    // @JsonBackReference
    private Question question;

    @Column(name = "VALUE")
    private String value;
    
    @Column(name = "IS_CORRECT_ANSWER")
    private Boolean isCorrectAnswer;

    @Column(name = "ORDINAL")
    private int ordinal;
    
    @Column(name = "DELETED", columnDefinition = "bit(1) DEFAULT false")
    private boolean deleted;

//    @JsonIgnore
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getCorrectAnswer() {
        return isCorrectAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}