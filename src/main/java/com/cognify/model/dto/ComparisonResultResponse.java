package com.cognify.model.dto;

import java.util.List;

public class ComparisonResultResponse {
    private Long oldAttemptId;
    private Long newAttemptId;
    private String oldMbtiType;
    private String newMbtiType;
    private String oldExpandedMbtiType;
    private String newExpandedMbtiType;
    private Integer stabilityScore;
    private String comparisonSummary;
    private List<String> stableTraits;
    private List<String> changedTraits;
    private TraitComparisonResponse strongestIncrease;
    private TraitComparisonResponse strongestDecrease;
    private String psychologicalInterpretation;
    private List<TraitComparisonResponse> traitComparisons;

    public ComparisonResultResponse() {
    }

    public Long getOldAttemptId() {
        return oldAttemptId;
    }

    public Long getNewAttemptId() {
        return newAttemptId;
    }

    public String getOldMbtiType() {
        return oldMbtiType;
    }

    public String getNewMbtiType() {
        return newMbtiType;
    }

    public String getOldExpandedMbtiType() {
        return oldExpandedMbtiType;
    }

    public String getNewExpandedMbtiType() {
        return newExpandedMbtiType;
    }

    public Integer getStabilityScore() {
        return stabilityScore;
    }

    public String getComparisonSummary() {
        return comparisonSummary;
    }

    public List<String> getStableTraits() {
        return stableTraits;
    }

    public List<String> getChangedTraits() {
        return changedTraits;
    }

    public TraitComparisonResponse getStrongestIncrease() {
        return strongestIncrease;
    }

    public TraitComparisonResponse getStrongestDecrease() {
        return strongestDecrease;
    }

    public String getPsychologicalInterpretation() {
        return psychologicalInterpretation;
    }

    public List<TraitComparisonResponse> getTraitComparisons() {
        return traitComparisons;
    }

    public void setOldAttemptId(Long oldAttemptId) {
        this.oldAttemptId = oldAttemptId;
    }

    public void setNewAttemptId(Long newAttemptId) {
        this.newAttemptId = newAttemptId;
    }

    public void setOldMbtiType(String oldMbtiType) {
        this.oldMbtiType = oldMbtiType;
    }

    public void setNewMbtiType(String newMbtiType) {
        this.newMbtiType = newMbtiType;
    }

    public void setOldExpandedMbtiType(String oldExpandedMbtiType) {
        this.oldExpandedMbtiType = oldExpandedMbtiType;
    }

    public void setNewExpandedMbtiType(String newExpandedMbtiType) {
        this.newExpandedMbtiType = newExpandedMbtiType;
    }

    public void setStabilityScore(Integer stabilityScore) {
        this.stabilityScore = stabilityScore;
    }

    public void setComparisonSummary(String comparisonSummary) {
        this.comparisonSummary = comparisonSummary;
    }

    public void setStableTraits(List<String> stableTraits) {
        this.stableTraits = stableTraits;
    }

    public void setChangedTraits(List<String> changedTraits) {
        this.changedTraits = changedTraits;
    }

    public void setStrongestIncrease(TraitComparisonResponse strongestIncrease) {
        this.strongestIncrease = strongestIncrease;
    }

    public void setStrongestDecrease(TraitComparisonResponse strongestDecrease) {
        this.strongestDecrease = strongestDecrease;
    }

    public void setPsychologicalInterpretation(String psychologicalInterpretation) {
        this.psychologicalInterpretation = psychologicalInterpretation;
    }

    public void setTraitComparisons(List<TraitComparisonResponse> traitComparisons) {
        this.traitComparisons = traitComparisons;
    }
}
