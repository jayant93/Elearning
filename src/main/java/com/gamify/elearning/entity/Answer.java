package com.gamify.elearning.entity;

import javax.persistence.*;

import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name = "ANSWER")
public class Answer extends BaseEntity {

    private static final long serialVersionUID = -3838805743612949461L;

    @Column(name = "ANSWER")
    private String answer;

    @Column(name = "IS_CORRECT")
    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private ELearningUser user;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public ELearningUser getUser() {
        return user;
    }

    public void setUser(ELearningUser user) {
        this.user = user;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}