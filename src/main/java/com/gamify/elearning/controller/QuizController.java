package com.gamify.elearning.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.gamify.elearning.dto.AnswerDTO;
import com.gamify.elearning.dto.ChoiceDTO;
import com.gamify.elearning.dto.QuestionDTO;
import com.gamify.elearning.dto.QuizDTO;
import com.gamify.elearning.dto.QuizResultDTO;
import com.gamify.elearning.entity.*;
import com.gamify.elearning.repository.*;
import com.gamify.elearning.repository.projection.QuizProjection;
import com.gamify.elearning.repository.projection.QuizProjection2;
import com.ideyatech.opentides.core.repository.SystemCodesRepository;
import com.ideyatech.opentides.core.web.controller.BaseRestController;
import com.ideyatech.opentides.um.repository.UserRepository;
import com.ideyatech.opentides.um.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@BasePathAwareController
//@RestController
@RequestMapping("/api/quiz")
public class QuizController extends BaseRestController<Quiz> {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
	private ElementRepository elementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElementProgressRepository elementProgressRepository;

    @Autowired
    private ELearningUserRepository elearningUserRepository;

    @Autowired
    private SystemCodesRepository<String> systemCodesRepository;

    // @GetMapping("/find/{id}")
    // public @ResponseBody ResponseEntity<?> getQuizByLessonId(@PathVariable String id) {
    //     Quiz quiz = new Quiz();
    //     // quiz.setLe
    //     return ResponseEntity.ok(quizRepository.findByExample(arg0, arg1, arg2, arg3)));
    // }

    @GetMapping("/findAll")
    public @ResponseBody ResponseEntity<?> getQuizzes() {
        return ResponseEntity.ok(quizRepository.findAll());
    }

