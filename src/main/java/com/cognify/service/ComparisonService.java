package com.cognify.service;

import com.cognify.model.dto.ComparisonResultResponse;
import com.cognify.model.dto.TraitComparisonResponse;
import com.cognify.model.entity.AssessmentAttempt;
import com.cognify.model.entity.TraitScore;
import com.cognify.repository.AssessmentAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class ComparisonService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    public ComparisonService(AssessmentAttemptRepository assessmentAttemptRepository) {
        this.assessmentAttemptRepository = assessmentAttemptRepository;
    }

    public ComparisonResultResponse compareAttempts(Long oldAttemptId, Long newAttemptId) {
        AssessmentAttempt oldAttempt = assessmentAttemptRepository.findById(oldAttemptId)
                .orElseThrow(() -> new RuntimeException("Old attempt not found"));

        AssessmentAttempt newAttempt = assessmentAttemptRepository.findById(newAttemptId)
                .orElseThrow(() -> new RuntimeException("New attempt not found"));

        Map<String, Integer> oldScores = toTraitMap(oldAttempt.getTraitScores());
        Map<String, Integer> newScores = toTraitMap(newAttempt.getTraitScores());

        List<TraitComparisonResponse> comparisons = new ArrayList<>();
        List<Integer> differences = new ArrayList<>();
        List<String> stableTraits = new ArrayList<>();
        List<String> changedTraits = new ArrayList<>();

        Set<String> allTraits = new LinkedHashSet<>();
        allTraits.addAll(oldScores.keySet());
        allTraits.addAll(newScores.keySet());

        TraitComparisonResponse biggestIncrease = null;
        TraitComparisonResponse biggestDecrease = null;

        for (String trait : allTraits) {
            int oldScore = oldScores.getOrDefault(trait, 0);
            int newScore = newScores.getOrDefault(trait, 0);
            int difference = newScore - oldScore;

            differences.add(Math.abs(difference));

            TraitComparisonResponse response = new TraitComparisonResponse();
            response.setTraitName(trait);
            response.setOldScore(oldScore);
            response.setNewScore(newScore);
            response.setDifference(difference);
            response.setChangeType(classifyChange(Math.abs(difference)));
            comparisons.add(response);

            if ("Stable".equals(response.getChangeType())) {
                stableTraits.add(trait);
            } else {
                changedTraits.add(trait);
            }

            if (biggestIncrease == null || response.getDifference() > biggestIncrease.getDifference()) {
                biggestIncrease = response;
            }
            if (biggestDecrease == null || response.getDifference() < biggestDecrease.getDifference()) {
                biggestDecrease = response;
            }
        }

        int stabilityScore = calculateStabilityScore(differences);
        String comparisonSummary = generateComparisonSummary(oldAttempt, newAttempt, stableTraits, changedTraits, biggestIncrease, biggestDecrease, stabilityScore);
        String psychologicalInterpretation = generatePsychologicalInterpretation(oldAttempt, newAttempt, stableTraits, changedTraits, biggestIncrease, biggestDecrease, stabilityScore);

        ComparisonResultResponse result = new ComparisonResultResponse();
        result.setOldAttemptId(oldAttempt.getId());
        result.setNewAttemptId(newAttempt.getId());
        result.setOldMbtiType(oldAttempt.getMbtiType());
        result.setNewMbtiType(newAttempt.getMbtiType());
        result.setOldExpandedMbtiType(expandMbti(oldAttempt.getMbtiType()));
        result.setNewExpandedMbtiType(expandMbti(newAttempt.getMbtiType()));
        result.setStabilityScore(stabilityScore);
        result.setComparisonSummary(comparisonSummary);
        result.setStableTraits(stableTraits);
        result.setChangedTraits(changedTraits);
        result.setStrongestIncrease(biggestIncrease != null && biggestIncrease.getDifference() > 0 ? biggestIncrease : null);
        result.setStrongestDecrease(biggestDecrease != null && biggestDecrease.getDifference() < 0 ? biggestDecrease : null);
        result.setPsychologicalInterpretation(psychologicalInterpretation);
        result.setTraitComparisons(comparisons);

        return result;
    }

    private Map<String, Integer> toTraitMap(List<TraitScore> traitScores) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (TraitScore traitScore : traitScores) {
            map.put(traitScore.getTraitName(), traitScore.getScore());
        }
        return map;
    }

    private String classifyChange(int absDifference) {
        if (absDifference <= 5) {
            return "Stable";
        } else if (absDifference <= 14) {
            return "Moderate Change";
        } else {
            return "Significant Change";
        }
    }

    private int calculateStabilityScore(List<Integer> differences) {
        if (differences.isEmpty()) {
            return 100;
        }

        double averageDifference = differences.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        long significantChanges = differences.stream()
                .filter(diff -> diff >= 15)
                .count();

        int score = (int) Math.round(100 - (averageDifference * 2.0) - (significantChanges * 5));

        if (score < 0) {
            return 0;
        }
        if (score > 100) {
            return 100;
        }
        return score;
    }

    private String generateComparisonSummary(AssessmentAttempt oldAttempt,
                                             AssessmentAttempt newAttempt,
                                             List<String> stableTraits,
                                             List<String> changedTraits,
                                             TraitComparisonResponse biggestIncrease,
                                             TraitComparisonResponse biggestDecrease,
                                             int stabilityScore) {
        StringBuilder summary = new StringBuilder();
        summary.append("Previous alignment: ")
                .append(oldAttempt.getMbtiType())
                .append(". Current alignment: ")
                .append(newAttempt.getMbtiType())
                .append(". ");

        if (Objects.equals(oldAttempt.getMbtiType(), newAttempt.getMbtiType())) {
            summary.append("The broad personality signature stayed recognizably consistent across both attempts. ");
        } else {
            summary.append("The newer assessment suggests a meaningful rebalancing in preference style rather than a trivial fluctuation. ");
        }

        if (!stableTraits.isEmpty()) {
            summary.append("Stable ground remained strongest in ")
                    .append(String.join(", ", stableTraits))
                    .append(". ");
        }

        if (!changedTraits.isEmpty()) {
            summary.append("Most visible movement appeared in ")
                    .append(String.join(", ", changedTraits))
                    .append(". ");
        }

        if (biggestIncrease != null && biggestIncrease.getDifference() > 0) {
            summary.append("The sharpest lift appeared in ")
                    .append(biggestIncrease.getTraitName())
                    .append(" (+")
                    .append(biggestIncrease.getDifference())
                    .append("). ");
        }

        if (biggestDecrease != null && biggestDecrease.getDifference() < 0) {
            summary.append("The clearest drop appeared in ")
                    .append(biggestDecrease.getTraitName())
                    .append(" (")
                    .append(biggestDecrease.getDifference())
                    .append("). ");
        }

        summary.append("Overall stability score: ").append(stabilityScore).append("/100.");
        return summary.toString();
    }

    private String generatePsychologicalInterpretation(AssessmentAttempt oldAttempt,
                                                       AssessmentAttempt newAttempt,
                                                       List<String> stableTraits,
                                                       List<String> changedTraits,
                                                       TraitComparisonResponse biggestIncrease,
                                                       TraitComparisonResponse biggestDecrease,
                                                       int stabilityScore) {
        StringBuilder interpretation = new StringBuilder();

        if (stabilityScore >= 80) {
            interpretation.append("This looks less like reinvention and more like refinement. The underlying personality structure held its shape, which usually points to durable preferences rather than mood-driven noise. ");
        } else if (stabilityScore >= 60) {
            interpretation.append("The profile shows continuity with movement. Core tendencies remained visible, yet some domains appear to be reorganizing as context, maturity, or recent demands shift what is most active. ");
        } else {
            interpretation.append("The comparison suggests a genuine reshuffling of emphasis. That level of movement often reflects a major context change, prolonged stress, developmental growth, or a different mode of self-presentation becoming active. ");
        }

        if (!Objects.equals(oldAttempt.getMbtiType(), newAttempt.getMbtiType())) {
            interpretation.append("Because the MBTI alignment moved from ")
                    .append(oldAttempt.getMbtiType())
                    .append(" to ")
                    .append(newAttempt.getMbtiType())
                    .append(", the newer attempt is likely expressing a different balance between energy, attention, judgment, or structure than before. ");
        }

        if (!stableTraits.isEmpty()) {
            interpretation.append("Even so, stability in ")
                    .append(String.join(", ", stableTraits))
                    .append(" suggests there is still a recognizable backbone underneath the change. ");
        }

        if (biggestIncrease != null && biggestIncrease.getDifference() > 0) {
            interpretation.append("Growth in ")
                    .append(biggestIncrease.getTraitName())
                    .append(" may indicate a domain that has recently become more practiced, protected, or psychologically available. ");
        }

        if (biggestDecrease != null && biggestDecrease.getDifference() < 0) {
            interpretation.append("The decline in ")
                    .append(biggestDecrease.getTraitName())
                    .append(" could reflect fatigue, changing priorities, stronger realism, or less dependence on that mode than before. ");
        }

        if (changedTraits.isEmpty()) {
            interpretation.append("Taken together, the comparison reads as highly consistent across time.");
        } else {
            interpretation.append("Taken together, the change profile looks interpretable rather than random: some capacities stayed anchored while others clearly moved.");
        }

        return interpretation.toString();
    }

    private String expandMbti(String mbti) {
        if (mbti == null || mbti.length() != 4) {
            return "Unavailable";
        }

        return switch (mbti) {
            case "INTJ" -> "Introverted, Intuitive, Thinking, Judging";
            case "INTP" -> "Introverted, Intuitive, Thinking, Perceiving";
            case "ENTJ" -> "Extroverted, Intuitive, Thinking, Judging";
            case "ENTP" -> "Extroverted, Intuitive, Thinking, Perceiving";
            case "INFJ" -> "Introverted, Intuitive, Feeling, Judging";
            case "INFP" -> "Introverted, Intuitive, Feeling, Perceiving";
            case "ENFJ" -> "Extroverted, Intuitive, Feeling, Judging";
            case "ENFP" -> "Extroverted, Intuitive, Feeling, Perceiving";
            case "ISTJ" -> "Introverted, Sensing, Thinking, Judging";
            case "ISFJ" -> "Introverted, Sensing, Feeling, Judging";
            case "ESTJ" -> "Extroverted, Sensing, Thinking, Judging";
            case "ESFJ" -> "Extroverted, Sensing, Feeling, Judging";
            case "ISTP" -> "Introverted, Sensing, Thinking, Perceiving";
            case "ISFP" -> "Introverted, Sensing, Feeling, Perceiving";
            case "ESTP" -> "Extroverted, Sensing, Thinking, Perceiving";
            case "ESFP" -> "Extroverted, Sensing, Feeling, Perceiving";
            default -> "Unavailable";
        };
    }
}
