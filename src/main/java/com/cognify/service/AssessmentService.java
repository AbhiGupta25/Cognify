package com.cognify.service;

import com.cognify.model.analysis.ScoringContext;
import com.cognify.model.dto.AssessmentResultResponse;
import com.cognify.model.dto.QuestionResponse;
import com.cognify.model.dto.SubmitAssessmentRequest;
import com.cognify.model.dto.TraitScoreResponse;
import com.cognify.model.entity.AssessmentAttempt;
import com.cognify.model.entity.AssessmentResponse;
import com.cognify.model.entity.Question;
import com.cognify.model.entity.QuestionMapping;
import com.cognify.model.entity.TraitScore;
import com.cognify.model.entity.User;
import com.cognify.repository.AssessmentAttemptRepository;
import com.cognify.repository.AssessmentResponseRepository;
import com.cognify.repository.QuestionMappingRepository;
import com.cognify.repository.QuestionRepository;
import com.cognify.repository.TraitScoreRepository;
import com.cognify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssessmentService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMappingRepository questionMappingRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssessmentResponseRepository assessmentResponseRepository;
    private final TraitScoreRepository traitScoreRepository;

    public AssessmentService(UserRepository userRepository,
                             QuestionRepository questionRepository,
                             QuestionMappingRepository questionMappingRepository,
                             AssessmentAttemptRepository assessmentAttemptRepository,
                             AssessmentResponseRepository assessmentResponseRepository,
                             TraitScoreRepository traitScoreRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.questionMappingRepository = questionMappingRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.assessmentResponseRepository = assessmentResponseRepository;
        this.traitScoreRepository = traitScoreRepository;
    }

    public List<QuestionResponse> getAllQuestions() {
        List<Question> questions = questionRepository.findAllByOrderByDisplayOrderAsc();
        List<QuestionResponse> responseList = new ArrayList<>();

        for (Question question : questions) {
            responseList.add(new QuestionResponse(
                    question.getId(),
                    question.getText(),
                    question.getType().name(),
                    question.getDisplayOrder()
            ));
        }

        return responseList;
    }

    public AssessmentResultResponse getAttemptResult(Long attemptId, Long userId) {
        AssessmentAttempt attempt = assessmentAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Assessment attempt not found."));

        if (userId != null && !attempt.getUser().getId().equals(userId)) {
            throw new RuntimeException("That attempt does not belong to the current user.");
        }

        return toResultResponse(attempt);
    }

    @Transactional
    public AssessmentResultResponse submitAssessment(SubmitAssessmentRequest request) {
        validateRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssessmentAttempt attempt = new AssessmentAttempt(user);
        assessmentAttemptRepository.save(attempt);

        ScoringContext context = new ScoringContext();
        Map<Long, Integer> answerMap = new HashMap<>();

        for (var answerRequest : request.getAnswers()) {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found: " + answerRequest.getQuestionId()));

            AssessmentResponse response = new AssessmentResponse(
                    attempt,
                    question,
                    answerRequest.getNumericAnswer(),
                    answerRequest.getSelectedOption()
            );
            assessmentResponseRepository.save(response);

            answerMap.put(question.getId(), answerRequest.getNumericAnswer());
            applyMappings(question, answerRequest.getNumericAnswer(), context);
        }

        int contradictionCount = calculateContradictions(answerMap);
        context.setContradictionCount(contradictionCount);
        clampTraitScores(context);

        String mbtiType = generateMbtiType(context);
        String expandedMbti = expandMbti(mbtiType);
        String mbtiExplanation = generateMbtiExplanation(mbtiType);
        int confidenceScore = calculateConfidence(context);
        String archetype = generateArchetype(context);
        String coreProfile = generateCoreProfile(context, mbtiType, expandedMbti, archetype);
        String decisionPattern = generateDecisionPattern(context);
        String socialPattern = generateSocialPattern(context);
        String stressPattern = generateStressPattern(context);
        String behavioralPattern = generateBehavioralPattern(context);
        String cognitivePattern = generateCognitivePattern(context);
        String adaptivePattern = generateAdaptivePattern(context);
        String contradictionAnalysis = generateContradictionAnalysis(contradictionCount);

        String summary = generateMasterSummary(
                mbtiType,
                expandedMbti,
                archetype,
                confidenceScore,
                contradictionCount,
                coreProfile,
                adaptivePattern
        );

        attempt.setMbtiType(mbtiType);
        attempt.setArchetype(archetype);
        attempt.setConfidenceScore(confidenceScore);
        attempt.setContradictionCount(contradictionCount);
        attempt.setSummary(summary);
        attempt.setCoreProfile(coreProfile);
        attempt.setDecisionPattern(decisionPattern);
        attempt.setSocialPattern(socialPattern);
        attempt.setStressPattern(stressPattern);
        attempt.setBehavioralPattern(behavioralPattern);
        attempt.setCognitivePattern(cognitivePattern);
        attempt.setAdaptivePattern(adaptivePattern);
        attempt.setContradictionAnalysis(contradictionAnalysis);
        assessmentAttemptRepository.save(attempt);

        for (Map.Entry<String, Integer> entry : context.getTraitScores().entrySet()) {
            traitScoreRepository.save(new TraitScore(attempt, entry.getKey(), entry.getValue()));
        }

        AssessmentResultResponse result = toResultResponse(attempt);
        result.setExpandedMbtiType(expandedMbti);
        result.setMbtiExplanation(mbtiExplanation);
        return result;
    }

    private AssessmentResultResponse toResultResponse(AssessmentAttempt attempt) {
        AssessmentResultResponse result = new AssessmentResultResponse();
        result.setAttemptId(attempt.getId());
        result.setMbtiType(attempt.getMbtiType());
        result.setExpandedMbtiType(expandMbti(attempt.getMbtiType()));
        result.setMbtiExplanation(generateMbtiExplanation(attempt.getMbtiType()));
        result.setArchetype(defaultIfBlank(attempt.getArchetype(), "Balanced Integrator"));
        result.setConfidenceScore(attempt.getConfidenceScore());
        result.setContradictionCount(attempt.getContradictionCount());
        result.setSummary(defaultIfBlank(attempt.getSummary(), "A detailed summary will appear after a fresh assessment."));
        result.setCoreProfile(defaultIfBlank(attempt.getCoreProfile(), "A full identity readout is not available for this older attempt."));
        result.setDecisionPattern(defaultIfBlank(attempt.getDecisionPattern(), "Decision analysis is not available for this older attempt."));
        result.setSocialPattern(defaultIfBlank(attempt.getSocialPattern(), "Social energy analysis is not available for this older attempt."));
        result.setStressPattern(defaultIfBlank(attempt.getStressPattern(), "Stress analysis is not available for this older attempt."));
        result.setBehavioralPattern(defaultIfBlank(attempt.getBehavioralPattern(), "Behavioral pattern analysis is not available for this older attempt."));
        result.setCognitivePattern(defaultIfBlank(attempt.getCognitivePattern(), "Cognitive analysis is not available for this older attempt."));
        result.setAdaptivePattern(defaultIfBlank(attempt.getAdaptivePattern(), "Adaptive pattern analysis is not available for this older attempt."));
        result.setContradictionAnalysis(defaultIfBlank(attempt.getContradictionAnalysis(), "Contradiction analysis is not available for this older attempt."));
        result.setTraitScores(mapTraitScores(attempt));
        return result;
    }

    private List<TraitScoreResponse> mapTraitScores(AssessmentAttempt attempt) {
        List<TraitScoreResponse> traitScoreResponses = new ArrayList<>();
        for (TraitScore traitScore : attempt.getTraitScores()) {
            traitScoreResponses.add(new TraitScoreResponse(traitScore.getTraitName(), traitScore.getScore()));
        }
        return traitScoreResponses;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void validateRequest(SubmitAssessmentRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new RuntimeException("A valid user is required to submit an assessment.");
        }

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Assessment answers cannot be empty.");
        }

        for (var answer : request.getAnswers()) {
            if (answer.getQuestionId() == null) {
                throw new RuntimeException("Each answer must include a question ID.");
            }

            if (answer.getNumericAnswer() == null || answer.getNumericAnswer() < 1 || answer.getNumericAnswer() > 5) {
                throw new RuntimeException("Answers must use the 1 to 5 assessment scale.");
            }
        }
    }

    private String expandMbti(String mbti) {
        if (mbti == null || mbti.length() != 4) {
            return "Unavailable";
        }

        Map<Character, String> map = Map.of(
                'I', "Introverted",
                'E', "Extroverted",
                'N', "Intuitive",
                'S', "Sensing",
                'F', "Feeling",
                'T', "Thinking",
                'J', "Judging",
                'P', "Perceiving"
        );

        StringBuilder expanded = new StringBuilder();
        for (char c : mbti.toCharArray()) {
            expanded.append(map.get(c)).append(", ");
        }
        return expanded.substring(0, expanded.length() - 2);
    }

    private String generateMbtiExplanation(String mbti) {
        if (mbti == null || mbti.length() != 4) {
            return "A full MBTI explanation is unavailable for this attempt.";
        }

        char[] letters = mbti.toCharArray();
        return describeMbtiDimension(letters[0]) + " " + describeMbtiDimension(letters[1]) + " "
                + describeMbtiDimension(letters[2]) + " " + describeMbtiDimension(letters[3]);
    }

    private String describeMbtiDimension(char letter) {
        return switch (letter) {
            case 'I' -> "Introverted means attention narrows inward first: solitude, reflection, and selective interaction help you recover clarity.";
            case 'E' -> "Extroverted means energy moves outward first: dialogue, motion, and visible engagement sharpen your thinking.";
            case 'N' -> "Intuitive means the mind looks past surface facts toward patterns, implications, and future possibilities.";
            case 'S' -> "Sensing means trust builds through concrete evidence, lived detail, and what can be observed directly.";
            case 'F' -> "Feeling means decisions stay aware of values, tone, and human impact rather than treating emotion as noise.";
            case 'T' -> "Thinking means judgment leans on logic, consistency, and impersonal structure before sentiment.";
            case 'J' -> "Judging means closure feels stabilizing: plans, commitments, and defined direction create momentum.";
            case 'P' -> "Perceiving means flexibility stays alive: room to adapt, explore, and revise keeps you responsive.";
            default -> "";
        };
    }

    private void applyMappings(Question question, Integer numericAnswer, ScoringContext context) {
        List<QuestionMapping> mappings = questionMappingRepository.findByQuestion(question);
        int normalized = normalizeAnswer(numericAnswer);

        for (QuestionMapping mapping : mappings) {
            int effect = normalized * mapping.getWeight() * mapping.getDirection();

            switch (mapping.getTargetType()) {
                case TRAIT -> {
                    Map<String, Integer> traitScores = context.getTraitScores();
                    traitScores.put(
                            mapping.getTargetKey(),
                            traitScores.getOrDefault(mapping.getTargetKey(), 50) + effect
                    );
                }
                case AXIS -> {
                    Map<String, Integer> axisScores = context.getAxisScores();
                    axisScores.put(
                            mapping.getTargetKey(),
                            axisScores.getOrDefault(mapping.getTargetKey(), 0) + effect
                    );
                }
            }
        }
    }

    private int normalizeAnswer(Integer answer) {
        return switch (answer) {
            case 1 -> -2;
            case 2 -> -1;
            case 3 -> 0;
            case 4 -> 1;
            case 5 -> 2;
            default -> 0;
        };
    }

    private int calculateContradictions(Map<Long, Integer> answerMap) {
        int contradictions = 0;
        contradictions += isContradictory(answerMap, 1L, 2L);
        contradictions += isContradictory(answerMap, 3L, 4L);
        contradictions += isContradictory(answerMap, 5L, 6L);
        contradictions += isContradictory(answerMap, 7L, 8L);
        contradictions += isContradictory(answerMap, 9L, 10L);
        contradictions += isContradictory(answerMap, 11L, 12L);
        contradictions += isContradictory(answerMap, 13L, 14L);
        contradictions += isContradictory(answerMap, 15L, 16L);
        contradictions += isContradictory(answerMap, 17L, 18L);
        contradictions += isContradictory(answerMap, 21L, 22L);
        contradictions += isContradictory(answerMap, 23L, 24L);
        contradictions += isContradictory(answerMap, 25L, 28L);
        return contradictions;
    }

    private int isContradictory(Map<Long, Integer> answerMap, Long q1, Long q2) {
        Integer a1 = answerMap.get(q1);
        Integer a2 = answerMap.get(q2);

        if (a1 == null || a2 == null) {
            return 0;
        }

        return (a1 >= 4 && a2 >= 4) ? 1 : 0;
    }

    private void clampTraitScores(ScoringContext context) {
        for (Map.Entry<String, Integer> entry : context.getTraitScores().entrySet()) {
            int value = entry.getValue();
            if (value < 0) {
                value = 0;
            }
            if (value > 100) {
                value = 100;
            }
            entry.setValue(value);
        }
    }

    private String generateMbtiType(ScoringContext context) {
        Map<String, Integer> axisScores = context.getAxisScores();

        String ei = axisScores.getOrDefault("EI", 0) >= 0 ? "E" : "I";
        String sn = axisScores.getOrDefault("SN", 0) >= 0 ? "N" : "S";
        String tf = axisScores.getOrDefault("TF", 0) >= 0 ? "F" : "T";
        String jp = axisScores.getOrDefault("JP", 0) >= 0 ? "J" : "P";

        return ei + sn + tf + jp;
    }

    private int calculateConfidence(ScoringContext context) {
        Map<String, Integer> axisScores = context.getAxisScores();

        int ei = Math.min(100, Math.abs(axisScores.getOrDefault("EI", 0)) * 10);
        int sn = Math.min(100, Math.abs(axisScores.getOrDefault("SN", 0)) * 10);
        int tf = Math.min(100, Math.abs(axisScores.getOrDefault("TF", 0)) * 10);
        int jp = Math.min(100, Math.abs(axisScores.getOrDefault("JP", 0)) * 10);

        int average = (ei + sn + tf + jp) / 4;
        int adjusted = average - (context.getContradictionCount() * 5);

        if (adjusted < 20) {
            return 20;
        }
        return Math.min(adjusted, 100);
    }

    private String generateArchetype(ScoringContext context) {
        Map<String, Integer> traits = context.getTraitScores();

        int social = traits.get("Social Energy");
        int cognition = traits.get("Cognitive Style");
        int structure = traits.get("Structure Orientation");
        int emotion = traits.get("Emotional Sensitivity");
        int adaptability = traits.get("Adaptability");
        int consistency = traits.get("Behavioral Consistency");
        int stress = traits.get("Stress Resilience");

        if (cognition >= 65 && emotion >= 60 && social <= 50) {
            return "Reflective Visionary";
        }
        if (structure >= 65 && consistency >= 65 && stress >= 55) {
            return "Resilient Builder";
        }
        if (cognition >= 65 && structure >= 60 && emotion <= 50) {
            return "Strategic Architect";
        }
        if (social >= 60 && emotion >= 65 && adaptability >= 55) {
            return "Interpersonal Navigator";
        }
        if (emotion >= 65 && cognition >= 55 && stress <= 50) {
            return "Idealistic Processor";
        }
        if (adaptability >= 65 && cognition >= 60 && structure <= 55) {
            return "Adaptive Explorer";
        }
        if (consistency >= 60 && structure >= 60 && social <= 55) {
            return "Stabilizing Analyst";
        }
        return "Balanced Integrator";
    }

    private String generateCoreProfile(ScoringContext context, String mbtiType, String expandedMbti, String archetype) {
        Map<String, Integer> traits = context.getTraitScores();
        int social = traits.get("Social Energy");
        int emotion = traits.get("Emotional Sensitivity");
        int cognition = traits.get("Cognitive Style");
        int structure = traits.get("Structure Orientation");

        String socialLine = social < 40
                ? "The center of gravity sits inward: selectivity, privacy, and depth seem more restorative than constant exposure."
                : social > 60
                ? "The profile carries outward voltage: interaction, stimulation, and visible exchange likely sharpen your momentum."
                : "Social energy stays measured rather than extreme, suggesting you can move outward without losing your internal center.";

        String emotionLine = emotion > 60
                ? "Values and emotional texture are not background details here; they actively shape what feels true, safe, and worth pursuing."
                : emotion < 40
                ? "A cooler interpersonal stance shows up, with enough distance to evaluate situations without being easily swept into them."
                : "Emotion is present without flooding the system, which often supports nuance instead of overreaction.";

        String cognitionLine = cognition > 60
                ? "Mentally, the profile tilts toward patterns, meaning, and what is forming beneath the surface."
                : cognition < 40
                ? "Attention appears anchored in what is concrete, usable, and immediately testable."
                : "The mind seems able to alternate between abstraction and practical detail without becoming trapped in either.";

        String structureLine = structure > 60
                ? "Structure matters: clarity, sequencing, and follow-through likely make life feel cleaner and more controllable."
                : structure < 40
                ? "Flexibility matters more than rigid form, leaving room for improvisation when reality shifts."
                : "Neither rigidity nor drift dominates, which often reads as balanced self-management rather than fixed style.";

        return "The strongest current in this report points toward " + mbtiType + " (" + expandedMbti + "), framed through the Cognify archetype "
                + archetype + ". " + socialLine + " " + emotionLine + " " + cognitionLine + " " + structureLine;
    }

    private String generateDecisionPattern(ScoringContext context) {
        Map<String, Integer> traits = context.getTraitScores();
        int decision = traits.get("Decision Style");
        int emotion = traits.get("Emotional Sensitivity");

        if (decision >= 65 && emotion >= 60) {
            return "Choice-making looks deeply value-led. Efficiency still matters, but decisions are unlikely to feel complete unless they also respect people, atmosphere, and emotional consequence.";
        } else if (decision <= 40 && emotion <= 50) {
            return "Judgment leans crisp and unsentimental. Logic, structural fairness, and internal consistency seem to outrank comfort when the stakes rise.";
        } else if (decision >= 55) {
            return "A humane decision style comes through. People-awareness is in the room, yet it does not completely overpower structure or discernment.";
        } else if (decision <= 45) {
            return "Reasoning carries noticeable weight. There is a clear preference to understand the architecture of a problem before emotionally endorsing the answer.";
        } else {
            return "Decision style stays flexible rather than ideological, shifting between heart and analysis according to the demands of the moment.";
        }
    }

    private String generateSocialPattern(ScoringContext context) {
        int social = context.getTraitScores().get("Social Energy");

        if (social >= 70) {
            return "Interaction appears energizing rather than draining. Other people, shared environments, and live exchange may actually amplify your clarity instead of scattering it.";
        } else if (social >= 55) {
            return "There is healthy outward range here. Social participation is accessible, though the quality of contact likely matters more than sheer volume.";
        } else if (social <= 35) {
            return "Solitude looks medicinal, not avoidant. Space, privacy, and selective connection probably restore your thinking more effectively than constant availability.";
        } else {
            return "The social stance feels intentional and selective. Engagement happens when it has meaning, not simply because the room is full.";
        }
    }

    private String generateStressPattern(ScoringContext context) {
        int stress = context.getTraitScores().get("Stress Resilience");

        if (stress <= 35) {
            return "Pressure appears to hit the nervous system quickly. When too many variables pile up, clarity can fragment and self-regulation may feel more effortful than usual.";
        } else if (stress <= 50) {
            return "Stress sensitivity is present but not defining. Core intentions may remain stable even when execution, steadiness, or confidence starts to wobble.";
        } else if (stress >= 70) {
            return "Resilience is one of the cleaner strengths in this report. Even under strain, the profile suggests an ability to hold shape, recover footing, and keep functioning with minimal collapse.";
        } else {
            return "The pressure response looks workable overall. Strain is real, but it does not appear to dominate the system or dismantle coherence too easily.";
        }
    }

    private String generateBehavioralPattern(ScoringContext context) {
        int consistency = context.getTraitScores().get("Behavioral Consistency");
        int structure = context.getTraitScores().get("Structure Orientation");

        if (consistency >= 65 && structure >= 60) {
            return "Execution appears disciplined and dependable. Once a commitment locks in, there is a strong chance it gets carried through with steadiness rather than bursts of intensity alone.";
        } else if (consistency <= 40) {
            return "Behavior seems more state-dependent. Motivation, mood, or context may meaningfully change what gets expressed from one moment to the next.";
        } else {
            return "Patterning is moderately stable. You are neither radically erratic nor rigidly fixed, which often translates into adaptable but mostly reliable follow-through.";
        }
    }

    private String generateCognitivePattern(ScoringContext context) {
        int cognition = context.getTraitScores().get("Cognitive Style");

        if (cognition >= 70) {
            return "Thinking runs strongly abstract. Possibilities, hidden structure, and conceptual depth are likely more compelling than surface facts alone.";
        } else if (cognition >= 55) {
            return "The mind tilts toward implication and pattern. You likely notice what something means before everyone else has finished describing what it is.";
        } else if (cognition <= 35) {
            return "Cognition appears grounded and pragmatic. What works, what is observable, and what can be applied now seem to carry real authority.";
        } else {
            return "The cognitive style is mixed in a useful way, able to move between conceptual framing and concrete execution without losing too much efficiency.";
        }
    }

    private String generateAdaptivePattern(ScoringContext context) {
        int adaptability = context.getTraitScores().get("Adaptability");
        int structure = context.getTraitScores().get("Structure Orientation");

        if (adaptability >= 65 && structure <= 50) {
            return "Change does not appear especially threatening here. The system seems capable of reorienting quickly without needing perfect continuity first.";
        } else if (adaptability >= 60 && structure >= 60) {
            return "Flexibility is present, but it prefers a frame. You can pivot when necessary, especially when the new path still feels intelligible and purposeful.";
        } else if (adaptability <= 40) {
            return "Abrupt change likely creates friction. Time, predictability, and a clear runway to reorganize seem important for protecting your best functioning.";
        } else {
            return "Adaptability sits in the middle band: neither rigid nor highly fluid, but responsive when change feels meaningful and manageable.";
        }
    }

    private String generateContradictionAnalysis(int contradictionCount) {
        if (contradictionCount == 0) {
            return "The response pattern is notably coherent. That usually signals a steadier self-concept and clearer preference architecture across situations.";
        } else if (contradictionCount <= 2) {
            return "A small amount of internal tension appears, which often reflects nuance rather than unreliability. Different contexts may be drawing out different sides of the same person.";
        } else if (contradictionCount <= 4) {
            return "The profile contains visible internal cross-currents. Behavior may shift materially by context, pressure, or relationship dynamic, producing a more layered presentation than a single label can capture.";
        } else {
            return "Contradiction is strong enough to matter. The report suggests competing tendencies are active at the same time, which can make identity feel situational, fragmented, or harder to summarize cleanly.";
        }
    }

    private String generateMasterSummary(String mbtiType,
                                         String expandedMbti,
                                         String archetype,
                                         int confidenceScore,
                                         int contradictionCount,
                                         String coreProfile,
                                         String adaptivePattern) {
        return "Closest alignment: " + mbtiType + " (" + expandedMbti + "). "
                + "Cognify archetype: " + archetype + ". "
                + "Signal confidence: " + confidenceScore + "/100 with "
                + contradictionCount + " contradiction marker"
                + (contradictionCount == 1 ? "" : "s") + ". "
                + coreProfile + " " + adaptivePattern;
    }
}
