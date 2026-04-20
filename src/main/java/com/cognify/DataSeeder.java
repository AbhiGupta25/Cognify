package com.cognify;

import com.cognify.model.entity.Question;
import com.cognify.model.entity.QuestionMapping;
import com.cognify.model.entity.User;
import com.cognify.model.enumtype.MappingTargetType;
import com.cognify.model.enumtype.QuestionType;
import com.cognify.repository.QuestionMappingRepository;
import com.cognify.repository.QuestionRepository;
import com.cognify.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               QuestionRepository questionRepository,
                               QuestionMappingRepository questionMappingRepository) {
        return args -> {
            seedUser(userRepository);

            if (questionRepository.count() > 0) {
                System.out.println("Questions already seeded. Skipping question seeding.");
                return;
            }

            // Q1
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I feel energized after spending time with a group of people.",
                    QuestionType.LIKERT,
                    1,
                    mapping(MappingTargetType.TRAIT, "Social Energy", 3, 1),
                    mapping(MappingTargetType.AXIS, "EI", 2, 1)
            );

            // Q2
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I prefer spending time alone to recharge.",
                    QuestionType.LIKERT,
                    2,
                    mapping(MappingTargetType.TRAIT, "Social Energy", 3, -1),
                    mapping(MappingTargetType.AXIS, "EI", 2, -1)
            );

            // Q3
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I actively seek opportunities to meet new people.",
                    QuestionType.LIKERT,
                    3,
                    mapping(MappingTargetType.TRAIT, "Social Energy", 3, 1),
                    mapping(MappingTargetType.AXIS, "EI", 2, 1)
            );

            // Q4
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "In social settings, I usually wait for others to approach me.",
                    QuestionType.LIKERT,
                    4,
                    mapping(MappingTargetType.TRAIT, "Social Energy", 2, -1),
                    mapping(MappingTargetType.AXIS, "EI", 2, -1)
            );

            // Q5
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I often consider how others feel before making decisions.",
                    QuestionType.LIKERT,
                    5,
                    mapping(MappingTargetType.TRAIT, "Emotional Sensitivity", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Decision Style", 2, 1),
                    mapping(MappingTargetType.AXIS, "TF", 2, 1)
            );

            // Q6
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I prioritize logic over emotions when making important decisions.",
                    QuestionType.LIKERT,
                    6,
                    mapping(MappingTargetType.TRAIT, "Decision Style", 3, -1),
                    mapping(MappingTargetType.AXIS, "TF", 2, -1)
            );

            // Q7
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I find it easy to empathize with others’ problems.",
                    QuestionType.LIKERT,
                    7,
                    mapping(MappingTargetType.TRAIT, "Emotional Sensitivity", 3, 1),
                    mapping(MappingTargetType.AXIS, "TF", 1, 1)
            );

            // Q8
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I believe decisions should be based on objective facts rather than personal values.",
                    QuestionType.LIKERT,
                    8,
                    mapping(MappingTargetType.TRAIT, "Decision Style", 3, -1),
                    mapping(MappingTargetType.AXIS, "TF", 2, -1)
            );

            // Q9
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I prefer having a clear plan before starting a task.",
                    QuestionType.LIKERT,
                    9,
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Behavioral Consistency", 2, 1),
                    mapping(MappingTargetType.AXIS, "JP", 2, 1)
            );

            // Q10
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I am comfortable adapting plans on the go.",
                    QuestionType.LIKERT,
                    10,
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 3, -1),
                    mapping(MappingTargetType.TRAIT, "Adaptability", 2, 1),
                    mapping(MappingTargetType.AXIS, "JP", 2, -1)
            );

            // Q11
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I like to finish tasks well before deadlines.",
                    QuestionType.LIKERT,
                    11,
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Behavioral Consistency", 2, 1),
                    mapping(MappingTargetType.AXIS, "JP", 2, 1)
            );

            // Q12
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I often leave things open-ended instead of making firm decisions.",
                    QuestionType.LIKERT,
                    12,
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 3, -1),
                    mapping(MappingTargetType.AXIS, "JP", 2, -1)
            );

            // Q13
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I focus more on practical details than abstract ideas.",
                    QuestionType.LIKERT,
                    13,
                    mapping(MappingTargetType.TRAIT, "Cognitive Style", 3, -1),
                    mapping(MappingTargetType.AXIS, "SN", 2, -1)
            );

            // Q14
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I enjoy thinking about possibilities and future scenarios.",
                    QuestionType.LIKERT,
                    14,
                    mapping(MappingTargetType.TRAIT, "Cognitive Style", 3, 1),
                    mapping(MappingTargetType.AXIS, "SN", 2, 1)
            );

            // Q15
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I rely on real-world experience more than imagination.",
                    QuestionType.LIKERT,
                    15,
                    mapping(MappingTargetType.TRAIT, "Cognitive Style", 3, -1),
                    mapping(MappingTargetType.AXIS, "SN", 2, -1)
            );

            // Q16
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I often think about concepts and ideas beyond immediate reality.",
                    QuestionType.LIKERT,
                    16,
                    mapping(MappingTargetType.TRAIT, "Cognitive Style", 3, 1),
                    mapping(MappingTargetType.AXIS, "SN", 2, 1)
            );

            // Q17
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I remain calm even in high-pressure situations.",
                    QuestionType.LIKERT,
                    17,
                    mapping(MappingTargetType.TRAIT, "Stress Resilience", 3, 1)
            );

            // Q18
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I get overwhelmed easily when things go wrong.",
                    QuestionType.LIKERT,
                    18,
                    mapping(MappingTargetType.TRAIT, "Stress Resilience", 3, -1)
            );

            // Q19
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I recover quickly after setbacks.",
                    QuestionType.LIKERT,
                    19,
                    mapping(MappingTargetType.TRAIT, "Stress Resilience", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Adaptability", 1, 1)
            );

            // Q20
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I tend to overthink problems and stress about outcomes.",
                    QuestionType.LIKERT,
                    20,
                    mapping(MappingTargetType.TRAIT, "Stress Resilience", 3, -1)
            );

            // Q21
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I adjust quickly to unexpected changes.",
                    QuestionType.LIKERT,
                    21,
                    mapping(MappingTargetType.TRAIT, "Adaptability", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Stress Resilience", 1, 1)
            );

            // Q22
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I find sudden changes uncomfortable.",
                    QuestionType.LIKERT,
                    22,
                    mapping(MappingTargetType.TRAIT, "Adaptability", 3, -1)
            );

            // Q23
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I enjoy trying new approaches instead of sticking to routines.",
                    QuestionType.LIKERT,
                    23,
                    mapping(MappingTargetType.TRAIT, "Adaptability", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Cognitive Style", 1, 1)
            );

            // Q24
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I prefer doing things the same way I’ve always done them.",
                    QuestionType.LIKERT,
                    24,
                    mapping(MappingTargetType.TRAIT, "Adaptability", 3, -1)
            );

            // Q25
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I follow through on commitments consistently.",
                    QuestionType.LIKERT,
                    25,
                    mapping(MappingTargetType.TRAIT, "Behavioral Consistency", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 1, 1)
            );

            // Q26
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "My productivity varies a lot depending on mood.",
                    QuestionType.LIKERT,
                    26,
                    mapping(MappingTargetType.TRAIT, "Behavioral Consistency", 3, -1),
                    mapping(MappingTargetType.TRAIT, "Stress Resilience", 1, -1)
            );

            // Q27
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I maintain routines even when I don’t feel like it.",
                    QuestionType.LIKERT,
                    27,
                    mapping(MappingTargetType.TRAIT, "Behavioral Consistency", 3, 1),
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 2, 1)
            );

            // Q28
            seedQuestion(
                    questionRepository,
                    questionMappingRepository,
                    "I often start things but don’t finish them.",
                    QuestionType.LIKERT,
                    28,
                    mapping(MappingTargetType.TRAIT, "Behavioral Consistency", 3, -1),
                    mapping(MappingTargetType.TRAIT, "Structure Orientation", 1, -1)
            );

            System.out.println("Seeded sample user, 28 questions, and mappings successfully.");
        };
    }

private void seedUser(UserRepository userRepository) {
        String email = "demo@cognify.com";
        if (userRepository.findByEmail(email).isEmpty()) {
                userRepository.save(new User("Demo User", email, "demo123"));
                System.out.println("Seeded demo user: " + email);
        }
    }

    private void seedQuestion(QuestionRepository questionRepository,
                              QuestionMappingRepository questionMappingRepository,
                              String text,
                              QuestionType type,
                              int displayOrder,
                              MappingSpec... mappingSpecs) {
        Question question = new Question(text, type, displayOrder);
        question = questionRepository.save(question);

        List<QuestionMapping> mappings = new ArrayList<>();
        for (MappingSpec spec : mappingSpecs) {
            mappings.add(new QuestionMapping(
                    question,
                    spec.targetType(),
                    spec.targetKey(),
                    spec.weight(),
                    spec.direction()
            ));
        }

        questionMappingRepository.saveAll(mappings);
    }

    private MappingSpec mapping(MappingTargetType targetType,
                                String targetKey,
                                int weight,
                                int direction) {
        return new MappingSpec(targetType, targetKey, weight, direction);
    }

    private record MappingSpec(MappingTargetType targetType,
                               String targetKey,
                               int weight,
                               int direction) {
    }
}