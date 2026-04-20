package com.cognify.model.dto;

public class TraitComparisonResponse {
    private String traitName;
    private Integer oldScore;
    private Integer newScore;
    private Integer difference;
    private String changeType;

    public TraitComparisonResponse() {
    }

    public String getTraitName() {
        return traitName;
    }

    public Integer getOldScore() {
        return oldScore;
    }

    public Integer getNewScore() {
        return newScore;
    }

    public Integer getDifference() {
        return difference;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setTraitName(String traitName) {
        this.traitName = traitName;
    }

    public void setOldScore(Integer oldScore) {
        this.oldScore = oldScore;
    }

    public void setNewScore(Integer newScore) {
        this.newScore = newScore;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
}