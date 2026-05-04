package com.cognify.service;

import com.cognify.model.dto.SimulationRequest;
import com.cognify.model.dto.SimulationResponse;
import com.cognify.model.entity.AssessmentAttempt;
import com.cognify.model.entity.TraitScore;
import com.cognify.repository.AssessmentAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SimulationService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    public SimulationService(AssessmentAttemptRepository assessmentAttemptRepository) {
        this.assessmentAttemptRepository = assessmentAttemptRepository;
    }

    public SimulationResponse analyzeScenario(SimulationRequest request) {
        validateRequest(request);

        AssessmentAttempt attempt = assessmentAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new RuntimeException("Assessment attempt not found."));

        if (!attempt.getUser().getId().equals(request.getUserId())) {
            throw new RuntimeException("That attempt does not belong to the current user.");
        }

        Map<String, Integer> traits = toTraitMap(attempt.getTraitScores());
        SimulationContext context = new SimulationContext(
                attempt,
                traits,
                normalizeCategory(request.getScenarioCategory()),
                normalizeScenarioText(request.getScenarioText())
        );

        SimulationResponse response = new SimulationResponse();
        response.setScenarioTitle(buildScenarioTitle(context));
        response.setPersonalitySnapshot(buildPersonalitySnapshot(context));
        response.setLikelyDefaultReaction(buildDefaultReaction(context));
        response.setHiddenBlindSpot(buildHiddenBlindSpot(context));
        response.setWhatYourBrainIsOptimizingFor(buildOptimization(context));
        response.setBetterResponseStrategy(buildBetterStrategy(context));
        response.setThreeStepActionPlan(buildThreeStepPlan(context));
        response.setSevenDayGrowthPlan(buildSevenDayPlan(context));
        response.setReflectionPrompt(buildReflectionPrompt(context));
        return response;
    }

    private void validateRequest(SimulationRequest request) {
        if (request == null) {
            throw new RuntimeException("Simulation request is required.");
        }
        if (request.getUserId() == null) {
            throw new RuntimeException("A valid user is required for simulation mode.");
        }
        if (request.getAttemptId() == null) {
            throw new RuntimeException("An assessment attempt is required for simulation mode.");
        }
        if (request.getScenarioCategory() == null || request.getScenarioCategory().isBlank()) {
            throw new RuntimeException("Choose a simulation category.");
        }

        String normalizedCategory = normalizeCategory(request.getScenarioCategory());
        if ("Custom Scenario".equals(normalizedCategory)
                && (request.getScenarioText() == null || request.getScenarioText().isBlank())) {
            throw new RuntimeException("Describe the custom situation you want Cognify to simulate.");
        }
    }

    private Map<String, Integer> toTraitMap(List<TraitScore> traitScores) {
        Map<String, Integer> traits = new LinkedHashMap<>();
        traits.put("Social Energy", 50);
        traits.put("Emotional Sensitivity", 50);
        traits.put("Decision Style", 50);
        traits.put("Structure Orientation", 50);
        traits.put("Cognitive Style", 50);
        traits.put("Stress Resilience", 50);
        traits.put("Adaptability", 50);
        traits.put("Behavioral Consistency", 50);

        for (TraitScore traitScore : traitScores) {
            traits.put(traitScore.getTraitName(), traitScore.getScore());
        }

        return traits;
    }

    private String normalizeCategory(String rawCategory) {
        if (rawCategory == null || rawCategory.isBlank()) {
            return "Custom Scenario";
        }

        String normalized = rawCategory.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "group project conflict" -> "Group Project Conflict";
            case "viva / interview pressure", "viva/interview pressure", "viva interview pressure" -> "Viva / Interview Pressure";
            case "career confusion" -> "Career Confusion";
            case "procrastination" -> "Procrastination";
            case "friendship conflict" -> "Friendship Conflict";
            case "leadership situation" -> "Leadership Situation";
            case "custom scenario" -> "Custom Scenario";
            default -> "Custom Scenario";
        };
    }

    private String normalizeScenarioText(String rawText) {
        if (rawText == null) {
            return "";
        }
        return rawText.trim();
    }

    private String buildScenarioTitle(SimulationContext context) {
        if (!context.scenarioText().isBlank()) {
            return switch (context.category()) {
                case "Custom Scenario" -> "Custom Simulation: " + trimSentence(context.scenarioText(), 58);
                default -> context.category() + ": " + trimSentence(context.scenarioText(), 52);
            };
        }

        return switch (context.category()) {
            case "Group Project Conflict" -> "Simulation: Team Friction Under Deadline";
            case "Viva / Interview Pressure" -> "Simulation: High-Pressure Evaluation";
            case "Career Confusion" -> "Simulation: Fork-in-the-Road Career Decision";
            case "Procrastination" -> "Simulation: Motivation Drift Before a Key Deadline";
            case "Friendship Conflict" -> "Simulation: Emotional Misalignment With a Close Friend";
            case "Leadership Situation" -> "Simulation: Leading Without Losing Yourself";
            default -> "Simulation: Personalized Real-Life Scenario";
        };
    }

    private String buildPersonalitySnapshot(SimulationContext context) {
        String mbti = fallback(context.attempt().getMbtiType(), "Unknown");
        String archetype = fallback(context.attempt().getArchetype(), "Balanced Integrator");
        String confidence = context.attempt().getConfidenceScore() != null
                ? context.attempt().getConfidenceScore() + "/100"
                : "Unavailable";

        return "This simulation is anchored in your saved " + mbti + " profile and the Cognify archetype "
                + archetype + ". Your current signal suggests " + socialDescriptor(context) + ", "
                + decisionDescriptor(context) + ", and " + stressDescriptor(context) + ". Confidence in this profile sits at "
                + confidence + ", so the likely pattern here is not random mood alone but a repeatable behavioral style. "
                + fallback(context.attempt().getCoreProfile(), "Your report emphasizes a blended but identifiable internal pattern.");
    }

    private String buildDefaultReaction(SimulationContext context) {
        String categoryLead = switch (context.category()) {
            case "Group Project Conflict" -> "In a tense group setting, you are likely to scan for who is being unreasonable, what is inefficient, and where the atmosphere is starting to destabilize.";
            case "Viva / Interview Pressure" -> "Under direct evaluation, your first reaction is likely to be a fast internal check for whether you sound coherent, competent, and in control.";
            case "Career Confusion" -> "When too many future paths stay open, your mind is likely to cycle between possibility, pressure, and the need for a signal that feels trustworthy.";
            case "Procrastination" -> "When momentum slips, your default pattern is likely to start with internal negotiation rather than immediate action.";
            case "Friendship Conflict" -> "In personal conflict, you are likely to track both the emotional meaning of what happened and the long-term implication for trust.";
            case "Leadership Situation" -> "When you are suddenly placed in charge, your first instinct is likely to be organizing the emotional and structural field at the same time.";
            default -> "In an ambiguous real-life scenario, your likely first move is to read the room, locate the pressure point, and respond in the style your assessment most strongly predicts.";
        };

        return categoryLead + " " + socialOverlay(context) + " " + structureOverlay(context) + " " + fallback(
                context.attempt().getBehavioralPattern(),
                "Your saved report suggests that this reaction will follow your broader consistency pattern rather than appear out of nowhere."
        );
    }

    private String buildHiddenBlindSpot(SimulationContext context) {
        if (trait(context, "Stress Resilience") < 42 && trait(context, "Behavioral Consistency") < 48) {
            return "Your blind spot in this scenario is not lack of intelligence but state-dependence. Once stress rises, you may mistake temporary overwhelm for a permanent truth about the situation, then act from urgency instead of signal.";
        }
        if (trait(context, "Emotional Sensitivity") > 68 && trait(context, "Decision Style") > 58) {
            return "Your blind spot is likely over-identifying with tone. You may correctly sense emotional undercurrents but still give them more steering power than the actual objective problem deserves.";
        }
        if (trait(context, "Structure Orientation") > 68 && trait(context, "Adaptability") < 45) {
            return "Your blind spot is rigidity disguised as responsibility. Because structure feels safe and productive, you may double down on control right when flexibility would create the better outcome.";
        }
        if (trait(context, "Cognitive Style") > 66 && trait(context, "Behavioral Consistency") < 52) {
            return "Your blind spot is elegant overthinking. You may generate a sharp interpretation of the problem without moving quickly enough into concrete behavior that changes the outcome.";
        }
        if (trait(context, "Social Energy") < 42) {
            return "Your blind spot is silent withdrawal. You may believe you are buying clarity by stepping back, while other people experience the same move as disengagement or lack of ownership.";
        }
        return "Your blind spot here is subtle self-protection: part of you may optimize for preserving identity, harmony, or certainty before fully testing what the situation is actually asking from you.";
    }

    private String buildOptimization(SimulationContext context) {
        List<String> priorities = new ArrayList<>();

        if (trait(context, "Stress Resilience") < 45) {
            priorities.add("reducing internal overload");
        } else {
            priorities.add("maintaining control under pressure");
        }

        if (trait(context, "Emotional Sensitivity") >= 60) {
            priorities.add("protecting relational tone");
        } else {
            priorities.add("keeping the situation logically clean");
        }

        if (trait(context, "Structure Orientation") >= 60) {
            priorities.add("creating a clear sequence");
        } else if (trait(context, "Adaptability") >= 60) {
            priorities.add("keeping room to pivot");
        } else {
            priorities.add("avoiding premature lock-in");
        }

        return "In this simulation, your brain is primarily optimizing for " + String.join(", ", priorities)
                + ". That is why your first impulse may feel emotionally correct even before it has been fully pressure-tested against the external reality of the situation. "
                + fallback(context.attempt().getDecisionPattern(), "Your saved decision profile suggests you will balance meaning and logic in a recognizable way.");
    }

    private String buildBetterStrategy(SimulationContext context) {
        String categoryAnchor = switch (context.category()) {
            case "Group Project Conflict" -> "Treat the conflict as a coordination problem first and a personality problem second.";
            case "Viva / Interview Pressure" -> "Slow the interaction down by giving your mind a structure it can trust: point, evidence, example, close.";
            case "Career Confusion" -> "Replace abstract life pressure with bounded experiments that create signal.";
            case "Procrastination" -> "Stop negotiating with the whole mountain and build one low-friction starting lane.";
            case "Friendship Conflict" -> "Lead with clean honesty instead of mental replay. Clarify impact without dramatizing motive.";
            case "Leadership Situation" -> "Anchor the room with a few clear decisions, then invite input so leadership feels steady rather than controlling.";
            default -> "Convert the scenario from a vague emotional field into a sequence you can observe, name, and influence.";
        };

        return categoryAnchor + " " + stressSupport(context) + " " + adaptabilitySupport(context)
                + " The best version of your profile in this moment is not a different personality. It is your same personality with more sequencing, less fusion, and faster reality-checking.";
    }

    private List<String> buildThreeStepPlan(SimulationContext context) {
        List<String> steps = new ArrayList<>();
        switch (context.category()) {
            case "Group Project Conflict" -> {
                steps.add("Name the real issue in one sentence: missed ownership, unclear expectations, uneven effort, or tone damage.");
                steps.add("Call for a short reset conversation with roles, deadlines, and one concrete accountability checkpoint.");
                steps.add("After the conversation, send a clean written summary so the group remembers structure instead of just emotion.");
            }
            case "Viva / Interview Pressure" -> {
                steps.add("Before entering, choose three anchor stories or examples that prove competence under pressure.");
                steps.add("During difficult questions, pause for one beat and answer in a simple structure instead of chasing perfection.");
                steps.add("Afterward, record where your mind tightened so you can rehearse the pressure points rather than vaguely worrying about them.");
            }
            case "Career Confusion" -> {
                steps.add("Reduce the decision to two or three live options instead of carrying every possible future at once.");
                steps.add("Score each option against energy, values, lifestyle fit, and practical traction using your current profile rather than social comparison.");
                steps.add("Run one small test this week such as a conversation, portfolio draft, or short project to replace fog with evidence.");
            }
            case "Procrastination" -> {
                steps.add("Define the smallest visible starting action you can do in under fifteen minutes.");
                steps.add("Remove one source of friction from the environment before you begin.");
                steps.add("Finish the first block before evaluating your mood, because momentum is the real target.");
            }
            case "Friendship Conflict" -> {
                steps.add("Clarify what actually hurt you: exclusion, inconsistency, disrespect, or misunderstanding.");
                steps.add("Start the conversation with observation and impact, not accusation.");
                steps.add("Ask for one future-facing repair behavior so the friendship has a practical next step.");
            }
            case "Leadership Situation" -> {
                steps.add("Stabilize the room by stating the mission, the time horizon, and the immediate next priority.");
                steps.add("Assign ownership based on strengths instead of trying to carry everything yourself.");
                steps.add("Close the loop with a brief check-in rhythm so leadership becomes a system, not a burst of effort.");
            }
            default -> {
                steps.add("Write the scenario as facts first so your mind is working with reality instead of raw intensity.");
                steps.add("Decide what outcome matters most in the next seventy-two hours.");
                steps.add("Take one visible action that moves the situation forward before asking yourself to feel fully ready.");
            }
        }
        return steps;
    }

    private List<String> buildSevenDayPlan(SimulationContext context) {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Re-read your summary and core profile, then write one sentence about how this scenario triggers your usual pattern.");
        plan.add("Day 2: Track one moment where you felt reactive and label what you were trying to protect: control, harmony, certainty, image, or energy.");
        plan.add("Day 3: Practice one low-stakes version of the better response strategy in a smaller conversation or task.");
        plan.add("Day 4: Build a structure that supports your weakest pressure trait, such as accountability, recovery time, or decision framing.");
        plan.add("Day 5: Use one direct action that interrupts your default loop and creates external evidence.");
        plan.add("Day 6: Review whether the scenario changed more because of your thoughts, your environment, or your communication.");
        plan.add("Day 7: Capture one new rule for yourself that you want future-you to remember in similar situations.");
        return plan;
    }

    private String buildReflectionPrompt(SimulationContext context) {
        return "When this kind of situation appears, what part of you reacts first: the part protecting identity, the part protecting stability, or the part trying to prove competence, and how would the wiser version of your "
                + fallback(context.attempt().getArchetype(), "Cognify profile")
                + " respond one beat later?";
    }

    private String socialDescriptor(SimulationContext context) {
        int social = trait(context, "Social Energy");
        if (social >= 65) {
            return "you tend to process pressure externally and gain momentum through visible engagement";
        }
        if (social <= 40) {
            return "you tend to process pressure inwardly and protect clarity through selective space";
        }
        return "you tend to stay balanced between outward engagement and inward recalibration";
    }

    private String decisionDescriptor(SimulationContext context) {
        int decision = trait(context, "Decision Style");
        if (decision >= 60) {
            return "your decisions naturally stay sensitive to human impact";
        }
        if (decision <= 40) {
            return "your decisions naturally tighten around logic and clean structure";
        }
        return "your decisions usually blend emotional intelligence with practical reasoning";
    }

    private String stressDescriptor(SimulationContext context) {
        int stress = trait(context, "Stress Resilience");
        if (stress >= 65) {
            return "you usually keep form even when the situation heats up";
        }
        if (stress <= 40) {
            return "pressure can narrow your range faster than people around you may realize";
        }
        return "your stress response is workable but still sensitive to overload";
    }

    private String socialOverlay(SimulationContext context) {
        int social = trait(context, "Social Energy");
        if (social >= 65) {
            return "You may speak early, test ideas in real time, and try to shift the energy by engaging it directly.";
        }
        if (social <= 40) {
            return "You may initially pull back, process privately, and want a few internal minutes before saying what you really think.";
        }
        return "You are likely to stay selective, engaging when it feels useful rather than reacting just to fill the air.";
    }

    private String structureOverlay(SimulationContext context) {
        int structure = trait(context, "Structure Orientation");
        int adaptability = trait(context, "Adaptability");
        if (structure >= 65) {
            return "Because structure calms your system, you will probably start building order, priorities, or a decision frame almost immediately.";
        }
        if (adaptability >= 65) {
            return "Because flexibility is one of your strengths, you may improvise effectively, though the risk is leaving key expectations too implicit.";
        }
        return "You are likely to search for a usable middle path, enough order to move forward without locking yourself too early into one angle.";
    }

    private String stressSupport(SimulationContext context) {
        int stress = trait(context, "Stress Resilience");
        if (stress <= 40) {
            return "The key adjustment is reducing nervous-system noise before trying to make a brilliant decision.";
        }
        if (stress >= 65) {
            return "Your resilience lets you stay functional, but do not let that strength trick you into carrying everything silently.";
        }
        return "A small regulation move before action will probably improve the quality of the response more than more thinking alone.";
    }

    private String adaptabilitySupport(SimulationContext context) {
        int adaptability = trait(context, "Adaptability");
        int consistency = trait(context, "Behavioral Consistency");
        if (adaptability <= 40) {
            return "Leave yourself one planned pivot so the strategy does not collapse if reality changes.";
        }
        if (consistency <= 45) {
            return "Keep the strategy behaviorally small and visible so execution does not depend on a perfect emotional state.";
        }
        return "Aim for a response that is concrete enough to execute and flexible enough to survive real-world friction.";
    }

    private int trait(SimulationContext context, String name) {
        return context.traits().getOrDefault(name, 50);
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String trimSentence(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3).trim() + "...";
    }

    private record SimulationContext(
            AssessmentAttempt attempt,
            Map<String, Integer> traits,
            String category,
            String scenarioText
    ) {
    }
}