    @GetMapping("/findAll/count")
    public @ResponseBody ResponseEntity<?> getQuizCount() {
        long total = (Long) quizRepository.getTotalQuizzesActive();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getQuizById(@PathVariable String id) {
        Quiz quiz = quizRepository.findOne(id);

        // Map<String, Object> quizMap = new HashMap<>();
        // quizMap.put("title", quiz.getTitle());
        // quizMap.put("id", quiz.getId());
        // quizMap.put("lessonId", quiz.getLesson().getId());
        // quizMap.put("lessonNo", quiz.getLesson().getOrdinal());
        // quizMap.put("elementNo", quiz.getOrdinal());
        // quizMap.put("passingRate", quiz.getPassingRate());
        // List<Map<String, Object>> questionList = new ArrayList<>();
        // for (Question question : quiz.getQuestions()) {
        //     Map<String, Object> questionMap = new HashMap<>();
        //     questionMap.put("id", question.getId());
        //     questionMap.put("question", question.getQuestion());
        //     questionMap.put("answerType", question.getAnswerType());
        //     questionMap.put("ordinal", question.getOrdinal());

        //     List<Map<String, Object>> choiceList = new ArrayList<>();
        //     for (Choice choice : question.getChoices()) {
        //         Map<String, Object> choiceMap = new HashMap<>();
        //         choiceMap.put("value", choice.getValue());
        //         choiceMap.put("correctAnswer", choice.getCorrectAnswer());
        //         choiceMap.put("ordinal", choice.getOrdinal());
        //         choiceList.add(choiceMap);
        //     }
        //     questionMap.put("choices", choiceList);
        //     questionList.add(questionMap);
        // }
        // quizMap.put("questions", questionList);

        return ResponseEntity.ok(projectionFactory.createProjection(QuizProjection2.class, quiz));
    }

    @PostMapping("/save")
    public @ResponseBody ResponseEntity<?> saveQuiz(@RequestBody QuizDTO quizDTO, BindingResult bindingResult, HttpServletRequest request) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
		ELearningUser user = elearningUserRepository.findOne(userId);

        Lesson lesson = lessonRepository.findOne(quizDTO.getLessonId());
        Quiz newQuiz = new Quiz();
        newQuiz.setLesson(lesson);
		newQuiz.setTitle(quizDTO.getTitle());
		newQuiz.setPassingRate(quizDTO.getPassingRate());
		List<Question> questions = new ArrayList<>();
		for (QuestionDTO questionDTO: quizDTO.getQuestions()) {
            Question newQuestion = new Question();
            newQuestion.setAnswerType(questionDTO.getAnswerType());
            newQuestion.setQuestion(questionDTO.getQuestion());
            newQuestion.setOrdinal(questionDTO.getOrdinal());
            newQuestion.setQuiz(newQuiz);
            List<Choice> choices = new ArrayList<>();
            for (ChoiceDTO choiceDTO: questionDTO.getChoices()) {
                Choice newChoice = new Choice();
                newChoice.setCorrectAnswer(choiceDTO.getCorrectAnswer());
                newChoice.setValue(choiceDTO.getValue());
                newChoice.setQuestion(newQuestion);
                choices.add(newChoice);
            }
            newQuestion.setChoices(choices);
            questions.add(newQuestion);
        }
        newQuiz.setQuestions(questions);
        newQuiz.setUser(user);
        newQuiz.setOrdinal(lesson.getNumOfElements());
        //		newQuiz.setType(systemCodesRepository.findByKey("ELEMENT_TYPE_QUIZ"));
        lesson.setNumOfElements(lesson.getNumOfElements()+1);
        lesson.setNumOfQuizzes(lesson.getNumOfQuizzes()+1);
        lessonRepository.save(lesson);

        newQuiz = quizRepository.save(newQuiz);
        if(newQuiz != null) {
            return ResponseEntity.ok(projectionFactory.createProjection(QuizProjection.class, newQuiz));
        }

        return new ResponseEntity<String>("Could not save quiz.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/update")
    public @ResponseBody ResponseEntity<?> updateQuiz(@RequestBody QuizDTO quizDTO, BindingResult bindingResult, HttpServletRequest request) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
        ELearningUser user = elearningUserRepository.findOne(userId);

        Quiz oldQuiz = quizRepository.findOne(quizDTO.getId());

        // Delete missing elements from DTO
        for (int i = oldQuiz.getQuestions().size()-1; i >= 0; i--) {
            Question question = oldQuiz.getQuestions().get(i);
            boolean foundQuestion = false;
            for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
                if(questionDTO.getId().equals(question.getId())) {
                    foundQuestion = true;

                    for (int j = question.getChoices().size()-1; j >= 0; j--) {
                        boolean foundChoice = false;
                        Choice choice = question.getChoices().get(j);
                        for (ChoiceDTO choiceDTO : questionDTO.getChoices()) {
                            if(choiceDTO.getId() != null && choiceDTO.getId().equals(choice.getId())) {
                                foundChoice = true;
                                break;
                            }
                        }
                        if(!foundChoice) {
                            choice.setDeleted(true);
                            choiceRepository.save(choice);
                        }
                    }
                    break;
                }
            }
            if(!foundQuestion) {
                question.setDeleted(true);
                questionRepository.save(question);
            }
        }

        // Update or add questions/choices
        for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
            Question question = questionRepository.findOne(questionDTO.getId());
            Question newQuestion = null;
            if (question != null) {
                question.setQuestion(questionDTO.getQuestion());
                question.setAnswerType(questionDTO.getAnswerType());
                question.setOrdinal(questionDTO.getOrdinal());
            } else {
                newQuestion = new Question();
                newQuestion.setQuestion(questionDTO.getQuestion());
                newQuestion.setAnswerType(questionDTO.getAnswerType());
                newQuestion.setOrdinal(questionDTO.getOrdinal());
                newQuestion.setQuiz(oldQuiz);
                newQuestion.setChoices(new ArrayList<>());
            }

