package com.gamify.elearning.entity;

import java.util.List;


import com.fasterxml.jackson.annotation.*;
import com.ideyatech.opentides.core.entity.BaseEntity;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@Entity
@Table(name="QUESTION")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Question extends BaseEntity {

    private static final long serialVersionUID = 9174942129548320334L;

    @ManyToOne
    @JoinColumn(name = "QUIZ_ID")
    // @JsonBackReference
    private Quiz quiz;

    @Column(name = "QUESTION")
    private String question;

    public enum AnswerType { SINGLE, MULTIPLE }

    @Column(name = "ANSWER_TYPE")
    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    // @JsonManagedReference
    @OrderBy(clause = "ORDINAL ASC")
    @Where(clause = "DELETED = 0")
    private List<Choice> choices;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    private List<Answer> answers;

    @Column(name = "ORDINAL")
    private int ordinal;

    @Column(name = "DELETED", columnDefinition = "bit(1) DEFAULT false")
    private boolean deleted;

    //    @JsonIgnore
    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
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