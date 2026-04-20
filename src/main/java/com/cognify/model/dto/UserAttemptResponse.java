package com.cognify.model.dto;

import java.time.LocalDateTime;

public class UserAttemptResponse {
    private Long attemptId;
    private LocalDateTime attemptDate;
    private String mbtiType;
    private String expandedMbtiType;
    private String archetype;
    private Integer confidenceScore;
    private Integer contradictionCount;
    private String summary;

    public UserAttemptResponse() {
    }

    public UserAttemptResponse(Long attemptId,
                               LocalDateTime attemptDate,
                               String mbtiType,
                               String expandedMbtiType,
                               String archetype,
                               Integer confidenceScore,
                               Integer contradictionCount,
                               String summary) {
        this.attemptId = attemptId;
        this.attemptDate = attemptDate;
        this.mbtiType = mbtiType;
        this.expandedMbtiType = expandedMbtiType;
        this.archetype = archetype;
        this.confidenceScore = confidenceScore;
        this.contradictionCount = contradictionCount;
        this.summary = summary;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public String getMbtiType() {
        return mbtiType;
    }

    public String getExpandedMbtiType() {
        return expandedMbtiType;
    }

    public String getArchetype() {
        return archetype;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public Integer getContradictionCount() {
        return contradictionCount;
    }

    public String getSummary() {
        return summary;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public void setAttemptDate(LocalDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }

    public void setMbtiType(String mbtiType) {
        this.mbtiType = mbtiType;
    }

    public void setExpandedMbtiType(String expandedMbtiType) {
        this.expandedMbtiType = expandedMbtiType;
    }

    public void setArchetype(String archetype) {
        this.archetype = archetype;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public void setContradictionCount(Integer contradictionCount) {
        this.contradictionCount = contradictionCount;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
