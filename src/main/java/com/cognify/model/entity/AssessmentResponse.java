package com.cognify.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "assessment_responses")
public class AssessmentResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id")
    private AssessmentAttempt attempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private Integer numericAnswer;

    @Column(length = 255)
    private String selectedOption;

    public AssessmentResponse() {
    }

    public AssessmentResponse(AssessmentAttempt attempt, Question question, Integer numericAnswer, String selectedOption) {
        this.attempt = attempt;
        this.question = question;
        this.numericAnswer = numericAnswer;
        this.selectedOption = selectedOption;
    }

    public Long getId() {
        return id;
    }

    public AssessmentAttempt getAttempt() {
        return attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public Integer getNumericAnswer() {
        return numericAnswer;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setAttempt(AssessmentAttempt attempt) {
        this.attempt = attempt;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setNumericAnswer(Integer numericAnswer) {
        this.numericAnswer = numericAnswer;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}