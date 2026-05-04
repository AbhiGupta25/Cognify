package com.cognify.model.dto;

public class SimulationRequest {
    private Long userId;
    private Long attemptId;
    private String scenarioText;
    private String scenarioCategory;

    public SimulationRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public String getScenarioText() {
        return scenarioText;
    }

    public void setScenarioText(String scenarioText) {
        this.scenarioText = scenarioText;
    }

    public String getScenarioCategory() {
        return scenarioCategory;
    }

    public void setScenarioCategory(String scenarioCategory) {
        this.scenarioCategory = scenarioCategory;
    }
}
