package com.cognify.model.dto;

import java.util.List;

public class AssessmentResultResponse {
    private Long attemptId;
    private String mbtiType;
    private String expandedMbtiType;
    private String mbtiExplanation;
    private String archetype;
    private Integer confidenceScore;
    private Integer contradictionCount;
    private String summary;
    private String coreProfile;
    private String decisionPattern;
    private String socialPattern;
    private String stressPattern;
    private String behavioralPattern;
    private String cognitivePattern;
    private String adaptivePattern;
    private String contradictionAnalysis;
    private List<TraitScoreResponse> traitScores;

    public AssessmentResultResponse() {
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public String getMbtiType() {
        return mbtiType;
    }

    public String getExpandedMbtiType() {
        return expandedMbtiType;
    }

    public String getMbtiExplanation() {
        return mbtiExplanation;
    }

    public void setMbtiType(String mbtiType) {
        this.mbtiType = mbtiType;
    }

    public void setExpandedMbtiType(String expandedMbtiType) {
        this.expandedMbtiType = expandedMbtiType;
    }

    public void setMbtiExplanation(String mbtiExplanation) {
        this.mbtiExplanation = mbtiExplanation;
    }

    public String getArchetype() {
        return archetype;
    }

    public void setArchetype(String archetype) {
        this.archetype = archetype;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getContradictionCount() {
        return contradictionCount;
    }

    public void setContradictionCount(Integer contradictionCount) {
        this.contradictionCount = contradictionCount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoreProfile() {
        return coreProfile;
    }

    public void setCoreProfile(String coreProfile) {
        this.coreProfile = coreProfile;
    }

    public String getDecisionPattern() {
        return decisionPattern;
    }

    public void setDecisionPattern(String decisionPattern) {
        this.decisionPattern = decisionPattern;
    }

    public String getSocialPattern() {
        return socialPattern;
    }

    public void setSocialPattern(String socialPattern) {
        this.socialPattern = socialPattern;
    }

    public String getStressPattern() {
        return stressPattern;
    }

    public void setStressPattern(String stressPattern) {
        this.stressPattern = stressPattern;
    }

    public String getBehavioralPattern() {
        return behavioralPattern;
    }

    public void setBehavioralPattern(String behavioralPattern) {
        this.behavioralPattern = behavioralPattern;
    }

    public String getCognitivePattern() {
        return cognitivePattern;
    }

    public void setCognitivePattern(String cognitivePattern) {
        this.cognitivePattern = cognitivePattern;
    }

    public String getAdaptivePattern() {
        return adaptivePattern;
    }

    public void setAdaptivePattern(String adaptivePattern) {
        this.adaptivePattern = adaptivePattern;
    }

    public String getContradictionAnalysis() {
        return contradictionAnalysis;
    }

    public void setContradictionAnalysis(String contradictionAnalysis) {
        this.contradictionAnalysis = contradictionAnalysis;
    }

    public List<TraitScoreResponse> getTraitScores() {
        return traitScores;
    }

    public void setTraitScores(List<TraitScoreResponse> traitScores) {
        this.traitScores = traitScores;
    }
}
