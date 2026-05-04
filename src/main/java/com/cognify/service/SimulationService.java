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
        return context.category();
    }

    private String buildPersonalitySnapshot(SimulationContext context) {
        String mbti = fallback(context.attempt().getMbtiType(), "Unknown");
        String archetype = fallback(context.attempt().getArchetype(), "Balanced Integrator");

        return "Profile Anchor: " + mbti + " · " + archetype + "\n"
                + "Primary pattern: " + primaryPattern(context) + "\n"
                + "Pressure tendency: " + pressureTendency(context) + "\n"
                + "Useful strength: " + usefulStrength(context) + "\n"
                + "Risk to watch: " + riskToWatch(context);
    }

    private String buildDefaultReaction(SimulationContext context) {
        return switch (context.category()) {
            case "Group Project Conflict" -> trait(context, "Social Energy") >= 60
                    ? "When contribution turns uneven under deadline pressure, your profile leans toward stepping in quickly to protect the grade and reset the group live. You notice weak ownership fast. The risk is moving before accountability becomes visible in writing."
                    : "When contribution turns uneven under deadline pressure, your profile leans toward reading the room first and deciding how to intervene without unnecessary drama. You notice weak ownership fast. The risk is waiting for people to self-correct after the situation already needs written accountability.";
            case "Viva / Interview Pressure" -> "When a panel pushes with follow-up questions, your first reaction is usually an internal competence check: do I sound clear, do I remember the simple things, do I still seem like I understand my own project logic? If stress spikes, you may briefly freeze or sound flatter than you feel. Your best recovery is to rebuild the answer from the core idea instead of trying to sound impressive immediately.";
            case "Career Confusion" -> "When too many paths stay open, your mind usually compares them in loops. You want the right answer before moving, but you also feel the cost of choosing wrong and closing doors. That is where decision paralysis starts: every option carries identity weight.";
            case "Procrastination" -> "When the task matters enough to feel self-defining, your default reaction is usually avoidance dressed up as delay logic. You tell yourself you will start when the mood is cleaner, drift into a dopamine escape, feel guilt at night, and wake up with the task feeling heavier. The real choke point is starting friction, not awareness.";
            case "Friendship Conflict" -> trait(context, "Emotional Sensitivity") >= 65
                    ? "When replies slow down or the tone suddenly feels off, your profile tends to decode emotional ambiguity quickly. You notice delayed replies, access shifts, and small tone changes early. The risk is reading the whole friendship through partial data before direct communication happens."
                    : "When replies slow down or the tone suddenly feels off, your profile tends to hold the tension internally while deciding whether clarity is worth the risk. You notice the ambiguity early. The risk is waiting so long that overthinking replaces actual conversation.";
            case "Leadership Situation" -> trait(context, "Structure Orientation") >= 65
                    ? "When you need to take charge, your profile leans toward creating structure quickly. You start clarifying roles, next decisions, and who owns what. The risk is carrying so much decision ownership that delegation becomes thinner than it should be."
                    : "When you need to take charge, your profile leans toward stabilizing the room before making the next move. You track role clarity and emotional control at the same time. The risk is holding the leadership energy too carefully and delaying a needed decision.";
            default -> "In the situation you described, your first move centers on this pressure point: " + customScenarioFocus(context) + ". Your profile pushes you to regain clarity, stability, or control exactly where the scenario feels most loaded.";
        };
    }

    private String buildHiddenBlindSpot(SimulationContext context) {
        return switch (context.category()) {
            case "Group Project Conflict" -> {
                if (trait(context, "Structure Orientation") > 68) {
                    yield "Your blind spot is assuming that once ownership is clarified, the problem is solved. In group work, people can agree politely and still fail quietly, so written accountability matters more than verbal reassurance.";
                }
                if (trait(context, "Emotional Sensitivity") > 68) {
                    yield "Your blind spot is over-managing tone. You may try so hard to avoid drama that you become softer than the situation requires, which lets uneven contribution continue.";
                }
                yield "Your blind spot is waiting too long for the team to self-correct. Deadline pressure punishes vague optimism.";
            }
            case "Viva / Interview Pressure" -> {
                if (trait(context, "Stress Resilience") < 42) {
                    yield "Your blind spot is reading a temporary freeze as proof that you do not know your material. In reality, the drop is often retrieval pressure, not understanding failure.";
                }
                yield "Your blind spot is trying to sound perfect instead of sounding clear. That is what makes answers feel memorized or less natural than they need to.";
            }
            case "Career Confusion" -> {
                if (trait(context, "Cognitive Style") > 65) {
                    yield "Your blind spot is turning career choice into a philosophy problem. You may compare paths so deeply that no option feels concrete enough to test.";
                }
                yield "Your blind spot is believing the right decision must appear before movement begins. That belief quietly feeds paralysis.";
            }
            case "Procrastination" -> {
                if (trait(context, "Behavioral Consistency") < 48) {
                    yield "Your blind spot is expecting motivation to arrive before action. That keeps the avoidance loop emotionally convincing even when you already know the first step.";
                }
                yield "Your blind spot is underestimating how much starting friction, not task difficulty, is driving the delay.";
            }
            case "Friendship Conflict" -> {
                if (trait(context, "Emotional Sensitivity") > 68) {
                    yield "Your blind spot is treating tone as decisive evidence before you have enough direct communication. Delayed replies and emotional ambiguity can make your mind fill in the blanks too aggressively.";
                }
                yield "Your blind spot is choosing either silence or intensity when what usually works better is direct but low-pressure communication.";
            }
            case "Leadership Situation" -> {
                if (trait(context, "Adaptability") < 45) {
                    yield "Your blind spot is mistaking control for leadership. If roles are clear but people feel over-managed, execution may still drop.";
                }
                yield "Your blind spot is carrying too much decision ownership alone instead of delegating clearly enough for the team to grow around you.";
            }
            default -> "Your blind spot is likely hidden inside the exact pressure you described: " + customScenarioBlindSpot(context);
        };
    }

    private String buildOptimization(SimulationContext context) {
        return switch (context.category()) {
            case "Group Project Conflict" -> "Your brain is optimizing for protecting output quality without letting the group become dramatic or chaotic. That is why you may care so much about the grade, role clarity, and whether there is a written trail of accountability.";
            case "Viva / Interview Pressure" -> "Your brain is optimizing for staying credible under scrutiny. It wants to recover quickly, explain project logic clearly, and avoid sounding either blank or rehearsed.";
            case "Career Confusion" -> "Your brain is optimizing for not choosing wrong too early. It is trying to preserve future optionality, reduce regret, and find a path that feels both meaningful and livable.";
            case "Procrastination" -> "Your brain is optimizing for short-term relief from friction. That is why dopamine escape can feel easier than starting, even when guilt keeps building in the background.";
            case "Friendship Conflict" -> "Your brain is optimizing for preserving the relationship without misstepping. It wants clarity, but it also wants to avoid saying something that could reduce emotional access further.";
            case "Leadership Situation" -> "Your brain is optimizing for stable execution under your name. It wants role clarity, emotional control, and enough decision ownership that the situation does not drift.";
            default -> "Your brain is optimizing for relief from the exact pressure inside your scenario: " + customScenarioFocus(context) + ". That is why your first impulse may feel urgent before it is fully tested.";
        };
    }

    private String buildBetterStrategy(SimulationContext context) {
        String categoryAnchor = switch (context.category()) {
            case "Group Project Conflict" -> "Treat the problem as an execution issue. Move the group from vague frustration to explicit ownership, dates, and written follow-up.";
            case "Viva / Interview Pressure" -> "Answer for clarity, not performance. When a question scrambles you, slow down, restate the logic, and rebuild the answer from first principles.";
            case "Career Confusion" -> "Stop asking your brain for one perfect choice. Compare fewer paths and turn them into small experiments that produce real signal.";
            case "Procrastination" -> "Shrink the target until starting feels almost too small to resist. Your job is to break the avoidance loop, not finish the whole mountain emotionally.";
            case "Friendship Conflict" -> "Use direct but low-pressure communication. Say what changed, what you noticed, and what you want to understand without flooding the moment.";
            case "Leadership Situation" -> "Lead with role clarity and calm pacing. Delegate visibly, own the decisions that are yours, and avoid confusing intensity with leadership.";
            default -> "Translate your scenario into one clear decision, one visible conversation, and one immediate next step.";
        };

        return categoryAnchor + " " + stressSupport(context) + " " + adaptabilitySupport(context);
    }

    private List<String> buildThreeStepPlan(SimulationContext context) {
        List<String> steps = new ArrayList<>();
        switch (context.category()) {
            case "Group Project Conflict" -> {
                steps.add("Name the issue clearly to yourself: uneven contribution, missed ownership, unclear deadline pressure, or people avoiding the hard conversation.");
                steps.add("Run a short reset with task ownership, explicit deadlines, and no vague promises about helping more later.");
                steps.add("Send written accountability right after the conversation so grade protection does not depend on memory or tone.");
            }
            case "Viva / Interview Pressure" -> {
                steps.add("Choose three anchor explanations that prove you understand the project logic, not just the final output.");
                steps.add("If you freeze or forget something simple, pause, recover, and restart from the core idea instead of panicking about the stumble.");
                steps.add("Practice sounding clear rather than memorized by answering one difficult question in your own words after the session.");
            }
            case "Career Confusion" -> {
                steps.add("Cut the options down to two or three live paths so your brain is not carrying every possible future at once.");
                steps.add("Compare each path against energy, practical fit, and the kind of daily life it creates, not just prestige or fear.");
                steps.add("Run one small experiment this week so decision paralysis gets replaced with real evidence.");
            }
            case "Procrastination" -> {
                steps.add("Define the tiniest first action that breaks starting friction in under ten minutes.");
                steps.add("Remove the easiest dopamine escape before you begin so avoidance has fewer hiding places.");
                steps.add("Work long enough to break the guilt loop before asking whether you feel motivated.");
            }
            case "Friendship Conflict" -> {
                steps.add("Decide what is actually hurting: delayed replies, emotional ambiguity, a tone shift, or fear of losing access.");
                steps.add("Send a direct but low-pressure message that names what changed without cornering the other person.");
                steps.add("Ask for clarity about the friendship instead of trying to decode everything from tone alone.");
            }
            case "Leadership Situation" -> {
                steps.add("State the mission, time horizon, and next decision so the room feels led quickly.");
                steps.add("Delegate by role clarity, not by hope. Everyone should know what they own and what they are accountable for.");
                steps.add("Hold emotional control while still owning the hard decisions that come with taking charge.");
            }
            default -> {
                steps.add("Write your scenario in one factual sentence using your own words: " + trimSentence(context.scenarioText(), 110));
                steps.add("Pick the one outcome that matters most in the next few days so the situation stops feeling shapeless.");
                steps.add("Take one direct visible action that responds to the exact pressure point you described.");
            }
        }
        return steps;
    }

    private List<String> buildSevenDayPlan(SimulationContext context) {
        return switch (context.category()) {
            case "Group Project Conflict" -> buildGroupProjectWeek();
            case "Viva / Interview Pressure" -> buildInterviewWeek();
            case "Career Confusion" -> buildCareerWeek();
            case "Procrastination" -> buildProcrastinationWeek();
            case "Friendship Conflict" -> buildFriendshipWeek();
            case "Leadership Situation" -> buildLeadershipWeek();
            default -> buildCustomWeek(context);
        };
    }

    private String buildReflectionPrompt(SimulationContext context) {
        return switch (context.category()) {
            case "Group Project Conflict" -> "When teamwork gets uneven, do you protect the relationship first, the grade first, or your own peace first, and what would a calmer version of you do to create accountability sooner?";
            case "Viva / Interview Pressure" -> "When you start freezing under questions, what exactly are you afraid the panel will conclude about you, and how can you return to explaining the logic instead of performing confidence?";
            case "Career Confusion" -> "Are you stuck because the options are truly equal, or because choosing one path means grieving the others?";
            case "Procrastination" -> "What are you actually avoiding at the moment of delay: effort, uncertainty, imperfection, or the feeling of being measured by the result?";
            case "Friendship Conflict" -> "In friendship tension, what do you usually fear more: sounding needy, sounding confrontational, or hearing an answer you may not like?";
            case "Leadership Situation" -> "When you take charge, what is harder for you: delegating trust, holding emotional steadiness, or accepting that your name will sit on the decision?";
            default -> "In the exact scenario you described, what are you trying hardest to protect, and what one clearer action would respect that need without letting it run the whole situation?";
        };
    }

    private String primaryPattern(SimulationContext context) {
        int social = trait(context, "Social Energy");
        int structure = trait(context, "Structure Orientation");
        int decision = trait(context, "Decision Style");

        if (social <= 40 && structure >= 60) {
            return "You usually process first, then act once you have a cleaner internal map.";
        }
        if (social >= 60 && decision >= 55) {
            return "You tend to move toward people quickly while tracking both outcome and human impact.";
        }
        if (structure >= 65) {
            return "You work best when ambiguity gets converted into sequence, ownership, and motion.";
        }
        return "You tend to balance reading the situation with finding the next practical move.";
    }

    private String pressureTendency(SimulationContext context) {
        int stress = trait(context, "Stress Resilience");
        int cognition = trait(context, "Cognitive Style");

        if (stress <= 40 && cognition >= 60) {
            return "Under pressure, your mind can get fast, sharp, and crowded at the same time.";
        }
        if (stress <= 40) {
            return "Under pressure, you may narrow quickly and react from overload before clarity.";
        }
        if (stress >= 65) {
            return "Under pressure, you usually stay composed enough to keep thinking while others destabilize.";
        }
        return "Under pressure, you stay workable, but too much ambiguity can still slow your best judgment.";
    }

    private String usefulStrength(SimulationContext context) {
        if (trait(context, "Adaptability") >= 65) {
            return "You can pivot faster than most once the real problem becomes visible.";
        }
        if (trait(context, "Structure Orientation") >= 65) {
            return "You can create order and role clarity when a situation starts drifting.";
        }
        if (trait(context, "Emotional Sensitivity") >= 65) {
            return "You notice emotional tone early, which helps you catch problems before they become obvious.";
        }
        if (trait(context, "Stress Resilience") >= 65) {
            return "You can keep functioning when the room gets tense or evaluative.";
        }
        return "You usually retain enough self-awareness to notice your own pattern while it is happening.";
    }

    private String riskToWatch(SimulationContext context) {
        if (trait(context, "Behavioral Consistency") <= 45) {
            return "Execution can become too dependent on your current state instead of the real priority.";
        }
        if (trait(context, "Emotional Sensitivity") >= 68) {
            return "You may let tone carry more meaning than the evidence fully supports.";
        }
        if (trait(context, "Structure Orientation") >= 68 && trait(context, "Adaptability") <= 45) {
            return "You may tighten control when the moment actually needs more flexibility.";
        }
        return "You may protect certainty, harmony, or self-image before fully testing the real demand of the moment.";
    }

    private String stressSupport(SimulationContext context) {
        int stress = trait(context, "Stress Resilience");
        if (stress <= 40) {
            return "Your first win is regulation, because once your system calms down your thinking becomes more trustworthy.";
        }
        if (stress >= 65) {
            return "You can stay functional under pressure, but do not let that strength turn into silent over-carrying.";
        }
        return "A small reset before action will probably improve the outcome more than another round of overthinking.";
    }

    private String adaptabilitySupport(SimulationContext context) {
        int adaptability = trait(context, "Adaptability");
        int consistency = trait(context, "Behavioral Consistency");
        if (adaptability <= 40) {
            return "Give yourself one planned pivot so the strategy does not collapse the moment reality changes.";
        }
        if (consistency <= 45) {
            return "Keep the next move small and visible so execution does not depend on the perfect mood.";
        }
        return "Aim for a response that is specific enough to execute and flexible enough to survive real-world friction.";
    }

    private List<String> buildGroupProjectWeek() {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: List the project tasks, current owners, and where contribution is uneven.");
        plan.add("Day 2: Send one clean accountability message with deadlines and explicit ownership.");
        plan.add("Day 3: Track whether teammates changed behavior or only sounded cooperative.");
        plan.add("Day 4: Tighten the plan around the riskiest unfinished deliverable.");
        plan.add("Day 5: Document progress in writing so grade protection is not based on memory.");
        plan.add("Day 6: Practice one firmer sentence that avoids drama but does not avoid responsibility.");
        plan.add("Day 7: Review what kind of teammate behavior triggers you fastest and how you want to handle it next time.");
        return plan;
    }

    private List<String> buildInterviewWeek() {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Write your project logic in plain language as if explaining it to a smart stranger.");
        plan.add("Day 2: Rehearse three answers without memorizing exact wording.");
        plan.add("Day 3: Practice recovering after a freeze by pausing and restarting from the core idea.");
        plan.add("Day 4: Answer one intentionally difficult follow-up question out loud.");
        plan.add("Day 5: Record yourself once and remove phrases that make you sound memorized.");
        plan.add("Day 6: Rehearse with mild pressure so forgetting simple things stops feeling catastrophic.");
        plan.add("Day 7: Write the exact recovery line you want to use when a question catches you off guard.");
        return plan;
    }

    private List<String> buildCareerWeek() {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Reduce your options to two or three serious paths.");
        plan.add("Day 2: Write what each path gives you and what each path costs you.");
        plan.add("Day 3: Notice whether fear of choosing wrong is louder than genuine attraction to any option.");
        plan.add("Day 4: Run one small experiment such as an informational call, project sample, or portfolio step.");
        plan.add("Day 5: Compare the paths again using real signal, not just imagination.");
        plan.add("Day 6: Remove one option if it is only alive because of guilt or comparison.");
        plan.add("Day 7: Decide the next one-month direction instead of demanding a permanent life answer.");
        return plan;
    }

    private List<String> buildProcrastinationWeek() {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Identify the first point of friction where avoidance usually starts.");
        plan.add("Day 2: Make the first action tiny enough that starting feels boring, not dramatic.");
        plan.add("Day 3: Notice your favorite dopamine escape and block it for one work sprint.");
        plan.add("Day 4: Start before you feel ready and measure what happens to the guilt loop.");
        plan.add("Day 5: Repeat the same start ritual so action depends less on mood.");
        plan.add("Day 6: Track whether the task was hard or whether beginning was the real problem.");
        plan.add("Day 7: Build one personal rule for future deadlines that protects momentum earlier.");
        return plan;
    }

    private List<String> buildFriendshipWeek() {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Name what changed without interpreting motive yet.");
        plan.add("Day 2: Separate delayed replies and tone shifts from the story your mind is building around them.");
        plan.add("Day 3: Draft a direct but low-pressure message.");
        plan.add("Day 4: Send it instead of continuing silent overthinking.");
        plan.add("Day 5: Notice whether clarity helped more than replaying the ambiguity.");
        plan.add("Day 6: Decide what kind of friendship access and communication you actually need.");
        plan.add("Day 7: Reflect on whether your usual move is silence, intensity, or decoding, and what healthier balance looks like.");
        return plan;
    }

    private List<String> buildLeadershipWeek() {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Clarify the mission, deadline, and what success looks like.");
        plan.add("Day 2: Assign ownership so each person knows their role clearly.");
        plan.add("Day 3: Notice where you are over-holding decisions that could be delegated.");
        plan.add("Day 4: Practice one calm leadership message that is firm without sounding harsh.");
        plan.add("Day 5: Run a short check-in focused on blockers, not blame.");
        plan.add("Day 6: Review where emotional control helped and where control became over-control.");
        plan.add("Day 7: Write one leadership rule you want to keep using when responsibility lands on you.");
        return plan;
    }

    private List<String> buildCustomWeek(SimulationContext context) {
        List<String> plan = new ArrayList<>();
        plan.add("Day 1: Rewrite your scenario in factual language: " + trimSentence(context.scenarioText(), 95));
        plan.add("Day 2: Identify the exact pressure point you keep circling mentally.");
        plan.add("Day 3: Turn that pressure point into one direct conversation, decision, or action.");
        plan.add("Day 4: Watch how your default pattern shows up once you begin acting.");
        plan.add("Day 5: Adjust the plan based on evidence, not just internal intensity.");
        plan.add("Day 6: Repeat one behavior that made the situation clearer.");
        plan.add("Day 7: Capture the lesson you want to carry into the next version of this same pattern.");
        return plan;
    }

    private String customScenarioFocus(SimulationContext context) {
        if (context.scenarioText().isBlank()) {
            return "the need to reduce uncertainty and regain traction";
        }
        return trimSentence(context.scenarioText(), 120);
    }

    private String customScenarioBlindSpot(SimulationContext context) {
        if (trait(context, "Cognitive Style") > 65) {
            return "you may analyze the wording and meaning of the situation more deeply than you act on it.";
        }
        if (trait(context, "Stress Resilience") < 42) {
            return "once the pressure rises, the situation may feel larger and more final than it really is.";
        }
        return "you may wait for perfect clarity before taking the first useful action.";
    }

    private int trait(SimulationContext context, String name) {
        return context.traits().getOrDefault(name, 50);
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String trimSentence(String text, int maxLength) {
        if (text == null || text.isBlank()) {
            return "";
        }
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
