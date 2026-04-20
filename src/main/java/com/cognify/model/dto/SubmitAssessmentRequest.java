package com.cognify.model.dto;

import java.util.List;

public class SubmitAssessmentRequest {
    private Long userId;
    private List<AssessmentAnswerRequest> answers;

    public SubmitAssessmentRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public List<AssessmentAnswerRequest> getAnswers() {
        return answers;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnswers(List<AssessmentAnswerRequest> answers) {
        this.answers = answers;
    }
}