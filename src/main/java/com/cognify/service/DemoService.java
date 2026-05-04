package com.cognify.service;

import com.cognify.model.dto.AssessmentResultResponse;
import com.cognify.model.dto.DemoStartResponse;
import com.cognify.model.dto.UserSignupResponse;
import com.cognify.model.entity.AssessmentAttempt;
import com.cognify.model.entity.TraitScore;
import com.cognify.model.entity.User;
import com.cognify.repository.AssessmentAttemptRepository;
import com.cognify.repository.TraitScoreRepository;
import com.cognify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DemoService {

    private static final String DEMO_EMAIL = "judge.demo@cognify.app";
    private static final String DEMO_NAME = "Judge Demo";
    private static final String DEMO_PASSWORD = "judge-demo";

    private final UserRepository userRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final TraitScoreRepository traitScoreRepository;
    private final AssessmentService assessmentService;

    public DemoService(UserRepository userRepository,
                       AssessmentAttemptRepository assessmentAttemptRepository,
                       TraitScoreRepository traitScoreRepository,
                       AssessmentService assessmentService) {
        this.userRepository = userRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.traitScoreRepository = traitScoreRepository;
        this.assessmentService = assessmentService;
    }

    @Transactional
    public DemoStartResponse startDemo() {
        User demoUser = userRepository.findByEmail(DEMO_EMAIL)
                .orElseGet(this::createDemoUser);

        AssessmentAttempt demoAttempt = findReusableDemoAttempt(demoUser);
        if (demoAttempt == null) {
            demoAttempt = createDemoAttempt(demoUser);
        }

        AssessmentResultResponse result = assessmentService.getAttemptResult(demoAttempt.getId(), demoUser.getId());
        UserSignupResponse user = new UserSignupResponse(
                demoUser.getId(),
                demoUser.getName(),
                demoUser.getEmail(),
                demoUser.getCreatedAt()
        );

        return new DemoStartResponse(user, result);
    }

    private User createDemoUser() {
        User user = new User(DEMO_NAME, DEMO_EMAIL, DEMO_PASSWORD);
        return userRepository.save(user);
    }

    private AssessmentAttempt findReusableDemoAttempt(User demoUser) {
        return assessmentAttemptRepository.findByUserOrderByAttemptDateDesc(demoUser).stream()
                .filter(this::isUsableDemoAttempt)
                .findFirst()
                .orElse(null);
    }

    private boolean isUsableDemoAttempt(AssessmentAttempt attempt) {
        return attempt.getMbtiType() != null
                && attempt.getArchetype() != null
                && attempt.getConfidenceScore() != null
                && attempt.getContradictionCount() != null
                && attempt.getSummary() != null
                && attempt.getCoreProfile() != null
                && attempt.getDecisionPattern() != null
                && attempt.getSocialPattern() != null
                && attempt.getStressPattern() != null
                && attempt.getBehavioralPattern() != null
                && attempt.getCognitivePattern() != null
                && attempt.getAdaptivePattern() != null
                && attempt.getContradictionAnalysis() != null
                && attempt.getTraitScores() != null
                && attempt.getTraitScores().size() >= 8;
    }

    private AssessmentAttempt createDemoAttempt(User user) {
        AssessmentAttempt attempt = new AssessmentAttempt(user);
        attempt.setMbtiType("ENTJ");
        attempt.setArchetype("Balanced Integrator");
        attempt.setConfidenceScore(82);
        attempt.setContradictionCount(1);
        attempt.setSummary("Closest alignment: ENTJ (Extroverted, Intuitive, Thinking, Judging). Cognify archetype: Balanced Integrator. This demo profile reads as ambitious, fast-patterning, and strategically composed under visible pressure, with one small contradiction marker showing flexibility rather than instability.");
        attempt.setCoreProfile("This profile combines decisive outward momentum with strong pattern recognition and a practical appetite for execution. The person behind it tends to move quickly from idea to structure, prefers influence over passivity, and usually feels most alive when complex situations can be organized into a plan with visible traction.");
        attempt.setDecisionPattern("Decision-making here is crisp, strategic, and future-oriented. The profile tends to judge ideas by leverage, logic, and downstream consequences, while still understanding that people need clarity and confidence from a leader before they can execute well.");
        attempt.setSocialPattern("Socially, this profile is not passive. It tends to enter rooms with initiative, speak with direction, and pull conversations toward outcomes. The strength is momentum. The risk is coming across as already decided before everyone else has fully caught up.");
        attempt.setStressPattern("Under pressure, this profile usually stays functional and externally composed. Stress does not disappear, but it often gets translated into tighter structure, faster prioritization, and more visible control. The watch-out is over-carrying responsibility instead of distributing it.");
        attempt.setBehavioralPattern("Behaviorally, this profile reads as reliable when stakes are visible. Once a target matters, the person usually builds a sequence, assigns effort, and follows through. They are more likely to struggle with impatience toward drift than with lack of motivation.");
        attempt.setCognitivePattern("Cognitively, this is a pattern-first profile. It tends to notice leverage, systems, and future implications early, then translate abstract insight into operational language. That makes it strong in leadership, strategy, and high-pressure explanation.");
        attempt.setAdaptivePattern("Adaptability is present, but not chaotic. This profile can pivot when evidence changes, especially if the pivot still protects momentum and competence. It does not enjoy aimlessness, but it can reframe quickly when a better path becomes visible.");
        attempt.setContradictionAnalysis("One contradiction marker suggests a nuanced profile rather than an unstable one. The person may combine confidence with occasional overextension, or flexibility with a strong preference for control, depending on the demands of the environment.");

        AssessmentAttempt savedAttempt = assessmentAttemptRepository.save(attempt);
        saveDemoTraitScores(savedAttempt);
        return savedAttempt;
    }

    private void saveDemoTraitScores(AssessmentAttempt attempt) {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Social Energy", 78);
        scores.put("Emotional Sensitivity", 58);
        scores.put("Decision Style", 34);
        scores.put("Structure Orientation", 84);
        scores.put("Cognitive Style", 81);
        scores.put("Stress Resilience", 73);
        scores.put("Adaptability", 62);
        scores.put("Behavioral Consistency", 76);

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            traitScoreRepository.save(new TraitScore(attempt, entry.getKey(), entry.getValue()));
        }
    }
}
