package com.cognify.model.dto;

import java.util.List;

public class SimulationResponse {
    private String scenarioTitle;
    private String personalitySnapshot;
    private String likelyDefaultReaction;
    private String hiddenBlindSpot;
    private String whatYourBrainIsOptimizingFor;
    private String betterResponseStrategy;
    private List<String> threeStepActionPlan;
    private List<String> sevenDayGrowthPlan;
    private String reflectionPrompt;

    public SimulationResponse() {
    }

    public String getScenarioTitle() {
        return scenarioTitle;
    }

    public void setScenarioTitle(String scenarioTitle) {
        this.scenarioTitle = scenarioTitle;
    }

    public String getPersonalitySnapshot() {
        return personalitySnapshot;
    }

    public void setPersonalitySnapshot(String personalitySnapshot) {
        this.personalitySnapshot = personalitySnapshot;
    }

    public String getLikelyDefaultReaction() {
        return likelyDefaultReaction;
    }

    public void setLikelyDefaultReaction(String likelyDefaultReaction) {
        this.likelyDefaultReaction = likelyDefaultReaction;
    }

    public String getHiddenBlindSpot() {
        return hiddenBlindSpot;
    }

    public void setHiddenBlindSpot(String hiddenBlindSpot) {
        this.hiddenBlindSpot = hiddenBlindSpot;
    }

    public String getWhatYourBrainIsOptimizingFor() {
        return whatYourBrainIsOptimizingFor;
    }

    public void setWhatYourBrainIsOptimizingFor(String whatYourBrainIsOptimizingFor) {
        this.whatYourBrainIsOptimizingFor = whatYourBrainIsOptimizingFor;
    }

    public String getBetterResponseStrategy() {
        return betterResponseStrategy;
    }

    public void setBetterResponseStrategy(String betterResponseStrategy) {
        this.betterResponseStrategy = betterResponseStrategy;
    }

    public List<String> getThreeStepActionPlan() {
        return threeStepActionPlan;
    }

    public void setThreeStepActionPlan(List<String> threeStepActionPlan) {
        this.threeStepActionPlan = threeStepActionPlan;
    }

    public List<String> getSevenDayGrowthPlan() {
        return sevenDayGrowthPlan;
    }

    public void setSevenDayGrowthPlan(List<String> sevenDayGrowthPlan) {
        this.sevenDayGrowthPlan = sevenDayGrowthPlan;
    }

    public String getReflectionPrompt() {
        return reflectionPrompt;
    }

    public void setReflectionPrompt(String reflectionPrompt) {
        this.reflectionPrompt = reflectionPrompt;
    }
}
