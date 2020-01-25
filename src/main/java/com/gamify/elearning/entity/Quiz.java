package com.gamify.elearning.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

/**
 * @author marvin
 */
@Entity
@Table(name="QUIZ")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Quiz extends Element {


    private static final long serialVersionUID = 2349007268447292102L;

    @Column(name = "PASSING_RATE")
    private Double passingRate;

    @OneToMany(mappedBy = "quiz")
    private List<QuizResult> quizResults;

    // @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz")
    @OrderBy(clause = "ORDINAL ASC")
    @Where(clause = "DELETED = 0")
    private List<Question> questions;
    
    public Quiz() {}

    public Double getPassingRate() {
        return passingRate;
    }

    public void setPassingRate(Double passingRate) {
        this.passingRate = passingRate;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<QuizResult> getQuizResults() {
        return quizResults;
    }

    public void setQuizResults(List<QuizResult> quizResults) {
        this.quizResults = quizResults;
    }
    
    public String getElementType() {
		return this.getClass().getSimpleName();
	}
    
}