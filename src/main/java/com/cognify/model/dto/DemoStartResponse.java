package com.cognify.model.dto;

public class DemoStartResponse {
    private UserSignupResponse user;
    private AssessmentResultResponse result;

    public DemoStartResponse() {
    }

    public DemoStartResponse(UserSignupResponse user, AssessmentResultResponse result) {
        this.user = user;
        this.result = result;
    }

    public UserSignupResponse getUser() {
        return user;
    }

    public void setUser(UserSignupResponse user) {
        this.user = user;
    }

    public AssessmentResultResponse getResult() {
        return result;
    }

    public void setResult(AssessmentResultResponse result) {
        this.result = result;
    }
}
