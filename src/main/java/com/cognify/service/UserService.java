package com.cognify.service;

import com.cognify.model.dto.UserAttemptResponse;
import com.cognify.model.dto.UserLoginRequest;
import com.cognify.model.dto.UserSignupRequest;
import com.cognify.model.dto.UserSignupResponse;
import com.cognify.model.entity.AssessmentAttempt;
import com.cognify.model.entity.User;
import com.cognify.repository.AssessmentAttemptRepository;
import com.cognify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;

    public UserService(UserRepository userRepository,
                       AssessmentAttemptRepository assessmentAttemptRepository) {
        this.userRepository = userRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
    }

    public UserSignupResponse signup(UserSignupRequest request) {
        String normalizedName = request.getName().trim();
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedPassword = request.getPassword().trim();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new RuntimeException("Email already exists. Use a different email.");
        }

        User user = new User();
        user.setName(normalizedName);
        user.setEmail(normalizedEmail);
        user.setPassword(normalizedPassword);

        User savedUser = userRepository.save(user);
        return new UserSignupResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    public UserSignupResponse login(UserLoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String password = request.getPassword().trim();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("No account found for that email."));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password.");
        }

        return new UserSignupResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public List<UserAttemptResponse> getAttemptsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return assessmentAttemptRepository.findByUserOrderByAttemptDateDesc(user).stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    private UserAttemptResponse toAttemptResponse(AssessmentAttempt attempt) {
        return new UserAttemptResponse(
                attempt.getId(),
                attempt.getAttemptDate(),
                attempt.getMbtiType(),
                expandMbti(attempt.getMbtiType()),
                attempt.getArchetype(),
                attempt.getConfidenceScore(),
                attempt.getContradictionCount(),
                attempt.getSummary()
        );
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
