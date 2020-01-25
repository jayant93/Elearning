package com.gamify.elearning.entity;

import java.util.Date;

import javax.persistence.*;

import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name = "QUIZ_RESULT")
public class QuizResult extends BaseEntity {

    private static final long serialVersionUID = -8072325283280333671L;

    @ManyToOne
    @JoinColumn(name = "QUIZ_ID")
    private Quiz quiz;
    
    @Column(name = "SCORE")
    private Integer score;

    @Column(name = "DATE_TAKEN")
    private Date dateTaken;

    @Column(name = "PASSED")
    private Boolean passed;
    
    @Column(name = "TOTAL_ITEMS")
    private Integer totalItems;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ELearningUser user;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

	public Integer getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public ELearningUser getUser() {
		return user;
	}

	public void setUser(ELearningUser user) {
		this.user = user;
	}

}