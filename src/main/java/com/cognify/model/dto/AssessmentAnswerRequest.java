package com.cognify.model.dto;

public class AssessmentAnswerRequest {
    private Long questionId;
    private Integer numericAnswer;
    private String selectedOption;

    public AssessmentAnswerRequest() {
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Integer getNumericAnswer() {
        return numericAnswer;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setNumericAnswer(Integer numericAnswer) {
        this.numericAnswer = numericAnswer;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}