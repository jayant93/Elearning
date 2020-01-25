package com.gamify.elearning.repository.projection;

import java.util.List;

import com.gamify.elearning.entity.Question;

import org.springframework.data.rest.core.config.Projection;

/**
 * @author marvin
 */
@Projection(name = "question", types = {Question.class})
public interface QuestionProjection {
    
    String getId();

    String getQuestion();

    String getAnswerType();

    List<ChoiceProjection> getChoices();

    Integer getOrdinal();
}