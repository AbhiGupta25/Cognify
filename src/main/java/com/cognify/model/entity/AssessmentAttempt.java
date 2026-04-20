package com.cognify.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessment_attempts")
public class AssessmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime attemptDate = LocalDateTime.now();

    @Column(length = 2000)
    private String summary;

    private String mbtiType;

    private String archetype;

    private Integer confidenceScore;

    private Integer contradictionCount;

    @Column(length = 2000)
    private String coreProfile;

    @Column(length = 2000)
    private String decisionPattern;

    @Column(length = 2000)
    private String socialPattern;

    @Column(length = 2000)
    private String stressPattern;

    @Column(length = 2000)
    private String behavioralPattern;

    @Column(length = 2000)
    private String cognitivePattern;

    @Column(length = 2000)
    private String adaptivePattern;

    @Column(length = 2000)
    private String contradictionAnalysis;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentResponse> responses = new ArrayList<>();

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TraitScore> traitScores = new ArrayList<>();

    public AssessmentAttempt() {
    }

    public AssessmentAttempt(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public String getSummary() {
        return summary;
    }

    public String getMbtiType() {
        return mbtiType;
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

    public String getCoreProfile() {
        return coreProfile;
    }

    public String getDecisionPattern() {
        return decisionPattern;
    }

    public String getSocialPattern() {
        return socialPattern;
    }

    public String getStressPattern() {
        return stressPattern;
    }

    public String getBehavioralPattern() {
        return behavioralPattern;
    }

    public String getCognitivePattern() {
        return cognitivePattern;
    }

    public String getAdaptivePattern() {
        return adaptivePattern;
    }

    public String getContradictionAnalysis() {
        return contradictionAnalysis;
    }

    public List<AssessmentResponse> getResponses() {
        return responses;
    }

    public List<TraitScore> getTraitScores() {
        return traitScores;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setMbtiType(String mbtiType) {
        this.mbtiType = mbtiType;
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

    public void setCoreProfile(String coreProfile) {
        this.coreProfile = coreProfile;
    }

    public void setDecisionPattern(String decisionPattern) {
        this.decisionPattern = decisionPattern;
    }

    public void setSocialPattern(String socialPattern) {
        this.socialPattern = socialPattern;
    }

    public void setStressPattern(String stressPattern) {
        this.stressPattern = stressPattern;
    }

    public void setBehavioralPattern(String behavioralPattern) {
        this.behavioralPattern = behavioralPattern;
    }

    public void setCognitivePattern(String cognitivePattern) {
        this.cognitivePattern = cognitivePattern;
    }

    public void setAdaptivePattern(String adaptivePattern) {
        this.adaptivePattern = adaptivePattern;
    }

    public void setContradictionAnalysis(String contradictionAnalysis) {
        this.contradictionAnalysis = contradictionAnalysis;
    }
}
