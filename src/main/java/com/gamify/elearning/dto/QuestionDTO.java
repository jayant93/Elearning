package com.gamify.elearning.dto;

import com.gamify.elearning.entity.Question;

import java.util.List;

public class QuestionDTO {
    String id;
    String question;
    int ordinal;
    List<ChoiceDTO> choices;
    Question.AnswerType answerType;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Question.AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(Question.AnswerType answerType) {
        this.answerType = answerType;
    }

    public enum AnswerType {SINGLE, MULTIPLE}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceDTO> choices) {
        this.choices = choices;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}