            for (ChoiceDTO choiceDTO : questionDTO.getChoices()) {
                Choice choice = null;
                if (choiceDTO.getId() != null) {
                    choice = choiceRepository.findOne(choiceDTO.getId());
                }
                if (choice != null) {
                    choice.setValue(choiceDTO.getValue());
                    choice.setCorrectAnswer(choiceDTO.getCorrectAnswer());
                    choice.setOrdinal(choiceDTO.getOrdinal());
                    choiceRepository.save(choice);
                } else {
                    Choice newChoice = new Choice();
                    newChoice.setValue(choiceDTO.getValue());
                    newChoice.setCorrectAnswer(choiceDTO.getCorrectAnswer());
                    newChoice.setOrdinal(choiceDTO.getOrdinal());
                    if (question != null) {
                        newChoice.setQuestion(question);
                        question.getChoices().add(newChoice);
                    } else if(newQuestion != null) {
                        newChoice.setQuestion(newQuestion);
                        newQuestion.getChoices().add(newChoice);
                    }
                }
            }
            if(newQuestion != null) {
                oldQuiz.getQuestions().add(newQuestion);
            }
        }

        oldQuiz.setPassingRate(quizDTO.getPassingRate());
        oldQuiz.setTitle(quizDTO.getTitle());

        if (!oldQuiz.getLesson().getId().equals(quizDTO.getLessonId())) {
            Lesson oldLesson = oldQuiz.getLesson();
            oldLesson.setNumOfElements(oldLesson.getNumOfElements()-1);
            oldLesson.setNumOfQuizzes(oldLesson.getNumOfQuizzes()-1);
            lessonRepository.save(oldLesson);
            Lesson newLesson = lessonRepository.findOne(quizDTO.getLessonId());
            newLesson.setNumOfElements(newLesson.getNumOfElements()+1);
            newLesson.setNumOfQuizzes(newLesson.getNumOfQuizzes()+1);
            lessonRepository.save(newLesson);
            oldQuiz.setOrdinal(newLesson.getNumOfElements());
            oldQuiz.setLesson(newLesson);
        }

        Quiz newQuiz = quizRepository.save(oldQuiz);
        if(newQuiz != null) {
            return ResponseEntity.ok(projectionFactory.createProjection(QuizProjection.class, newQuiz));
        }

        return new ResponseEntity<String>("Could not save quiz.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/{id}/delete")
    public @ResponseBody ResponseEntity<?> deleteQuiz(@PathVariable String id, HttpServletRequest request) {
        Quiz quiz = quizRepository.findOne(id);
        Lesson lesson = quiz.getLesson();
        // return ResponseEntity.ok("Quiz [" + id + "] has been deleted.");
        lesson.setNumOfElements(lesson.getNumOfElements()-1);
        lesson.setNumOfQuizzes(lesson.getNumOfQuizzes()-1);
        lesson.getElements().remove(quiz);
        List<Element> toSave = new ArrayList<>();
		for(int i = 0; i < lesson.getElements().size(); i++) {
			Element element = lesson.getElements().get(i);
			element.setOrdinal(i+1);
			toSave.add(element);
        }
        elementRepository.save(toSave);
        lessonRepository.save(lesson);
        quiz.setDeleted(true);
        quizRepository.save(quiz);
        List<ElementProgress> elemProgList = elementProgressRepository.findByElementId(id);
        for (ElementProgress elemProg : elemProgList) {
            elemProg.setDeleted(true);
        }
        elementProgressRepository.save(elemProgList);

        return ResponseEntity.ok("Quiz [" + id + "] has been deleted.");
    }

    @GetMapping("/result/{id}")
    public @ResponseBody ResponseEntity<?> getQuizResultById(@PathVariable String id) {
        return ResponseEntity.ok(quizResultRepository.findOne(id));
    }

    @PostMapping("/result/save")
    public @ResponseBody ResponseEntity<?> saveQuizResult(@RequestBody QuizResultDTO quizResultDTO, BindingResult bindingResult, HttpServletRequest request) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
        ELearningUser user = elearningUserRepository.findOne(userId);
        
        QuizResult newResult = new QuizResult();
        newResult.setScore(quizResultDTO.getScore());
        newResult.setTotalItems(quizResultDTO.getTotalItems());
        Quiz quiz = quizRepository.findOne(quizResultDTO.getQuizId());
        if(quiz == null) {
            return new ResponseEntity<String>("Quiz does not exist. Result is not saved.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        newResult.setQuiz(quiz);
        newResult.setPassed(quizResultDTO.getPassed());
        newResult.setDateTaken(new Date());
        newResult.setUser(user);

        QuizResult qr = quizResultRepository.save(newResult);
        
        if(qr != null) {
            return ResponseEntity.ok("Successfully saved quiz result.");
        }
        
        return new ResponseEntity<String>("Could not save quiz result.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/answer/save")
    public @ResponseBody ResponseEntity<?> saveAnswers(@RequestBody List<AnswerDTO> answersDTO, BindingResult bindingResult, HttpServletRequest request) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
        ELearningUser user = elearningUserRepository.findOne(userId);
        
        List<Answer> newAnswers = new ArrayList<>();
        for (AnswerDTO answer : answersDTO) {
            Answer newAnswer = new Answer();
            newAnswer.setUser(user);
            newAnswer.setAnswer(answer.getAnswer());
            newAnswer.setCorrect(answer.getCorrect());
            Question question = questionRepository.findOne(answer.getQuestionId());
            newAnswer.setQuestion(question);
            newAnswers.add(newAnswer);
        }
        List<Answer> savedAnswers = (List<Answer>) answerRepository.save(newAnswers);
        if(savedAnswers.size() != 0) {
            return ResponseEntity.ok("Successfully saved quiz answers.");
        }

        return new ResponseEntity<String>("Could not save quiz answers.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/entries/{id}")
    public @ResponseBody ResponseEntity<?> getQuizEntriesCount(@PathVariable String id) {
        return ResponseEntity.ok(quizResultRepository.countEntries(id));
    }

    @GetMapping("/top/{id}")
    public @ResponseBody ResponseEntity<?> getTopScorer(@PathVariable String id) {
        return ResponseEntity.ok(listMap(quizResultRepository.getResultsByQuizId(id, new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "score", "dateTaken")))));
    }

    @GetMapping("/results/date/asc/{id}/{page}/{size}")
    public @ResponseBody ResponseEntity<?> getQuizResultsOrderByScoreASC(@PathVariable String id, @PathVariable Integer page, @PathVariable Integer size) {
        return ResponseEntity.ok(listMap(quizResultRepository.getResultsByQuizId(id, new PageRequest(page, size, new Sort(Sort.Direction.ASC, "dateTaken")))));
    }

    @GetMapping("/results/date/desc/{id}/{page}/{size}")
    public @ResponseBody ResponseEntity<?> getQuizResultsOrderByScoreDESC(@PathVariable String id, @PathVariable Integer page, @PathVariable Integer size) {
        return ResponseEntity.ok(listMap(quizResultRepository.getResultsByQuizId(id, new PageRequest(page, size, new Sort(Sort.Direction.DESC, "dateTaken")))));
    }

    @GetMapping("/results/score/asc/{id}/{page}/{size}")
    public @ResponseBody ResponseEntity<?> getQuizResultsOrderByDateASC(@PathVariable String id, @PathVariable Integer page, @PathVariable Integer size) {
        return ResponseEntity.ok(listMap(quizResultRepository.getResultsByQuizId(id, new PageRequest(page, size, new Sort(Sort.Direction.ASC, "score")))));
    }

    @GetMapping("/results/score/desc/{id}/{page}/{size}")
    public @ResponseBody ResponseEntity<?> getQuizResultsOrderByDateDESC(@PathVariable String id, @PathVariable Integer page, @PathVariable Integer size) {
        return ResponseEntity.ok(listMap(quizResultRepository.getResultsByQuizId(id, new PageRequest(page, size, new Sort(Sort.Direction.DESC, "score")))));
    }

    private List<Map<String, Object>> listMap(List<QuizResult> results) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuizResult result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("userCompleteName", result.getUser().getCompleteName());
            map.put("userEmailAddress", result.getUser().getEmailAddress());
            map.put("quizTitle", result.getQuiz().getTitle());
            map.put("passed", result.getPassed());
            map.put("score", ((float) result.getScore() / (float) result.getTotalItems()) * 100.0);
            map.put("dateTaken", result.getDateTaken());
            map.put("lessonNo", result.getQuiz().getLesson().getOrdinal());
            map.put("profilePhotoUrl", result.getUser().getProfilePhotoUrl());
            list.add(map);
        }
        return list;
    }

    @GetMapping("/results/course/{id}")
    public @ResponseBody ResponseEntity<?> getUserQuizResultsByCourse(@PathVariable String id) {
        String userId = SecurityUtil.getJwtSessionUser().getId();
        ELearningUser user = elearningUserRepository.findOne(userId);

        List<Map<String, Object>> resultList = new ArrayList<>();
        Course course = courseRepository.findOne(id);
        for (Lesson lesson : course.getLessons()) {
            for (Element element : lesson.getElements()) {
                if (element instanceof Quiz) {
                    List<QuizResult> results = quizResultRepository.getResultsByQuizAndUser(element.getId(), user.getId());
                    if (results.size() > 0) {
                        for (QuizResult result : results) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("lessonNo", lesson.getOrdinal());
                            map.put("quizNo", element.getOrdinal());
                            map.put("quizTitle", element.getTitle());

                            map.put("userCompleteName", result.getUser().getCompleteName());
                            map.put("userEmailAddress", result.getUser().getEmailAddress());
                            map.put("quizTitle", result.getQuiz().getTitle());
                            map.put("passed", result.getPassed());
                            map.put("score", ((float) result.getScore() / (float) result.getTotalItems()) * 100.0);
                            map.put("dateTaken", result.getDateTaken());
                            map.put("lessonNo", result.getQuiz().getLesson().getOrdinal());
                            map.put("profilePhotoUrl", result.getUser().getProfilePhotoUrl());
                            resultList.add(map);
                        }
                    }
                    else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("lessonNo", lesson.getOrdinal());
                        map.put("quizNo", element.getOrdinal());
                        map.put("quizTitle", element.getTitle());
                        map.put("score", null);
                        map.put("dateTaken", null);
                        map.put("passed", null);
                        resultList.add(map);
                    }

                }
            }
        }

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/results/recent/takers")
    public @ResponseBody ResponseEntity<?> getRecentQuizTakers() {
        String userId = SecurityUtil.getJwtSessionUser().getId();
        List<ELearningUser> recentTakers = quizResultRepository.getRecentQuizTakersOfMyCourses(userId, new PageRequest(0, 5));

        List<Map<String, Object>> takersList = new ArrayList<>();
		for (ELearningUser user: recentTakers) {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("id", user.getId());
			userMap.put("profilePhotoUrl", user.getProfilePhotoUrl());
            userMap.put("completeName", user.getShortenedName());
			takersList.add(userMap);
		}
        return ResponseEntity.ok(recentTakers);
    }

    @GetMapping("/results/{id}/passed/count")
    public @ResponseBody ResponseEntity<?> getPassersOfQuiz(@PathVariable String id) {
        int count = quizResultRepository.countPassedQuizzes(id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/results/{id}/failed/count")
    public @ResponseBody ResponseEntity<?> getFailuresOfQuiz(@PathVariable String id) {
        int count = quizResultRepository.countFailedQuizzes(id);
        return ResponseEntity.ok(count);
    }

}