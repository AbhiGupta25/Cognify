package com.cognify.model.analysis;

import java.util.HashMap;
import java.util.Map;

public class ScoringContext {
    private Map<String, Integer> traitScores = new HashMap<>();
    private Map<String, Integer> axisScores = new HashMap<>();
    private int contradictionCount;

    public ScoringContext() {
        traitScores.put("Social Energy", 50);
        traitScores.put("Emotional Sensitivity", 50);
        traitScores.put("Decision Style", 50);
        traitScores.put("Structure Orientation", 50);
        traitScores.put("Cognitive Style", 50);
        traitScores.put("Stress Resilience", 50);
        traitScores.put("Adaptability", 50);
        traitScores.put("Behavioral Consistency", 50);

        axisScores.put("EI", 0);
        axisScores.put("SN", 0);
        axisScores.put("TF", 0);
        axisScores.put("JP", 0);
    }

    public Map<String, Integer> getTraitScores() {
        return traitScores;
    }

    public Map<String, Integer> getAxisScores() {
        return axisScores;
    }

    public int getContradictionCount() {
        return contradictionCount;
    }

    public void setContradictionCount(int contradictionCount) {
        this.contradictionCount = contradictionCount;
    }
}