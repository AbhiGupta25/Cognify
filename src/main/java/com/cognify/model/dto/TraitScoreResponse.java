package com.cognify.model.dto;

public class TraitScoreResponse {
    private String traitName;
    private Integer score;

    public TraitScoreResponse() {
    }

    public TraitScoreResponse(String traitName, Integer score) {
        this.traitName = traitName;
        this.score = score;
    }

    public String getTraitName() {
        return traitName;
    }

    public Integer getScore() {
        return score;
    }

    public void setTraitName(String traitName) {
        this.traitName = traitName;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}